package engine

import (
	"context"
	"net"
	"sync"
	"sync/atomic"
	"time"

	"golang.org/x/time/rate"

	"github.com/matinsenpai/mehrpol/internal/prober"
	"github.com/matinsenpai/mehrpol/internal/result"
)

// Config controls engine behaviour.
type Config struct {
	Concurrency int
	RateLimit   float64 // probes per second, <=0 means unlimited
	ProbeConfig prober.Config
}

// Stats exposes real-time counters.
type Stats struct {
	Tested   atomic.Int64
	Healthy  atomic.Int64
	Failed   atomic.Int64
	InFlight atomic.Int64
}

// ResultFunc is called for every completed probe result. It is invoked from
// worker goroutines, so implementations must be goroutine-safe.
type ResultFunc func(*result.Result)

// Engine orchestrates a pool of prober goroutines.
type Engine struct {
	cfg     Config
	stats   Stats
	limiter *rate.Limiter
}

// New creates a new Engine.
func New(cfg Config) *Engine {
	var lim *rate.Limiter
	if cfg.RateLimit > 0 {
		lim = rate.NewLimiter(rate.Limit(cfg.RateLimit), int(cfg.RateLimit)+1)
	}
	if cfg.Concurrency <= 0 {
		cfg.Concurrency = 100
	}
	return &Engine{cfg: cfg, limiter: lim}
}

// Stats returns a pointer to the live statistics.
func (e *Engine) Stats() *Stats {
	return &e.stats
}

// Run consumes IPs from src, probes each one, and forwards results to fn.
// It uses a fixed worker pool to avoid per-IP goroutine churn during large scans.
func (e *Engine) Run(ctx context.Context, src <-chan net.IP, fn ResultFunc) {
	workers := e.cfg.Concurrency
	jobs := make(chan net.IP, workers*2)
	var wg sync.WaitGroup

	for i := 0; i < workers; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			for ip := range jobs {
				if ctx.Err() != nil {
					continue
				}
				e.stats.InFlight.Add(1)
				r := prober.Probe(ctx, ip, e.cfg.ProbeConfig)
				e.stats.InFlight.Add(-1)
				e.stats.Tested.Add(1)
				if r.IsHealthy() {
					e.stats.Healthy.Add(1)
				} else {
					e.stats.Failed.Add(1)
				}
				fn(r)
			}
		}()
	}

	defer func() {
		close(jobs)
		wg.Wait()
	}()

	for {
		select {
		case <-ctx.Done():
			return
		case ip, ok := <-src:
			if !ok {
				return
			}
			if e.limiter != nil {
				if err := e.limiter.Wait(ctx); err != nil {
					return
				}
			}
			select {
			case jobs <- ip:
			case <-ctx.Done():
				return
			}
		}
	}
}

// RunList probes a fixed slice of IPs (used in `mehrpol test`).
func (e *Engine) RunList(ctx context.Context, ips []net.IP, fn ResultFunc) {
	ch := make(chan net.IP, len(ips))
	for _, ip := range ips {
		ch <- ip
	}
	close(ch)

	// Raise the timeout floor for the final validation round so slow IPs
	// still get a fair chance rather than being cut off too early.
	cfg := e.cfg
	cfg.ProbeConfig.Timeout = max(cfg.ProbeConfig.Timeout, 10*time.Second)
	e2 := New(cfg)
	e2.Run(ctx, ch, fn)
}
