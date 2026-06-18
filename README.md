# mehrpol

> Professional bilingual README for **mehrpol**.
>
> راهنمای حرفه‌ای دو زبانه برای پروژه **mehrpol**.

[![CI](https://github.com/matinsenpai/mehrpol/actions/workflows/ci.yml/badge.svg)](https://github.com/matinsenpai/mehrpol/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/matinsenpai/mehrpol?style=flat-square)](https://github.com/matinsenpai/mehrpol/releases/latest)
[![Go](https://img.shields.io/github/go-mod/go-version/matinsenpai/mehrpol?style=flat-square)](go.mod)
[![Android](https://img.shields.io/badge/android-apk-00BCD4?style=flat-square&logo=android)](#installation)
[![Platforms](https://img.shields.io/badge/platform-linux%20%7C%20macOS%20%7C%20windows%20%7C%20android%20%7C%20termux-informational?style=flat-square)](#installation)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](LICENSE)

---

## English

**mehrpol** is a Cloudflare edge IP scanner for restricted, high-latency, and unstable networks. It finds usable Cloudflare endpoints, checks SNI reachability, validates real VLESS/Trojan/VMess proxy links through embedded Xray, records scan history, and exports ready-to-import configs for popular clients.

mehrpol is available as a desktop terminal UI, Android app, and Termux-friendly binary.

### Features

| Category | Capability |
|---|---|
| 🔍 Scanning | Cloudflare IPv4 scanning, random source, custom `ips.txt` source, port selection, timeout control, and result sorting |
| ⚡ Parallel scanning | Concurrent workers with presets for restricted, balanced, and fast networks |
| 🌐 SNI check | Dedicated TLS SNI check for host/IP, SNI value, and port |
| 🤖 Auto-SNI | Optional SNI rotation and auto-detection using `MEHRPOL_SNIS` and `MEHRPOL_AUTO_SNI` |
| 🔐 TLS cert info | TLS handshake status plus certificate CN, issuer, expiry, and DNS names when available |
| 🧪 VLESS/Trojan test | End-to-end proxy validation with embedded Xray; VMess links are also parsed/exported where supported |
| 📈 Real-time speed chart | Live Phase 2 speed sparkline with latency, throughput, custom speed URL, sample size, minimum-speed filter, and optional upload test |
| 🧭 Cloudflare CDN status | Cloudflare ASN/range detection, colo/PoP metadata, country code, and CDN status |
| 🇮🇷 Iran blocked IP list | Marks endpoints found in `blocked_ips_ir.txt`, `iran_blocked_ips.txt`, or `MEHRPOL_IR_BLOCKLIST` |
| 🌍 Country/ASN filter | Filter results with `MEHRPOL_COUNTRY_FILTER` and `MEHRPOL_ASN_FILTER` |
| 🧠 Neighbor scan | Automatically scans nearby IPs around healthy random Cloudflare hits |
| 🕓 Scan history | Saves scan history and compares new, removed, and unchanged endpoints |
| 📣 Telegram bot | Sends scan summaries to Telegram when bot credentials are configured |
| ⏱️ Scheduled scan | Runs the saved scan repeatedly with `MEHRPOL_AUTOSCAN_EVERY` |
| 📦 Export | Exports V2Ray/Xray share links, Sing-box JSON, Clash YAML, and working endpoint lists |
| 📱 Android | Native Android UI with scan settings, result cards, copy actions, SNI check, and Xray validation |

### Screenshots

> Replace these placeholders with real screenshots.

| Terminal UI | Android App | Results / Export |
|---|---|---|
| ![Terminal UI screenshot placeholder](docs/screenshots/tui.png) | ![Android screenshot placeholder](docs/screenshots/android.png) | ![Results screenshot placeholder](docs/screenshots/results.png) |

### Installation

#### Linux / macOS

```bash
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash
```

Pre-release:

```bash
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

#### Windows PowerShell

```powershell
$r = Invoke-RestMethod https://api.github.com/repos/matinsenpai/mehrpol/releases/latest
$url = ($r.assets | Where-Object name -eq "mehrpol-windows-amd64.exe").browser_download_url
Invoke-WebRequest $url -OutFile mehrpol.exe
.\mehrpol.exe
```

#### Android APK

Download the latest APK from the [releases page](https://github.com/matinsenpai/mehrpol/releases/latest).

| APK | Use case |
|---|---|
| `mehrpol-{version}-universal-release.apk` | Recommended for most devices |
| `mehrpol-{version}-arm64-v8a-release.apk` | 64-bit ARM devices |
| `mehrpol-{version}-armeabi-v7a-release.apk` | 32-bit ARM devices |

#### Termux

Install Termux from F-Droid, then run:

```bash
pkg update && pkg upgrade -y
pkg install curl tar -y
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash
mehrpol
```

#### Build from source

```bash
go install github.com/matinsenpai/mehrpol/cmd/mehrpol@latest
```

From a cloned repository:

```bash
make build
./mehrpol
```

### Usage

Start the terminal UI:

```bash
mehrpol
```

Version commands:

```bash
mehrpol --version
mehrpol -v
mehrpol version
```

Typical workflow:

1. Open **Find Working IPs**.
2. Select a source: random Cloudflare IPs or `ips.txt`.
3. Configure count, workers, timeout, ports, and WebSocket requirement.
4. Optionally paste a `vless://`, `trojan://`, or `vmess://` share URL.
5. Run Phase 1 to find healthy endpoints.
6. If a config URL is provided, Phase 2 validates the best endpoints through embedded Xray.
7. Press `c` to copy/save working endpoints to `working_ips.txt`.
8. Press `e` to export `mehrpol-sub.txt`, `mehrpol-singbox.json`, and `mehrpol-clash.yaml`.
9. Press `h` to view scan history after a completed config scan.

TUI keys:

| Key | Action |
|---|---|
| `↑` / `↓` or `k` / `j` | Move between rows |
| `←` / `→` or `h` / `l` | Change options |
| `Enter` | Select, confirm, or start |
| `Space` | Toggle ports and binary options |
| `Esc` / `q` | Back, cancel, or quit |
| `c` | Copy/save working endpoints |
| `e` | Export configs after Phase 2 |
| `h` | Open scan history |

Advanced environment variables:

| Variable | Example | Purpose |
|---|---|---|
| `MEHRPOL_AUTOSCAN_EVERY` | `30m` | Scheduled scan interval |
| `MEHRPOL_SNIS` | `speed.cloudflare.com,www.cloudflare.com` | Candidate SNI rotation list |
| `MEHRPOL_AUTO_SNI` | `1` | Enable automatic SNI detection |
| `MEHRPOL_COUNTRY_FILTER` | `DE,NL,US` | Keep selected countries |
| `MEHRPOL_ASN_FILTER` | `AS13335,Cloudflare` | Keep selected ASNs or ASN organization names |
| `MEHRPOL_IR_BLOCKLIST` | `/path/blocked.txt` | Load custom Iran blocked IP/CIDR list |
| `TELEGRAM_BOT_TOKEN` | `123:abc` | Telegram bot token |
| `TELEGRAM_CHAT_ID` | `123456` | Telegram chat ID |

Input file format:

```text
# ips.txt
104.16.72.162
104.18.0.0/24
188.114.96.10,note
```

Output files:

| File | Purpose |
|---|---|
| `working_ips.txt` | Working `IP:port` endpoints |
| `mehrpolResult-YYYYMMDD-HHMMSS.txt` | Live scan report |
| `mehrpol-sub.txt` | V2Ray/Xray-style share links |
| `mehrpol-singbox.json` | Sing-box outbound config |
| `mehrpol-clash.yaml` | Clash proxy YAML |
| mehrpol history directory | JSON scan history |

### FAQ

**Does mehrpol replace Xray, V2Ray, or Clash?**
No. mehrpol discovers, validates, and exports working endpoints/configs for those clients.

**Why can Phase 1 pass but Phase 2 fail?**
Phase 1 checks reachability and CDN behavior. Phase 2 tests your actual proxy config through embedded Xray, so wrong UUID/password, host, path, SNI, route filtering, or server issues can fail there.

---

## فارسی

**mehrpol** یک اسکنر IPهای لبه Cloudflare برای شبکه‌های محدود، پرتاخیر و ناپایدار است. این ابزار endpointهای قابل استفاده را پیدا می‌کند، SNI را بررسی می‌کند، لینک‌های واقعی VLESS/Trojan/VMess را با Xray داخلی تست می‌کند، history اسکن را نگه می‌دارد و خروجی آماده برای کلاینت‌های رایج می‌سازد.

mehrpol به شکل رابط ترمینالی، اپ اندروید و باینری قابل اجرا در Termux ارائه می‌شود.

### امکانات

| بخش | قابلیت |
|---|---|
| 🔍 اسکن | اسکن IPv4های Cloudflare، منبع تصادفی، ورودی `ips.txt`، انتخاب پورت، timeout و مرتب‌سازی نتایج |
| ⚡ اسکن موازی | workerهای همزمان با preset مناسب شبکه محدود، متعادل و سریع |
| 🌐 بررسی SNI | تست TLS SNI برای host/IP، مقدار SNI و پورت |
| 🤖 Auto-SNI | چرخش SNI و تشخیص خودکار با `MEHRPOL_SNIS` و `MEHRPOL_AUTO_SNI` |
| 🔐 اطلاعات TLS cert | وضعیت TLS handshake، CN، issuer، expiry و DNS nameهای گواهی در صورت وجود |
| 🧪 تست VLESS/Trojan | اعتبارسنجی end-to-end با Xray داخلی؛ لینک‌های VMess هم در مسیرهای parser/export پشتیبانی می‌شوند |
| 📈 نمودار سرعت زنده | sparkline سرعت فاز ۲ همراه با latency، throughput، speed URL سفارشی، حجم نمونه، min-speed و تست آپلود |
| 🧭 وضعیت Cloudflare CDN | تشخیص ASN/rangeهای Cloudflare، colo/PoP، کشور و CDN status |
| 🇮🇷 لیست IPهای بلاک ایران | علامت‌گذاری IPهای موجود در `blocked_ips_ir.txt`، `iran_blocked_ips.txt` یا `MEHRPOL_IR_BLOCKLIST` |
| 🌍 فیلتر کشور/ASN | فیلتر نتایج با `MEHRPOL_COUNTRY_FILTER` و `MEHRPOL_ASN_FILTER` |
| 🧠 Neighbor scan | اسکن خودکار IPهای نزدیک به hitهای سالم در rangeهای Cloudflare |
| 🕓 تاریخچه اسکن | ذخیره history و مقایسه endpointهای جدید، حذف‌شده و ثابت |
| 📣 ربات تلگرام | ارسال خلاصه اسکن به تلگرام در صورت تنظیم اطلاعات ربات |
| ⏱️ اسکن زمان‌بندی‌شده | اجرای تکراری آخرین اسکن با `MEHRPOL_AUTOSCAN_EVERY` |
| 📦 خروجی | export لینک‌های V2Ray/Xray، JSON برای Sing-box، YAML برای Clash و لیست endpointها |
| 📱 اندروید | رابط native اندروید با تنظیمات اسکن، کارت نتایج، کپی IP، SNI Check و اعتبارسنجی Xray |

### اسکرین‌شات‌ها

> این placeholderها را با تصاویر واقعی جایگزین کنید.

| رابط ترمینال | اپ اندروید | نتایج / خروجی |
|---|---|---|
| ![Terminal UI screenshot placeholder](docs/screenshots/tui.png) | ![Android screenshot placeholder](docs/screenshots/android.png) | ![Results screenshot placeholder](docs/screenshots/results.png) |

### نصب

#### Linux / macOS

```bash
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash
```

نسخه pre-release:

```bash
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

#### Windows PowerShell

```powershell
$r = Invoke-RestMethod https://api.github.com/repos/matinsenpai/mehrpol/releases/latest
$url = ($r.assets | Where-Object name -eq "mehrpol-windows-amd64.exe").browser_download_url
Invoke-WebRequest $url -OutFile mehrpol.exe
.\mehrpol.exe
```

#### APK اندروید

آخرین APK را از [صفحه Releases](https://github.com/matinsenpai/mehrpol/releases/latest) دانلود کنید.

| APK | کاربرد |
|---|---|
| `mehrpol-{version}-universal-release.apk` | پیشنهادی برای بیشتر دستگاه‌ها |
| `mehrpol-{version}-arm64-v8a-release.apk` | دستگاه‌های ARM 64-bit |
| `mehrpol-{version}-armeabi-v7a-release.apk` | دستگاه‌های ARM 32-bit |

#### Termux

Termux را از F-Droid نصب کنید و اجرا کنید:

```bash
pkg update && pkg upgrade -y
pkg install curl tar -y
curl -fsSL https://github.com/matinsenpai/mehrpol/raw/refs/heads/main/install.sh | bash
mehrpol
```

#### نصب از سورس

```bash
go install github.com/matinsenpai/mehrpol/cmd/mehrpol@latest
```

داخل repository:

```bash
make build
./mehrpol
```

### راهنمای استفاده

اجرای رابط ترمینال:

```bash
mehrpol
```

دستورهای نسخه:

```bash
mehrpol --version
mehrpol -v
mehrpol version
```

روند معمول:

1. وارد **Find Working IPs** شوید.
2. منبع را انتخاب کنید: IP تصادفی Cloudflare یا فایل `ips.txt`.
3. تعداد، worker، timeout، پورت‌ها و WebSocket requirement را تنظیم کنید.
4. در صورت نیاز لینک `vless://`، `trojan://` یا `vmess://` را paste کنید.
5. فاز ۱ را اجرا کنید تا endpointهای سالم پیدا شوند.
6. اگر config URL وارد شده باشد، فاز ۲ بهترین endpointها را با Xray داخلی تست می‌کند.
7. با `c` endpointهای سالم را کپی و در `working_ips.txt` ذخیره کنید.
8. با `e` خروجی‌های `mehrpol-sub.txt`، `mehrpol-singbox.json` و `mehrpol-clash.yaml` را بسازید.
9. بعد از پایان اسکن config، با `h` history را ببینید.

کلیدهای TUI:

| کلید | عملکرد |
|---|---|
| `↑` / `↓` یا `k` / `j` | حرکت بین ردیف‌ها |
| `←` / `→` یا `h` / `l` | تغییر گزینه‌ها |
| `Enter` | انتخاب، تایید یا شروع |
| `Space` | فعال/غیرفعال کردن پورت‌ها و گزینه‌های دوحالته |
| `Esc` / `q` | برگشت، لغو یا خروج |
| `c` | کپی/ذخیره endpointهای سالم |
| `e` | export کانفیگ‌ها بعد از فاز ۲ |
| `h` | باز کردن history |

متغیرهای محیطی پیشرفته:

| متغیر | مثال | کاربرد |
|---|---|---|
| `MEHRPOL_AUTOSCAN_EVERY` | `30m` | فاصله زمانی اسکن زمان‌بندی‌شده |
| `MEHRPOL_SNIS` | `speed.cloudflare.com,www.cloudflare.com` | لیست SNI برای چرخش |
| `MEHRPOL_AUTO_SNI` | `1` | فعال‌سازی تشخیص خودکار SNI |
| `MEHRPOL_COUNTRY_FILTER` | `DE,NL,US` | نگه داشتن کشور‌های مشخص |
| `MEHRPOL_ASN_FILTER` | `AS13335,Cloudflare` | نگه داشتن ASN یا نام سازمان ASN |
| `MEHRPOL_IR_BLOCKLIST` | `/path/blocked.txt` | بارگذاری لیست IP/CIDR بلاک ایران |
| `TELEGRAM_BOT_TOKEN` | `123:abc` | توکن ربات تلگرام |
| `TELEGRAM_CHAT_ID` | `123456` | chat ID تلگرام |

فرمت فایل ورودی:

```text
# ips.txt
104.16.72.162
104.18.0.0/24
188.114.96.10,note
```

فایل‌های خروجی:

| فایل | کاربرد |
|---|---|
| `working_ips.txt` | endpointهای سالم به شکل `IP:port` |
| `mehrpolResult-YYYYMMDD-HHMMSS.txt` | گزارش زنده اسکن |
| `mehrpol-sub.txt` | لینک‌های share برای V2Ray/Xray |
| `mehrpol-singbox.json` | کانفیگ outbound برای Sing-box |
| `mehrpol-clash.yaml` | YAML پروکسی برای Clash |
| پوشه history | تاریخچه JSON اسکن‌ها |

### سوالات رایج

**آیا mehrpol جایگزین Xray، V2Ray یا Clash است؟**
خیر. mehrpol endpointهای سالم را پیدا، تست و برای این کلاینت‌ها export می‌کند.

**چرا فاز ۱ موفق است ولی فاز ۲ fail می‌شود؟**
فاز ۱ reachability و رفتار CDN را بررسی می‌کند. فاز ۲ کانفیگ واقعی شما را با Xray داخلی تست می‌کند؛ پس UUID/password، host، path، SNI، محدودیت مسیر یا مشکل سرور می‌تواند باعث fail شود.

---

## License

mehrpol is released under the MIT License. See [LICENSE](LICENSE).
