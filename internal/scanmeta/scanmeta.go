package scanmeta

import (
	"bufio"
	"net"
	"os"
	"path/filepath"
	"strconv"
	"strings"

	"github.com/matinsenpai/mehrpol/internal/result"
)

const (
	CloudflareASN = 13335
	CloudflareOrg = "Cloudflare, Inc."
)

var cloudflareCIDRs = []string{
	"173.245.48.0/20", "103.21.244.0/22", "103.22.200.0/22", "103.31.4.0/22",
	"141.101.64.0/18", "108.162.192.0/18", "190.93.240.0/20", "188.114.96.0/20",
	"197.234.240.0/22", "198.41.128.0/17", "162.158.0.0/15", "104.16.0.0/13",
	"104.24.0.0/14", "172.64.0.0/13", "131.0.72.0/22",
}

var cfNets []*net.IPNet

func init() {
	for _, cidr := range cloudflareCIDRs {
		_, n, err := net.ParseCIDR(cidr)
		if err == nil {
			cfNets = append(cfNets, n)
		}
	}
}

type BlockList struct{ nets []*net.IPNet }

func LoadDefaultBlockList() BlockList {
	paths := []string{}
	if env := strings.TrimSpace(os.Getenv("MEHRPOL_IR_BLOCKLIST")); env != "" {
		paths = append(paths, strings.Split(env, string(os.PathListSeparator))...)
	}
	if wd, err := os.Getwd(); err == nil {
		paths = append(paths, filepath.Join(wd, "blocked_ips_ir.txt"), filepath.Join(wd, "iran_blocked_ips.txt"))
	}
	if cfg, err := os.UserConfigDir(); err == nil {
		paths = append(paths, filepath.Join(cfg, "mehrpol", "blocked_ips_ir.txt"))
	}
	return LoadBlockList(paths...)
}

func LoadBlockList(paths ...string) BlockList {
	var out BlockList
	seen := map[string]struct{}{}
	for _, path := range paths {
		path = strings.TrimSpace(path)
		if path == "" {
			continue
		}
		if _, ok := seen[path]; ok {
			continue
		}
		seen[path] = struct{}{}
		f, err := os.Open(path)
		if err != nil {
			continue
		}
		sc := bufio.NewScanner(f)
		for sc.Scan() {
			line := strings.TrimSpace(sc.Text())
			if line == "" || strings.HasPrefix(line, "#") {
				continue
			}
			if strings.Contains(line, "/") {
				if _, n, err := net.ParseCIDR(line); err == nil {
					out.nets = append(out.nets, n)
				}
				continue
			}
			if ip := net.ParseIP(line); ip != nil {
				bits := 32
				if ip.To4() == nil {
					bits = 128
				}
				out.nets = append(out.nets, &net.IPNet{IP: ip, Mask: net.CIDRMask(bits, bits)})
			}
		}
		_ = f.Close()
	}
	return out
}

func (b BlockList) Contains(ip net.IP) bool {
	for _, n := range b.nets {
		if n.Contains(ip) {
			return true
		}
	}
	return false
}

func Enrich(r *result.Result, blocked BlockList) {
	if r == nil {
		return
	}
	if r.CDNStatus == "" {
		if r.Colo != "" || isCloudflareIP(r.IP) {
			r.CDNStatus = "cloudflare"
		} else {
			r.CDNStatus = "unknown"
		}
	}
	if isCloudflareIP(r.IP) {
		r.ASN = CloudflareASN
		r.ASNOrg = CloudflareOrg
	}
	if r.Country == "" {
		r.Country = CountryForColo(r.Colo)
	}
	r.BlockedIR = blocked.Contains(r.IP)
}

func PassesFilters(r *result.Result, countryFilter, asnFilter string) bool {
	if r == nil {
		return false
	}
	countryFilter = strings.TrimSpace(strings.ToUpper(countryFilter))
	asnFilter = strings.TrimSpace(strings.ToUpper(asnFilter))
	if countryFilter != "" {
		allowed := false
		for _, part := range strings.Split(countryFilter, ",") {
			if strings.TrimSpace(strings.ToUpper(part)) == strings.ToUpper(r.Country) {
				allowed = true
				break
			}
		}
		if !allowed {
			return false
		}
	}
	if asnFilter != "" {
		allowed := false
		asn := strconv.Itoa(r.ASN)
		for _, part := range strings.Split(asnFilter, ",") {
			p := strings.TrimSpace(strings.TrimPrefix(strings.ToUpper(part), "AS"))
			if p == asn || (r.ASNOrg != "" && strings.Contains(strings.ToUpper(r.ASNOrg), p)) {
				allowed = true
				break
			}
		}
		if !allowed {
			return false
		}
	}
	return true
}

func isCloudflareIP(ip net.IP) bool {
	for _, n := range cfNets {
		if n.Contains(ip) {
			return true
		}
	}
	return false
}

func CountryForColo(colo string) string {
	colo = strings.ToUpper(strings.TrimSpace(colo))
	if colo == "" {
		return ""
	}
	if c, ok := coloCountry[colo]; ok {
		return c
	}
	return "Global"
}

var coloCountry = map[string]string{
	"AMS":"NL", "ARN":"SE", "ATH":"GR", "ATL":"US", "BOM":"IN", "BOS":"US", "CDG":"FR",
	"CGK":"ID", "CPH":"DK", "DME":"RU", "DOH":"QA", "DUB":"IE", "DUS":"DE", "EWR":"US",
	"FCO":"IT", "FRA":"DE", "GRU":"BR", "HEL":"FI", "HKG":"HK", "IAD":"US", "ICN":"KR",
	"IST":"TR", "JFK":"US", "KUL":"MY", "LAX":"US", "LHR":"GB", "MAD":"ES", "MAN":"GB",
	"MRS":"FR", "MXP":"IT", "NRT":"JP", "ORD":"US", "OSL":"NO", "OTP":"RO", "PRG":"CZ",
	"SFO":"US", "SIN":"SG", "SJC":"US", "SYD":"AU", "TLV":"IL", "VIE":"AT", "WAW":"PL", "YYZ":"CA",
}
