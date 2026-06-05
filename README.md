# SenPai Scanner

> **Persian / فارسی:** [README.fa.md](README.fa.md)

[![CI](https://github.com/matinsenpai/senpaiscanner/actions/workflows/ci.yml/badge.svg)](https://github.com/matinsenpai/senpaiscanner/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/matinsenpai/senpaiscanner?style=flat-square)](https://github.com/matinsenpai/senpaiscanner/releases/latest)
[![Go Version](https://img.shields.io/github/go-mod/go-version/matinsenpai/senpaiscanner?style=flat-square)](go.mod)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](LICENSE)
[![Platforms](https://img.shields.io/badge/platform-linux%20%7C%20macOS%20%7C%20windows%20%7C%20android%20%7C%20termux-informational?style=flat-square)](#installation)

A Cloudflare IP finder with a terminal UI and an Android app, built for networks where latency is unpredictable and connections drop without warning. Probe Cloudflare edge IPs, optionally validate them through your VLESS or Trojan config with embedded xray — no commands to memorize.

---

## How it works

Run `senpaiscanner` and you land in a short menu. Navigate with arrow keys and Enter — no scan-related CLI flags.

```
┌────────────────────────────────────────────────────────────┐
│  ▶  Find Working IPs   scan Cloudflare IPs — config optional │
│     Retry Last Scan    retry last scan with previous config  │
│     About                                                │
│     Quit                                                 │
└────────────────────────────────────────────────────────────┘
```

**Find Working IPs** can run in one or two phases:

1. **Phase 1 — Connectivity scan** probes candidate Cloudflare IPs. Without a config URL it uses a standard HTTP probe; with a URL it derives SNI, host, WebSocket path, and port from your link. In **Random** mode, healthy hits also trigger a **neighbor scan** — nearby addresses in the same Cloudflare block are explored automatically.
2. **Phase 2 — xray validation** (optional) launches an embedded xray instance and tests the best Phase 1 hits end-to-end through your actual VLESS/Trojan config. Results show endpoint, transport type, download speed, latency (TTFB), and pass/fail status.

Press **`c`** when a scan finishes to copy working `IP:port` endpoints to the clipboard and save them to `ips.txt` next to the binary (or current working directory).

Your last scan settings are saved automatically. Use **Retry Last Scan** on the home screen to repeat the previous run without re-entering anything.

---

## Installation

### Desktop — pre-built binary

Download from the [releases page](https://github.com/matinsenpai/senpaiscanner/releases/latest).

| Platform | Architecture | File |
|---|---|---|
| Linux | x86_64 | `senpaiscanner-linux-amd64` |
| Linux | ARM64 | `senpaiscanner-linux-arm64` |
| Linux | 32-bit x86 | `senpaiscanner-linux-386` |
| macOS | Intel | `senpaiscanner-darwin-amd64` |
| macOS | Apple Silicon | `senpaiscanner-darwin-arm64` |
| Windows | x86_64 | `senpaiscanner-windows-amd64.exe` |
| Windows | 32-bit x86 | `senpaiscanner-windows-386.exe` |

**Linux / macOS:**

```bash
# stable release
curl -fsSL https://github.com/MatinSenPai/SenPaiScanner/raw/refs/heads/main/install.sh | bash

# pre-release
curl -fsSL https://github.com/MatinSenPai/SenPaiScanner/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

**Windows (PowerShell):**

```powershell
$r = Invoke-RestMethod https://api.github.com/repos/matinsenpai/senpaiscanner/releases/latest
$url = ($r.assets | Where-Object name -eq "senpaiscanner-windows-amd64.exe").browser_download_url
Invoke-WebRequest $url -OutFile senpaiscanner.exe
```

### Android — pre-built APK

Signed release APKs are attached to each GitHub release:

| File pattern | Description |
|---|---|
| `SenPaiScanner-{version}-universal-release.apk` | All ABIs (recommended) |
| `SenPaiScanner-{version}-arm64-v8a-release.apk` | 64-bit ARM only |
| `SenPaiScanner-{version}-armeabi-v7a-release.apk` | 32-bit ARM only |

Install the APK on your device (enable “Install from unknown sources” if needed), grant network permission, and tap **START SCAN** on the home screen.

### Termux (Android terminal)

Run the full desktop TUI inside [Termux](https://termux.dev/) — same workflow as Linux, including Phase 2 xray validation, persistent config, live results, and neighbor scan.

**1. Install Termux** from [F-Droid](https://f-droid.org/en/packages/com.termux/) (not the Play Store build). Open the app and run:

```bash
pkg update && pkg upgrade -y
pkg install curl tar -y
```

**2. Install SenPai Scanner** (auto-detects Termux and installs to `$PREFIX/bin`):

```bash
curl -fsSL https://github.com/MatinSenPai/SenPaiScanner/raw/refs/heads/main/install.sh | bash
```

Pre-release channel:

```bash
curl -fsSL https://github.com/MatinSenPai/SenPaiScanner/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

The installer downloads `senpaiscanner-linux-arm64` on 64-bit phones. (32-bit ARM devices are uncommon; use the native APK if the Linux binary is unavailable.)

**3. Run:**

```bash
senpaiscanner
```

**Termux tips**

| Topic | Notes |
|---|---|
| **Navigation** | Arrow keys on the on-screen keyboard, or a Bluetooth keyboard. `k` / `j` / `h` / `l` also work in menus. |
| **Paste config URL** | Long-press in Termux → Paste, or `termux-clipboard-get` if `termux-api` is installed. |
| **Clipboard (`c` key)** | May not work in all Termux setups. Results are always saved to `ips.txt` in the current directory when copy runs — use that file if clipboard fails. |
| **`ips.txt` / live results** | Keep files in `~/` (e.g. `cd ~` before starting). Paths shown in the TUI are relative to the working directory. |
| **Config file** | `~/.config/senpaiscanner/config.json` — powers **Retry Last Scan**. |
| **Long scans** | Optional: `termux-wake-lock` (from `pkg install termux-api`) to reduce the screen turning off mid-scan. |
| **Update** | Re-run the `install.sh` one-liner; it upgrades when a newer release is available. |

**Manual install** (without the script):

```bash
curl -fsSL -o "$PREFIX/bin/senpaiscanner" \
  https://github.com/matinsenpai/senpaiscanner/releases/latest/download/senpaiscanner-linux-arm64
chmod +x "$PREFIX/bin/senpaiscanner"
senpaiscanner
```

Prefer the **native APK** (above) if you want a touch UI without the terminal. Use **Termux** if you want the full desktop TUI and live results file on your phone.

### From source

```bash
go install github.com/matinsenpai/senpaiscanner/cmd/senpaiscanner@latest
```

---

## Usage

```bash
senpaiscanner              # open the TUI
senpaiscanner --version    # print version and exit
senpaiscanner -v           # same
senpaiscanner version      # same
```

Everything else is inside the TUI or Android app — there are no scan-related CLI flags.

### Navigation (desktop TUI)

| Key | Action |
|-----|--------|
| `↑` / `↓` or `k` / `j` | move between rows |
| `←` / `→` or `h` / `l` | move between options within a row |
| `Enter` | select / confirm / start |
| `Esc` | go back |
| `q` | quit from menu; during a scan, cancel or return to menu when finished |

On the **Config URL** row, `←` / `→` move the text cursor; `Ctrl+A` / `Ctrl+E` jump to start / end. Vim keys `h` / `j` / `k` / `l` type normally into the URL field on that row.

---

## Find Working IPs (desktop)

### Step 1 — Scan setup

| Row | Options | Notes |
|---|---|---|
| **Source** | Random / From File | random Cloudflare IPv4 ranges, or candidates from `ips.txt` |
| **Count** | 1,000 / 5,000 / 20,000 / Custom | IPs to probe in Phase 1 (Random); caps how many entries from `ips.txt` are used (From File) |
| **Workers** | 50 / 100 / 200 / Custom | parallel probers (default 50 — safe on restricted networks) |
| **Timeout** | 2s / 3s / 5s / Custom | per-probe deadline (default 5s) |
| **Ports** | Config, 443, 8443, 2053, 2083, 2087, 2096 | multi-select; each IP is tested on every selected port |

Press **Enter** on **Ports** to continue to the optional config step.

**Ports row:** use `←` / `→` to focus a port pill, then **`Space`** or **`Enter`** to toggle it. Select **Config** alone to use the port from your URL. Selecting multiple ports multiplies Phase 1 work (IPs × ports).

### Step 2 — Optional config

| Row | Options | Notes |
|---|---|---|
| **Config** | paste URL or leave empty | empty → **Phase 1 only**; with URL → Phase 1 + Phase 2 |
| **Top N** | 10 / 25 / 50 / 100 / All / Custom | how many Phase 1 hits to validate in Phase 2 (only when a config URL is set) |

Supported share links: **`vless://`** and **`trojan://`**. Parsing accepts common real-world quirks — case-insensitive schemes, missing `?` after the port, IPv6 hosts in brackets, and URL-encoded Trojan passwords.

**Enter** with an empty config field starts a connectivity-only scan. Paste a URL, set **Top N**, then **Enter** again to run full validation.

Invalid URLs show an inline warning and keep focus on the Config row.

### Persistent settings & Retry Last Scan

Every scan start saves your current setup (source, count, workers, timeout, ports, config URL, Top N) to a config file:

| Platform | Path |
|---|---|
| Windows | `%AppData%\senpaiscanner\config.json` |
| macOS | `~/Library/Application Support/senpaiscanner/config.json` |
| Linux | `~/.config/senpaiscanner/config.json` |

The home screen shows this path. **Retry Last Scan** loads the saved values and starts immediately — useful for overnight re-runs or after tweaking `ips.txt`.

### Live results file

During a scan the UI shows `live results → SenPaiScannerResult-YYYYMMDD-HHMMSS.txt` beside the binary or in the working directory.

- The file is **not created until the first healthy Phase 1 result** — no empty placeholder files.
- It is rewritten on each update so you can tail it in any editor while the scan runs.
- Sections: scan plan, Phase 1 table (healthy hits), Phase 2 table (when applicable).

### Phase 1 — Finding reachable IPs

| Mode | Probe behaviour |
|---|---|
| **No config URL** | Standard Cloudflare HTTP probe (`speed.cloudflare.com`, 64 KiB sample) |
| **With config URL** | SNI / host / path from your link; WebSocket upgrade required when `type=ws` |

**Random source only:** when a healthy IP is found, the scanner also probes nearby addresses in the same Cloudflare block (±1, ±2, … up to radius 32). Defaults: up to 12 neighbors per hit, 400 extra IPs total across the scan. This is automatic — no UI toggle.

The live table shows the top 20 results: **ENDPOINT**, **LOSS**, **AVG(ms)**, **COLO**, **STATUS** (✓/✗). Progress **target** = Count × selected ports (or capped `ips.txt` entries × ports).

Press `q` / `Esc` to cancel. When Phase 1-only finishes, press **`c`** to copy healthy endpoints.

### Phase 2 — xray validation

The top Phase 1 candidates (by average latency) are tested through embedded xray with your config:

| Column | Meaning |
|---|---|
| **ENDPOINT** | `IP:port` that was validated |
| **TYPE** | transport (`ws`, `grpc`, `xhttp`, …) |
| **SPEED** | measured download throughput in Mbps, or `n/a` if speed could not be measured |
| **LATENCY** | time to first byte through the proxy (TTFB) |
| **STATUS** | ✓ working / ✗ failed |

Connectivity is checked via Cloudflare `/cdn-cgi/trace` (`cp.cloudflare.com` first, then `cloudflare.com` as fallback). Speed measurement is best-effort; an endpoint can pass with SPEED `n/a`. Each candidate gets one automatic retry on failure.

| Key | Action |
|-----|--------|
| `c` | copy working endpoints to clipboard **and** save to `ips.txt` |
| `q` / `Esc` | return to the main menu |

Exported lines look like `104.16.72.162:443` — ready to paste into client configs or DNS/IP lists.

### About

Version string and short project blurb; `Enter` / `q` / `Esc` back to the menu.

---

## `ips.txt` format (From File)

Place `ips.txt` next to the executable or in the directory you run from. The scanner searches the working directory first, then the binary directory.

| Line type | Example | Behaviour |
|---|---|---|
| Plain IPv4 | `104.16.72.162` | Loaded |
| CSV | `104.16.72.162,note` | First column used |
| Comment / blank | `# my list` | Skipped |
| Small CIDR (≤256 hosts) | `104.16.72.160/29` | Fully expanded |
| Large CIDR | `104.16.0.0/16` | Random sample of up to **256** unique IPs |
| Invalid CIDR | `not-a-cidr/99` | Scan aborts with an error |

IPv6 lines are ignored. The **Count** row caps how many loaded IPs are actually probed when using From File.

**Workflow tip:** run a Random scan, press **`c`** to save working endpoints to `ips.txt`, then re-run with **From File** to validate your shortlist on more ports.

---

## Android app

The Android build shares the same probe engine and xray validation logic via a Go mobile bridge (`mobile/` + `gomobile bind`).

### UI overview

| Area | Features |
|---|---|
| **Home** | Stat cards (Tested, In-Flight, Healthy, Failed); discovered IP list; per-IP copy; bulk copy buttons for Phase 1 / Phase 2 results |
| **Settings** | Source (Random / From File + file picker), Count, Workers, Timeout, Ports, Config URL, Top N |
| **FAB** | START SCAN / STOP SCAN |
| **Info** | App description, version, GitHub and Telegram links |

### Android vs desktop

| Feature | Desktop TUI | Android |
|---|---|---|
| Persistent config + Retry Last Scan | ✓ | — (in-memory for session) |
| Live results file | ✓ | — |
| Optional config as separate screen | ✓ | Config URL in Settings |
| Phase 1 only (no config URL) | ✓ | ✓ (leave Config URL empty) |
| Neighbor scan (Random) | ✓ | ✓ |
| CIDR lines in `ips.txt` | ✓ | plain IPs only |
| Copy to `ips.txt` on device | ✓ | clipboard only |

Phase 2 runs only when a Config URL is set. xray validation on Android uses a JNI-safe code path (no stdio redirection) to avoid terminal deadlocks seen on desktop builds.

---

## Tips for restricted networks

**Start with defaults.** 5,000 random IPs, 50 workers, 5s timeout, and port 443 (or your config port) are a good baseline on lossy or filtered lines.

**Use From File after a partial run.** Copy working endpoints with `c`, edit `ips.txt`, then re-run with **Source → From File** to validate only your shortlist on more ports. You can paste CIDR blocks to sample a subnet.

**Try multiple ports.** Cloudflare CDN ports (443, 8443, 2053, …) behave differently under DPI. Multi-port selection lets Phase 1 find the best `IP:port` pair before xray validation.

**Let neighbor scan work.** In Random mode you do not need to raise Count to explore a dense block — healthy hits automatically queue nearby addresses.

**WebSocket configs need WS-friendly IPs.** Phase 1 runs an idle TLS hold plus a WebSocket upgrade check when your URL uses `type=ws`. An IP that passes trace but fails WS will not become a Phase 2 candidate.

**0% loss alone is not enough.** For HTTP-style probing, non-zero download throughput or a successful WS check is required for an IP to count as healthy.

**Speed in Phase 2 is best-effort.** Connectivity is confirmed via trace (with fallback hosts). Download speed is measured when possible. If speed cannot be measured reliably, the endpoint can still show ✓ with SPEED `n/a`.

---

## FAQ

**Why doesn't it just run a ping?**
Cloudflare drops ICMP on their edge IPs. SenPai Scanner validates HTTP/TLS behaviour and, for proxy configs, runs traffic through xray — closer to real VLESS/Trojan usage than ping or bare TCP.

**How is this different from warp-plus?**
SenPai Scanner does not run a permanent proxy. It finds and validates Cloudflare IPs for **your** xray config and exports `IP:port` lists you can plug into Sing-Box, v2rayN, etc.

**Where do the IP ranges come from?**
Embedded from Cloudflare's official published lists (`cloudflare.com/ips-v4`, `cloudflare.com/ips-v6`). The binary ships with a snapshot; ranges rarely change.

**"ips.txt not found" when using From File**
Place `ips.txt` next to the executable or in your current working directory before starting.

**The scan feels slow with many ports selected**
Each selected port is probed for every IP. Testing 5 ports on 5,000 IPs means 25,000 probes in Phase 1 — lower Count or narrow the port list if needed.

**Why is tested count higher than my Count setting?**
In Random mode, neighbor scan adds extra probes around healthy hits (up to 400 total). This is intentional.

**What happened to Quick Scan, Custom Scan, Test IPs, and Discover Colos?**
Those separate menu flows were removed to focus on one workflow: find working endpoints, optionally validate through xray, export results. The core probe engine powers **Find Working IPs** and the Android app.

---

## Building from source

### Desktop

```bash
git clone https://github.com/matinsenpai/senpaiscanner.git
cd senpaiscanner
make build          # current platform
make build-all      # all platforms → dist/
make test
make install        # to $GOPATH/bin
```

**Windows (cross-compile all platforms):**

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
# optional: -Version "0.6.0"
```

Binaries land in `dist/`.

Tagged pushes (`v*`) trigger [GoReleaser](https://goreleaser.com/) via GitHub Actions to publish multi-platform archives and checksums.

### Android

```bash
# 1. Build the Go mobile library (creates android/app/libs/senpaiscanner.aar)
cd android
./build_go_mobile.sh          # Linux / macOS
build_go_mobile.bat           # Windows

# 2. Build debug or release APK
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # requires signing keystore for release
```

Release CI builds signed APKs automatically and attaches them to the GitHub release when you push a version tag.

---

## Contributing

See **[CONTRIBUTING.md](CONTRIBUTING.md)** for project principles, development setup, and pull request guidelines.

Issues and PRs are welcome. For larger changes, open an issue first to discuss scope.

For bugs, include your OS/arch, version (`senpaiscanner --version`), the screen you were on, and what you expected vs what happened.

---

## Roadmap

- Configurable download/upload thresholds for final filtering
- Persistent settings on Android
- `Watch` mode for continuous monitoring
- Export directly to xray/Sing-Box JSON from the results screen

---

## License

MIT — see [LICENSE](LICENSE).
