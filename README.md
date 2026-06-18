# mehrpolscanner

> Professional bilingual README for **mehrpolscanner**.
>
> راهنمای حرفه‌ای دو زبانه برای پروژه **mehrpolscanner**.

[![CI](https://github.com/matinsenpai/mehrpolscanner/actions/workflows/ci.yml/badge.svg)](https://github.com/matinsenpai/mehrpolscanner/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/matinsenpai/mehrpolscanner?style=flat-square)](https://github.com/matinsenpai/mehrpolscanner/releases/latest)
[![Go](https://img.shields.io/github/go-mod/go-version/matinsenpai/mehrpolscanner?style=flat-square)](go.mod)
[![Android](https://img.shields.io/badge/android-apk-00BCD4?style=flat-square&logo=android)](#installation)
[![Platforms](https://img.shields.io/badge/platform-linux%20%7C%20macOS%20%7C%20windows%20%7C%20android%20%7C%20termux-informational?style=flat-square)](#installation)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](LICENSE)

---

## English

**mehrpolscanner** is a Cloudflare edge IP scanner for restricted, high-latency, and unstable networks. It finds usable Cloudflare endpoints, checks SNI reachability, validates real VLESS/Trojan/VMess proxy links through embedded Xray, records scan history, and exports ready-to-import configs for popular clients.

mehrpolscanner is a fork of [SenPaiScanner by MatinSenPai](https://github.com/MatinSenPai/SenPaiScanner).

mehrpolscanner is available as a desktop terminal UI, Android app, and Termux-friendly binary.

### Features

| Category | Capability |
|---|---|
| 🔍 Scanning | Cloudflare IPv4 scanning, random source, custom `ips.txt` source, port selection, timeout control, result sorting, and IP count selector for config generation |
| ⚡ Parallel scanning | Concurrent workers with presets for restricted, balanced, and fast networks |
| 🌐 SNI check | Dedicated TLS SNI check for host/IP, SNI value, and port; when only SNI + port are entered, it automatically scans Cloudflare IPs and returns the best latency-sorted results |
| 🤖 Auto-SNI | Optional SNI rotation and auto-detection using `MEHRPOLSCANNER_SNIS` and `MEHRPOLSCANNER_AUTO_SNI` |
| 🔐 TLS cert info | TLS handshake status plus certificate CN, issuer, expiry, and DNS names when available |
| 🧪 VLESS/Trojan test | End-to-end proxy validation with embedded Xray; VMess links are also parsed/exported where supported |
| 📈 Real-time charts | Live Phase 2 speed sparkline and real-time latency sparkline chart with throughput, custom speed URL, sample size, minimum-speed filter, and optional upload test |
| 🧭 Cloudflare CDN status | Cloudflare ASN/range detection, colo/PoP metadata, country code, and CDN status |
| 🇮🇷 Iran blocked IP list | Marks endpoints found in `blocked_ips_ir.txt`, `iran_blocked_ips.txt`, or `MEHRPOLSCANNER_IR_BLOCKLIST` |
| 🌍 Country/ASN filter | Filter results with `MEHRPOLSCANNER_COUNTRY_FILTER` and `MEHRPOLSCANNER_ASN_FILTER` |
| 🧠 Neighbor scan | Automatically scans nearby IPs around healthy random Cloudflare hits |
| 🕓 History tab | Shows previous scans with date, IP, and healthy count, and compares new, removed, and unchanged endpoints |
| 📣 Telegram bot | Sends scan summaries to Telegram when bot credentials are configured |
| ⏱️ Scheduled scan | Runs the saved scan repeatedly with `MEHRPOLSCANNER_AUTOSCAN_EVERY` |
| 📦 Export dialog | Exports V2Ray links, Xray JSON, Clash YAML, CSV, and working endpoint lists with copy and download actions |
| 📱 Android | Native Android UI with an improved HOME screen, floating scan button, scan settings, result cards, per-IP copy button, SNI check, and Xray validation |

### Screenshots

> Replace these placeholders with real screenshots.

| Terminal UI | Android App | Results / Export |
|---|---|---|
| ![Terminal UI screenshot placeholder](docs/screenshots/tui.png) | ![Android screenshot placeholder](docs/screenshots/android.png) | ![Results screenshot placeholder](docs/screenshots/results.png) |

### Installation

#### Linux / macOS

```bash
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash
```

Pre-release:

```bash
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

#### Windows PowerShell

```powershell
$r = Invoke-RestMethod https://api.github.com/repos/matinsenpai/mehrpolscanner/releases/latest
$url = ($r.assets | Where-Object name -eq "mehrpolscanner-windows-amd64.exe").browser_download_url
Invoke-WebRequest $url -OutFile mehrpolscanner.exe
.\mehrpolscanner.exe
```

#### Android APK

Download the latest APK from the [releases page](https://github.com/matinsenpai/mehrpolscanner/releases/latest).

| APK | Use case |
|---|---|
| `mehrpolscanner-{version}-universal-release.apk` | Recommended for most devices |
| `mehrpolscanner-{version}-arm64-v8a-release.apk` | 64-bit ARM devices |
| `mehrpolscanner-{version}-armeabi-v7a-release.apk` | 32-bit ARM devices |

#### Termux

Install Termux from F-Droid, then run:

```bash
pkg update && pkg upgrade -y
pkg install curl tar -y
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash
mehrpolscanner
```

#### Build from source

```bash
go install github.com/matinsenpai/mehrpolscanner/cmd/mehrpolscanner@latest
```

From a cloned repository:

```bash
make build
./mehrpolscanner
```

### Usage

Start the terminal UI:

```bash
mehrpolscanner
```

Version commands:

```bash
mehrpolscanner --version
mehrpolscanner -v
mehrpolscanner version
```

Typical workflow:

1. Open **Find Working IPs**.
2. Select a source: random Cloudflare IPs or `ips.txt`.
3. Configure count, workers, timeout, ports, and WebSocket requirement.
4. Optionally paste a `vless://`, `trojan://`, or `vmess://` share URL.
5. Run Phase 1 to find healthy endpoints.
6. If a config URL is provided, Phase 2 validates the best endpoints through embedded Xray.
7. Press `c` to copy/save working endpoints to `working_ips.txt`.
8. Press `e` to export `mehrpolscanner-sub.txt`, `mehrpolscanner-singbox.json`, and `mehrpolscanner-clash.yaml`.
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
| `MEHRPOLSCANNER_AUTOSCAN_EVERY` | `30m` | Scheduled scan interval |
| `MEHRPOLSCANNER_SNIS` | `speed.cloudflare.com,www.cloudflare.com` | Candidate SNI rotation list |
| `MEHRPOLSCANNER_AUTO_SNI` | `1` | Enable automatic SNI detection |
| `MEHRPOLSCANNER_COUNTRY_FILTER` | `DE,NL,US` | Keep selected countries |
| `MEHRPOLSCANNER_ASN_FILTER` | `AS13335,Cloudflare` | Keep selected ASNs or ASN organization names |
| `MEHRPOLSCANNER_IR_BLOCKLIST` | `/path/blocked.txt` | Load custom Iran blocked IP/CIDR list |
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
| `mehrpolscannerResult-YYYYMMDD-HHMMSS.txt` | Live scan report |
| `mehrpolscanner-sub.txt` | V2Ray/Xray-style share links |
| `mehrpolscanner-singbox.json` | Sing-box outbound config |
| `mehrpolscanner-clash.yaml` | Clash proxy YAML |
| mehrpolscanner history directory | JSON scan history |

### FAQ

**Does mehrpolscanner replace Xray, V2Ray, or Clash?**
No. mehrpolscanner discovers, validates, and exports working endpoints/configs for those clients.

**Why can Phase 1 pass but Phase 2 fail?**
Phase 1 checks reachability and CDN behavior. Phase 2 tests your actual proxy config through embedded Xray, so wrong UUID/password, host, path, SNI, route filtering, or server issues can fail there.

---

## فارسی

**mehrpolscanner** یک اسکنر IPهای لبه Cloudflare برای شبکه‌های محدود، پرتاخیر و ناپایدار است. این ابزار endpointهای قابل استفاده را پیدا می‌کند، SNI را بررسی می‌کند، لینک‌های واقعی VLESS/Trojan/VMess را با Xray داخلی تست می‌کند، history اسکن را نگه می‌دارد و خروجی آماده برای کلاینت‌های رایج می‌سازد.

mehrpolscanner یک fork از [SenPaiScanner by MatinSenPai](https://github.com/MatinSenPai/SenPaiScanner) است.

mehrpolscanner به شکل رابط ترمینالی، اپ اندروید و باینری قابل اجرا در Termux ارائه می‌شود.

### امکانات

| بخش | قابلیت |
|---|---|
| 🔍 اسکن | اسکن IPv4های Cloudflare، منبع تصادفی، ورودی `ips.txt`، انتخاب پورت، timeout، مرتب‌سازی نتایج و انتخاب تعداد IP برای ساخت config |
| ⚡ اسکن موازی | workerهای همزمان با preset مناسب شبکه محدود، متعادل و سریع |
| 🌐 بررسی SNI | تست TLS SNI برای host/IP، مقدار SNI و پورت؛ اگر فقط SNI و پورت وارد شود، IPهای Cloudflare را خودکار اسکن می‌کند و بهترین نتایج را بر اساس latency برمی‌گرداند |
| 🤖 Auto-SNI | چرخش SNI و تشخیص خودکار با `MEHRPOLSCANNER_SNIS` و `MEHRPOLSCANNER_AUTO_SNI` |
| 🔐 اطلاعات TLS cert | وضعیت TLS handshake، CN، issuer، expiry و DNS nameهای گواهی در صورت وجود |
| 🧪 تست VLESS/Trojan | اعتبارسنجی end-to-end با Xray داخلی؛ لینک‌های VMess هم در مسیرهای parser/export پشتیبانی می‌شوند |
| 📈 نمودارهای زنده | sparkline سرعت فاز ۲ و نمودار sparkline لحظه‌ای latency همراه با throughput، speed URL سفارشی، حجم نمونه، min-speed و تست آپلود |
| 🧭 وضعیت Cloudflare CDN | تشخیص ASN/rangeهای Cloudflare، colo/PoP، کشور و CDN status |
| 🇮🇷 لیست IPهای بلاک ایران | علامت‌گذاری IPهای موجود در `blocked_ips_ir.txt`، `iran_blocked_ips.txt` یا `MEHRPOLSCANNER_IR_BLOCKLIST` |
| 🌍 فیلتر کشور/ASN | فیلتر نتایج با `MEHRPOLSCANNER_COUNTRY_FILTER` و `MEHRPOLSCANNER_ASN_FILTER` |
| 🧠 Neighbor scan | اسکن خودکار IPهای نزدیک به hitهای سالم در rangeهای Cloudflare |
| 🕓 تب History | نمایش اسکن‌های قبلی با تاریخ، IP و تعداد سالم‌ها، همراه با مقایسه endpointهای جدید، حذف‌شده و ثابت |
| 📣 ربات تلگرام | ارسال خلاصه اسکن به تلگرام در صورت تنظیم اطلاعات ربات |
| ⏱️ اسکن زمان‌بندی‌شده | اجرای تکراری آخرین اسکن با `MEHRPOLSCANNER_AUTOSCAN_EVERY` |
| 📦 دیالوگ خروجی | export لینک‌های V2Ray، JSON برای Xray، YAML برای Clash، CSV و لیست endpointها با copy و download |
| 📱 اندروید | رابط native اندروید با HOME screen بهبودیافته، دکمه شناور اسکن، تنظیمات اسکن، کارت نتایج، دکمه کپی برای هر IP، SNI Check و اعتبارسنجی Xray |

### اسکرین‌شات‌ها

> این placeholderها را با تصاویر واقعی جایگزین کنید.

| رابط ترمینال | اپ اندروید | نتایج / خروجی |
|---|---|---|
| ![Terminal UI screenshot placeholder](docs/screenshots/tui.png) | ![Android screenshot placeholder](docs/screenshots/android.png) | ![Results screenshot placeholder](docs/screenshots/results.png) |

### نصب

#### Linux / macOS

```bash
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash
```

نسخه pre-release:

```bash
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash -s -- --prerelease
```

#### Windows PowerShell

```powershell
$r = Invoke-RestMethod https://api.github.com/repos/matinsenpai/mehrpolscanner/releases/latest
$url = ($r.assets | Where-Object name -eq "mehrpolscanner-windows-amd64.exe").browser_download_url
Invoke-WebRequest $url -OutFile mehrpolscanner.exe
.\mehrpolscanner.exe
```

#### APK اندروید

آخرین APK را از [صفحه Releases](https://github.com/matinsenpai/mehrpolscanner/releases/latest) دانلود کنید.

| APK | کاربرد |
|---|---|
| `mehrpolscanner-{version}-universal-release.apk` | پیشنهادی برای بیشتر دستگاه‌ها |
| `mehrpolscanner-{version}-arm64-v8a-release.apk` | دستگاه‌های ARM 64-bit |
| `mehrpolscanner-{version}-armeabi-v7a-release.apk` | دستگاه‌های ARM 32-bit |

#### Termux

Termux را از F-Droid نصب کنید و اجرا کنید:

```bash
pkg update && pkg upgrade -y
pkg install curl tar -y
curl -fsSL https://github.com/matinsenpai/mehrpolscanner/raw/refs/heads/main/install.sh | bash
mehrpolscanner
```

#### نصب از سورس

```bash
go install github.com/matinsenpai/mehrpolscanner/cmd/mehrpolscanner@latest
```

داخل repository:

```bash
make build
./mehrpolscanner
```

### راهنمای استفاده

اجرای رابط ترمینال:

```bash
mehrpolscanner
```

دستورهای نسخه:

```bash
mehrpolscanner --version
mehrpolscanner -v
mehrpolscanner version
```

روند معمول:

1. وارد **Find Working IPs** شوید.
2. منبع را انتخاب کنید: IP تصادفی Cloudflare یا فایل `ips.txt`.
3. تعداد، worker، timeout، پورت‌ها و WebSocket requirement را تنظیم کنید.
4. در صورت نیاز لینک `vless://`، `trojan://` یا `vmess://` را paste کنید.
5. فاز ۱ را اجرا کنید تا endpointهای سالم پیدا شوند.
6. اگر config URL وارد شده باشد، فاز ۲ بهترین endpointها را با Xray داخلی تست می‌کند.
7. با `c` endpointهای سالم را کپی و در `working_ips.txt` ذخیره کنید.
8. با `e` خروجی‌های `mehrpolscanner-sub.txt`، `mehrpolscanner-singbox.json` و `mehrpolscanner-clash.yaml` را بسازید.
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
| `MEHRPOLSCANNER_AUTOSCAN_EVERY` | `30m` | فاصله زمانی اسکن زمان‌بندی‌شده |
| `MEHRPOLSCANNER_SNIS` | `speed.cloudflare.com,www.cloudflare.com` | لیست SNI برای چرخش |
| `MEHRPOLSCANNER_AUTO_SNI` | `1` | فعال‌سازی تشخیص خودکار SNI |
| `MEHRPOLSCANNER_COUNTRY_FILTER` | `DE,NL,US` | نگه داشتن کشور‌های مشخص |
| `MEHRPOLSCANNER_ASN_FILTER` | `AS13335,Cloudflare` | نگه داشتن ASN یا نام سازمان ASN |
| `MEHRPOLSCANNER_IR_BLOCKLIST` | `/path/blocked.txt` | بارگذاری لیست IP/CIDR بلاک ایران |
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
| `mehrpolscannerResult-YYYYMMDD-HHMMSS.txt` | گزارش زنده اسکن |
| `mehrpolscanner-sub.txt` | لینک‌های share برای V2Ray/Xray |
| `mehrpolscanner-singbox.json` | کانفیگ outbound برای Sing-box |
| `mehrpolscanner-clash.yaml` | YAML پروکسی برای Clash |
| پوشه history | تاریخچه JSON اسکن‌ها |

### سوالات رایج

**آیا mehrpolscanner جایگزین Xray، V2Ray یا Clash است؟**
خیر. mehrpolscanner endpointهای سالم را پیدا، تست و برای این کلاینت‌ها export می‌کند.

**چرا فاز ۱ موفق است ولی فاز ۲ fail می‌شود؟**
فاز ۱ reachability و رفتار CDN را بررسی می‌کند. فاز ۲ کانفیگ واقعی شما را با Xray داخلی تست می‌کند؛ پس UUID/password، host، path، SNI، محدودیت مسیر یا مشکل سرور می‌تواند باعث fail شود.

---

## License

mehrpolscanner is released under the MIT License. See [LICENSE](LICENSE).
