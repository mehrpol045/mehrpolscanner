package scanner

import (
	"context"
	"fmt"
	"net"
	"strconv"
	"time"

	"github.com/matinsenpai/mehrpol/internal/xraytest"
)

// ProxyConfig represents the parsed proxy configuration for supported protocols.
type ProxyConfig struct {
	Protocol    string
	Address     string
	Port        int
	UserID      string
	Password    string
	Method      string
	Network     string
	TLS         bool
	SNI         string
	Path        string
	Host        string
	Fingerprint string

	xray *xraytest.VLESSConfig
}

// ParseProxyURL parses a VLESS, Trojan, or VMess share URL.
func ParseProxyURL(rawURL string) (*ProxyConfig, error) {
	cfg, err := xraytest.ParseProxyURL(rawURL)
	if err != nil {
		return nil, err
	}
	out := &ProxyConfig{
		Protocol:    cfg.Protocol,
		Address:     cfg.Address,
		Port:        cfg.Port,
		UserID:      cfg.UUID,
		Password:    cfg.Password,
		Network:     cfg.Network,
		TLS:         cfg.Security == "tls",
		SNI:         cfg.SNI,
		Path:        cfg.Path,
		Host:        cfg.Host,
		Fingerprint: cfg.Fingerprint,
		xray:        cfg,
	}
	if out.UserID == "" {
		out.UserID = cfg.Password
	}
	if out.Port == 0 {
		out.Port = 443
	}
	return out, nil
}

// Validator handles end-to-end proxy validation using the shared xraytest runner.
type Validator struct {
	cfg *ProxyConfig
}

// NewValidator creates a new Validator instance.
func NewValidator() *Validator { return &Validator{} }

// SetupXrayClient stores the proxy configuration for validation.
// The shared xraytest runner creates and tears down embedded Xray per validation,
// which avoids stale long-lived instances and keeps this compatibility package small.
func (v *Validator) SetupXrayClient(cfg *ProxyConfig) (func(), error) {
	if cfg == nil {
		return nil, fmt.Errorf("nil proxy config")
	}
	if cfg.xray == nil {
		cfg.xray = &xraytest.VLESSConfig{
			Protocol:    cfg.Protocol,
			Address:     cfg.Address,
			Port:        cfg.Port,
			UUID:        cfg.UserID,
			Password:    cfg.Password,
			Network:     cfg.Network,
			Security:    securityName(cfg.TLS),
			SNI:         cfg.SNI,
			Path:        cfg.Path,
			Host:        cfg.Host,
			Fingerprint: cfg.Fingerprint,
		}
	}
	v.cfg = cfg
	return func() { v.cfg = nil }, nil
}

// Validate performs a connectivity check against targetIP:port.
func (v *Validator) Validate(ctx context.Context, targetIP net.IP, port int) (time.Duration, error) {
	if v.cfg == nil {
		return 0, fmt.Errorf("Xray client not set up; call SetupXrayClient first")
	}
	if targetIP == nil {
		return 0, fmt.Errorf("nil target IP")
	}
	if port <= 0 {
		port = v.cfg.Port
	}
	if v.cfg.xray != nil && (v.cfg.Protocol == "vless" || v.cfg.Protocol == "trojan" || v.cfg.Protocol == "vmess") {
		res := xraytest.ValidateConfig(ctx, v.cfg.xray.WithEndpoint(targetIP.String(), port), 30*time.Second)
		if res.Success {
			return res.Latency, nil
		}
		if res.Latency > 0 {
			return res.Latency, fmt.Errorf("%s", res.Error)
		}
		return 0, fmt.Errorf("%s", res.Error)
	}

	addr := net.JoinHostPort(targetIP.String(), strconv.Itoa(port))
	dialer := &net.Dialer{Timeout: 10 * time.Second}
	start := time.Now()
	conn, err := dialer.DialContext(ctx, "tcp", addr)
	if err != nil {
		return 0, err
	}
	_ = conn.Close()
	return time.Since(start), nil
}

func securityName(tls bool) string {
	if tls {
		return "tls"
	}
	return "none"
}
