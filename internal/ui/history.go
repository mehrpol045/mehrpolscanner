package ui

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"time"

	"github.com/matinsenpai/mehrpol/internal/result"
	"github.com/matinsenpai/mehrpol/internal/xraytest"
)

type scanHistoryEntry struct {
	ID        string                 `json:"id"`
	StartedAt time.Time              `json:"started_at"`
	Phase1    []scanHistoryPhase1Row `json:"phase1"`
	Phase2    []scanHistoryPhase2Row `json:"phase2,omitempty"`
}

type scanHistoryPhase1Row struct {
	Endpoint   string    `json:"endpoint"`
	IP         string    `json:"ip"`
	Port       int       `json:"port"`
	LossPct    float64   `json:"loss_pct"`
	AvgMs      int64     `json:"avg_ms"`
	JitterMs   int64     `json:"jitter_ms"`
	Country    string    `json:"country,omitempty"`
	ASN        int       `json:"asn,omitempty"`
	ASNOrg     string    `json:"asn_org,omitempty"`
	Colo       string    `json:"colo,omitempty"`
	CDNStatus  string    `json:"cdn_status,omitempty"`
	BlockedIR  bool      `json:"blocked_ir,omitempty"`
	BestSNI    string    `json:"best_sni,omitempty"`
	CertCN     string    `json:"cert_cn,omitempty"`
	CertIssuer string    `json:"cert_issuer,omitempty"`
	CertExpiry time.Time `json:"cert_expiry,omitempty"`
	Throughput float64   `json:"throughput,omitempty"`
}

type scanHistoryPhase2Row struct {
	Endpoint   string  `json:"endpoint"`
	IP         string  `json:"ip"`
	Port       int     `json:"port"`
	Transport  string  `json:"transport"`
	Success    bool    `json:"success"`
	LatencyMs  int64   `json:"latency_ms,omitempty"`
	Throughput float64 `json:"throughput,omitempty"`
	Error      string  `json:"error,omitempty"`
}

type scanComparison struct {
	New      []string
	Removed  []string
	Kept     []string
	Previous string
}

func historyDir() string {
	if dir, err := os.UserConfigDir(); err == nil {
		path := filepath.Join(dir, "mehrpol", "history")
		_ = os.MkdirAll(path, 0755)
		return path
	}
	path := filepath.Join(".", ".mehrpol-history")
	_ = os.MkdirAll(path, 0755)
	return path
}

func saveScanHistory(phase1 []*result.Result, phase2 []*xraytest.ValidationResult) (string, error) {
	entry := scanHistoryEntry{ID: time.Now().Format("20060102-150405"), StartedAt: time.Now()}
	for _, r := range phase1 {
		if r == nil || !r.IsHealthy() {
			continue
		}
		entry.Phase1 = append(entry.Phase1, scanHistoryPhase1Row{
			Endpoint:   formatEndpoint(r.IP.String(), r.Port),
			IP:         r.IP.String(),
			Port:       r.Port,
			LossPct:    r.Loss(),
			AvgMs:      r.Avg().Milliseconds(),
			JitterMs:   r.Jitter().Milliseconds(),
			Country:    r.Country,
			ASN:        r.ASN,
			ASNOrg:     r.ASNOrg,
			Colo:       r.Colo,
			CDNStatus:  r.CDNStatus,
			BlockedIR:  r.BlockedIR,
			BestSNI:    r.BestSNI,
			CertCN:     r.CertCN,
			CertIssuer: r.CertIssuer,
			CertExpiry: r.CertExpiry,
			Throughput: r.Throughput,
		})
	}
	for _, r := range phase2 {
		if r == nil {
			continue
		}
		entry.Phase2 = append(entry.Phase2, scanHistoryPhase2Row{
			Endpoint:   formatEndpoint(r.IP, r.Port),
			IP:         r.IP,
			Port:       r.Port,
			Transport:  r.Transport,
			Success:    r.Success,
			LatencyMs:  r.Latency.Milliseconds(),
			Throughput: r.Throughput,
			Error:      r.Error,
		})
	}
	b, err := json.MarshalIndent(entry, "", "  ")
	if err != nil {
		return "", err
	}
	path := filepath.Join(historyDir(), entry.ID+".json")
	return path, os.WriteFile(path, b, 0644)
}

func loadScanHistory() ([]scanHistoryEntry, error) {
	files, err := filepath.Glob(filepath.Join(historyDir(), "*.json"))
	if err != nil {
		return nil, err
	}
	sort.Strings(files)
	var entries []scanHistoryEntry
	for _, path := range files {
		b, err := os.ReadFile(path)
		if err != nil {
			continue
		}
		var entry scanHistoryEntry
		if err := json.Unmarshal(b, &entry); err == nil {
			entries = append(entries, entry)
		}
	}
	return entries, nil
}

func compareWithPreviousScan(current []*result.Result) scanComparison {
	entries, _ := loadScanHistory()
	if len(entries) == 0 {
		return scanComparison{}
	}
	prev := entries[len(entries)-1]
	prevSet := map[string]struct{}{}
	for _, r := range prev.Phase1 {
		prevSet[r.Endpoint] = struct{}{}
	}
	curSet := map[string]struct{}{}
	for _, r := range current {
		if r != nil && r.IsHealthy() {
			curSet[formatEndpoint(r.IP.String(), r.Port)] = struct{}{}
		}
	}
	cmp := scanComparison{Previous: prev.ID}
	for ep := range curSet {
		if _, ok := prevSet[ep]; ok {
			cmp.Kept = append(cmp.Kept, ep)
		} else {
			cmp.New = append(cmp.New, ep)
		}
	}
	for ep := range prevSet {
		if _, ok := curSet[ep]; !ok {
			cmp.Removed = append(cmp.Removed, ep)
		}
	}
	sort.Strings(cmp.New)
	sort.Strings(cmp.Kept)
	sort.Strings(cmp.Removed)
	return cmp
}

func summarizeComparison(c scanComparison) string {
	if c.Previous == "" {
		return "history saved; no previous scan to compare"
	}
	return fmt.Sprintf("history saved; vs %s: +%d new, -%d removed, %d unchanged", c.Previous, len(c.New), len(c.Removed), len(c.Kept))
}

func historySummaryLines(limit int) []string {
	entries, _ := loadScanHistory()
	if len(entries) == 0 {
		return []string{"No scan history yet."}
	}
	if limit <= 0 || limit > len(entries) {
		limit = len(entries)
	}
	entries = entries[len(entries)-limit:]
	lines := make([]string, 0, len(entries))
	for i := len(entries) - 1; i >= 0; i-- {
		e := entries[i]
		lines = append(lines, fmt.Sprintf("%s  phase1=%d  phase2=%d", e.ID, len(e.Phase1), len(e.Phase2)))
	}
	return lines
}

func historyLatestConfigLinks(rawURL string, phase1 []*result.Result) string {
	cfg, err := xraytest.ParseProxyURL(rawURL)
	if err != nil || cfg == nil {
		return ""
	}
	var urls []string
	for _, r := range result.TopN(phase1, 20) {
		if r == nil || !r.IsHealthy() {
			continue
		}
		urls = append(urls, cfg.WithEndpoint(r.IP.String(), r.Port).ToShareURL())
	}
	return strings.Join(urls, "\n")
}
