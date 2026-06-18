package com.mehrpol.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.mehrpol.mobile.Callback
import com.mehrpol.mobile.Mobile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.SNIHostName
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

@Serializable
data class ScanConfig(
    // Source
    val sourceType: String = "Random",
    val sourceFile: String = "",

    // Count
    val countType: String = "5000",
    val customCount: String = "",
    
    // Workers
    val workerType: String = "50- default (restricted net)",
    val customWorkers: String = "",

    // Timeout
    val timeoutType: String = "5s - default (restricted net)",
    val customTimeout: String = "",

    // Ports
    val portType: String = "Config", // "Config" or "CustomPorts"
    val selectedPorts: Set<Int> = emptySet(),
    val configUrl: String = "",

    // Top N
    val topNType: String = "50",
    val customTopN: String = "",

    // Generators
    val fragmentInterval: String = "30",
    val fragmentPackets: String = "8",
    val cleanIp: String = "",
    val protocolType: String = "VLESS",
    val shadowsocksMethod: String = "chacha20-ietf-poly1305",
    val generatedConfig: String = ""
)

data class IpResult(
    val ip: String,
    val port: Int,
    val latencyMs: Int,
    val loss: Double,
    val colo: String,
    val isHealthy: Boolean,
    val isPhase2: Boolean = false,
    val phase2Type: String = "",
    val phase2Speed: Double = 0.0,
    val phase2Status: Boolean = false,
    val jitterMs: Int = 0,
    val datacenterName: String = "",
    val countryCode: String = "",
    val region: String = "Unknown"
)

data class FavoriteIp(
    val ip: String,
    val port: Int,
    val latencyMs: Int,
    val jitterMs: Int,
    val downloadSpeed: Double,
    val colo: String,
    val datacenterName: String,
    val countryCode: String,
    val region: String,
    val lastSeen: String
)

data class MonitorSample(
    val ip: String,
    val port: Int,
    val latencyMs: Int,
    val stabilityPercent: Int,
    val checkedAt: String,
    val isBetter: Boolean = false
)

data class MonitorUiState(
    val isScheduled: Boolean = false,
    val intervalSeconds: Int = 300,
    val isFloodRunning: Boolean = false,
    val lastSamples: List<MonitorSample> = emptyList(),
    val notificationMessage: String? = null
)

data class SniCheckResult(
    val host: String,
    val sni: String,
    val port: Int,
    val status: String,
    val latencyMs: Long,
    val isValid: Boolean,
    val message: String
)

data class SniCheckUiState(
    val isRunning: Boolean = false,
    val result: SniCheckResult? = null,
    val scanResults: List<SniCheckResult> = emptyList(),
    val scannedCount: Int = 0,
    val totalCount: Int = 0,
    val isRangeScan: Boolean = false,
    val error: String? = null
)

data class ScanHistoryEntry(
    val date: String,
    val ipCount: Int,
    val healthyCount: Int
)

data class ScanUiState(
    val isRunning: Boolean = false,
    val hasCompletedScan: Boolean = false,
    val tested: Int = 0,
    val healthy: Int = 0,
    val failed: Int = 0,
    val inFlight: Int = 0,
    val isPhase2: Boolean = false,
    val totalPhase2: Int = 0,
    val results: List<IpResult> = emptyList(),
    val latencySamples: List<Int> = emptyList(),
    val history: List<ScanHistoryEntry> = emptyList(),
    val error: String? = null,
    val config: ScanConfig = ScanConfig(),
    val sniCheck: SniCheckUiState = SniCheckUiState(),
    val favorites: List<FavoriteIp> = emptyList(),
    val monitor: MonitorUiState = MonitorUiState(),
    val autoReplacedConfig: String = ""
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()
    private var monitorJob: Job? = null

    private val scanCallback = object : Callback {
        override fun onProgress(tested: Long, healthy: Long, failed: Long, inFlight: Long, isPhase2: Boolean) {
            val current = _uiState.value
            var total = current.totalPhase2
            if (isPhase2 && total == 0 && tested.toInt() == 0) {
                total = inFlight.toInt()
            }
            _uiState.value = current.copy(
                tested = tested.toInt(),
                healthy = healthy.toInt(),
                failed = failed.toInt(),
                inFlight = inFlight.toInt(),
                isPhase2 = isPhase2,
                totalPhase2 = total
            )
        }

        override fun onResult(ip: String, port: Long, latencyMs: Long, loss: Double, colo: String, isHealthy: Boolean, isPhase2: Boolean, phase2Type: String, phase2Speed: Double, phase2Status: Boolean) {
            val res = enrichResult(
                IpResult(ip, port.toInt(), latencyMs.toInt(), loss, colo, isHealthy, isPhase2, phase2Type, phase2Speed, phase2Status)
            )
            val current = _uiState.value
            val newList = current.results.toMutableList()
            newList.add(0, res)
            val newSamples = if (res.latencyMs > 0) {
                (current.latencySamples + res.latencyMs).takeLast(60)
            } else {
                current.latencySamples
            }
            val updatedConfig = maybeReplaceSavedConfig(current, res)
            _uiState.value = current.copy(
                results = newList,
                latencySamples = newSamples,
                autoReplacedConfig = updatedConfig ?: current.autoReplacedConfig
            )
        }

        override fun onFinished() {
            val current = _uiState.value
            val ipCount = current.results.map { it.ip }.distinct().size.takeIf { it > 0 } ?: current.tested
            val healthyCount = countHealthyResults(current.results)
            val newHistory = if (ipCount > 0 || current.tested > 0) {
                val entry = ScanHistoryEntry(
                    date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                    ipCount = ipCount,
                    healthyCount = healthyCount
                )
                (listOf(entry) + current.history).take(50)
            } else {
                current.history
            }
            _uiState.value = current.copy(
                isRunning = false,
                hasCompletedScan = ipCount > 0 || current.tested > 0,
                history = newHistory
            )
        }

        override fun onError(err: String) {
            _uiState.value = _uiState.value.copy(isRunning = false, error = err)
        }
    }

    fun updateConfig(config: ScanConfig) {
        _uiState.value = _uiState.value.copy(config = config)
    }

    fun toggleFavorite(result: IpResult) {
        val current = _uiState.value
        val key = "${result.ip}:${result.port}"
        val existing = current.favorites.any { "${it.ip}:${it.port}" == key }
        val nextFavorites = if (existing) {
            current.favorites.filterNot { "${it.ip}:${it.port}" == key }
        } else {
            val favorite = FavoriteIp(
                ip = result.ip,
                port = result.port,
                latencyMs = result.latencyMs,
                jitterMs = result.jitterMs,
                downloadSpeed = result.phase2Speed,
                colo = result.colo,
                datacenterName = result.datacenterName,
                countryCode = result.countryCode,
                region = result.region,
                lastSeen = nowText()
            )
            (listOf(favorite) + current.favorites).distinctBy { "${it.ip}:${it.port}" }
        }
        _uiState.value = current.copy(favorites = nextFavorites)
    }

    fun removeFavorite(ip: String, port: Int) {
        _uiState.value = _uiState.value.copy(
            favorites = _uiState.value.favorites.filterNot { it.ip == ip && it.port == port }
        )
    }

    fun updateGeneratedConfig(content: String) {
        _uiState.value = _uiState.value.copy(config = _uiState.value.config.copy(generatedConfig = content))
    }

    fun startOrStopMonitor() {
        if (monitorJob?.isActive == true) {
            monitorJob?.cancel()
            monitorJob = null
            _uiState.value = _uiState.value.copy(monitor = _uiState.value.monitor.copy(isScheduled = false))
            return
        }
        _uiState.value = _uiState.value.copy(monitor = _uiState.value.monitor.copy(isScheduled = true))
        monitorJob = viewModelScope.launch {
            while (true) {
                runMonitorPass(floodCount = 3, markRunning = false)
                delay(_uiState.value.monitor.intervalSeconds.coerceAtLeast(30) * 1000L)
            }
        }
    }

    fun updateMonitorInterval(seconds: Int) {
        _uiState.value = _uiState.value.copy(
            monitor = _uiState.value.monitor.copy(intervalSeconds = seconds.coerceIn(30, 3600))
        )
    }

    fun runPingFlood() {
        viewModelScope.launch {
            runMonitorPass(floodCount = 12, markRunning = true)
        }
    }

    fun clearMonitorNotification() {
        _uiState.value = _uiState.value.copy(monitor = _uiState.value.monitor.copy(notificationMessage = null))
    }

    fun toggleScan() {
        if (Mobile.isRunning()) {
            Mobile.stopScan()
            _uiState.value = _uiState.value.copy(isRunning = false)
        } else {
            val jsonConfig = Json.encodeToString(_uiState.value.config)
            _uiState.value = _uiState.value.copy(
                isRunning = true,
                tested = 0,
                healthy = 0,
                failed = 0,
                inFlight = 0,
                totalPhase2 = 0,
                results = emptyList(),
                latencySamples = emptyList(),
                hasCompletedScan = false,
                error = null
            )
            Mobile.startScan(jsonConfig, scanCallback)
        }
    }
    

    private fun countHealthyResults(results: List<IpResult>): Int {
        val phase2Results = results.filter { it.isPhase2 }
        return if (phase2Results.isNotEmpty()) {
            phase2Results.count { it.phase2Status }
        } else {
            results.count { it.isHealthy }
        }
    }


    private suspend fun runMonitorPass(floodCount: Int, markRunning: Boolean) {
        val favorites = _uiState.value.favorites
        if (favorites.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                monitor = _uiState.value.monitor.copy(
                    isFloodRunning = false,
                    notificationMessage = "Add favorites before starting monitor tests"
                )
            )
            return
        }
        if (markRunning) {
            _uiState.value = _uiState.value.copy(monitor = _uiState.value.monitor.copy(isFloodRunning = true))
        }
        val samples = withContext(Dispatchers.IO) {
            favorites.map { favorite ->
                async {
                    val latencies = (1..floodCount).mapNotNull {
                        tcpProbeLatency(favorite.ip, favorite.port, 2_500)
                    }
                    val average = latencies.takeIf { it.isNotEmpty() }?.average()?.toInt() ?: -1
                    val stability = ((latencies.size.toDouble() / floodCount.toDouble()) * 100.0).toInt()
                    MonitorSample(
                        ip = favorite.ip,
                        port = favorite.port,
                        latencyMs = average,
                        stabilityPercent = stability,
                        checkedAt = nowText(),
                        isBetter = average > 0 && favorite.latencyMs > 0 && average < favorite.latencyMs
                    )
                }
            }.awaitAll()
        }.sortedWith(compareBy<MonitorSample> { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }.thenByDescending { it.stabilityPercent })

        val best = samples.firstOrNull { it.isBetter }
        val message = best?.let { "Better IP found: ${it.ip}:${it.port} at ${it.latencyMs} ms" }
        _uiState.value = _uiState.value.copy(
            monitor = _uiState.value.monitor.copy(
                isFloodRunning = false,
                lastSamples = samples,
                notificationMessage = message
            )
        )
    }

    private fun tcpProbeLatency(ip: String, port: Int, timeoutMs: Int): Int? {
        val started = System.currentTimeMillis()
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
            }
            (System.currentTimeMillis() - started).toInt()
        } catch (_: Exception) {
            null
        }
    }

    private fun maybeReplaceSavedConfig(current: ScanUiState, result: IpResult): String? {
        if (!result.isHealthy && !result.phase2Status) return null
        val favorites = current.favorites
        if (favorites.isEmpty()) return null
        val bestFavorite = favorites.minByOrNull { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE } ?: return null
        if (result.latencyMs <= 0 || bestFavorite.latencyMs <= 0 || result.latencyMs >= bestFavorite.latencyMs) return null
        val source = current.config.generatedConfig.ifBlank { current.config.configUrl }
        if (!source.contains("://")) return null
        return replaceProxyEndpointText(source, result.ip, result.port, "mehrpol-auto-${result.ip}")
    }

    private fun enrichResult(result: IpResult): IpResult {
        val pop = popInfo(result.colo)
        val jitter = estimateJitter(result.latencyMs, result.loss, result.phase2Speed)
        return result.copy(
            jitterMs = jitter,
            datacenterName = pop.name,
            countryCode = pop.countryCode,
            region = pop.region
        )
    }

    private fun estimateJitter(latencyMs: Int, loss: Double, speed: Double): Int {
        if (latencyMs <= 0) return 0
        val lossPenalty = (loss * 2.5).toInt()
        val speedBonus = if (speed > 1024 * 1024) -2 else 0
        return (latencyMs / 12 + lossPenalty + speedBonus).coerceAtLeast(1)
    }

    private fun popInfo(colo: String): CloudflarePopInfo {
        val key = colo.trim().uppercase(Locale.US)
        return CLOUDFLARE_POP_INFO[key] ?: CloudflarePopInfo(
            name = if (key.isNotBlank()) "Cloudflare $key" else "Cloudflare edge",
            countryCode = "",
            region = "Unknown"
        )
    }

    private fun nowText(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    private fun replaceProxyEndpointText(template: String, ip: String, port: Int, fragment: String): String {
        val schemeEnd = template.indexOf("://")
        if (schemeEnd < 0) return template
        val authorityStart = schemeEnd + 3
        val authorityEnd = listOf('/', '?', '#')
            .map { template.indexOf(it, authorityStart) }
            .filter { it >= 0 }
            .minOrNull() ?: template.length
        val authority = template.substring(authorityStart, authorityEnd)
        val userInfoEnd = authority.lastIndexOf('@')
        val userInfo = if (userInfoEnd >= 0) authority.substring(0, userInfoEnd + 1) else ""
        val withoutFragment = template.substring(0, authorityStart) + userInfo + ip + ":$port" + template.substring(authorityEnd).substringBefore('#')
        return "$withoutFragment#$fragment"
    }

    fun runSniCheck(host: String, sni: String, portText: String) {
        val cleanHost = host.trim()
        val cleanSni = sni.trim()
        val port = portText.trim().toIntOrNull() ?: 443
        if (cleanSni.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                sniCheck = SniCheckUiState(error = "SNI is required")
            )
            return
        }
        if (port !in 1..65535) {
            _uiState.value = _uiState.value.copy(
                sniCheck = SniCheckUiState(error = "Port must be between 1 and 65535")
            )
            return
        }

        if (cleanHost.isEmpty()) {
            runCloudflareSniScan(cleanSni, port)
        } else {
            _uiState.value = _uiState.value.copy(sniCheck = SniCheckUiState(isRunning = true))
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) { checkSni(cleanHost, cleanSni, port) }
                _uiState.value = _uiState.value.copy(sniCheck = SniCheckUiState(result = result))
            }
        }
    }

    private fun runCloudflareSniScan(sni: String, port: Int) {
        val candidates = cloudflareSniCandidates()
        _uiState.value = _uiState.value.copy(
            sniCheck = SniCheckUiState(
                isRunning = true,
                isRangeScan = true,
                totalCount = candidates.size
            )
        )
        viewModelScope.launch {
            val validResults = mutableListOf<SniCheckResult>()
            var scanned = 0
            withContext(Dispatchers.IO) {
                candidates.chunked(CLOUDFLARE_SNI_SCAN_CONCURRENCY).forEach { batch ->
                    val batchResults = batch.map { candidate ->
                        async { checkSni(candidate, sni, port, timeoutMs = 4_000) }
                    }.awaitAll()
                    scanned += batchResults.size
                    validResults += batchResults.filter { it.isValid }
                    val topResults = validResults
                        .sortedBy { it.latencyMs }
                        .take(CLOUDFLARE_SNI_SCAN_RESULT_LIMIT)
                    _uiState.value = _uiState.value.copy(
                        sniCheck = SniCheckUiState(
                            isRunning = scanned < candidates.size,
                            scanResults = topResults,
                            scannedCount = scanned,
                            totalCount = candidates.size,
                            isRangeScan = true
                        )
                    )
                }
            }
            val finalResults = validResults
                .sortedBy { it.latencyMs }
                .take(CLOUDFLARE_SNI_SCAN_RESULT_LIMIT)
            _uiState.value = _uiState.value.copy(
                sniCheck = SniCheckUiState(
                    scanResults = finalResults,
                    scannedCount = candidates.size,
                    totalCount = candidates.size,
                    isRangeScan = true,
                    error = if (finalResults.isEmpty()) "No valid Cloudflare IPs found for this SNI and port" else null
                )
            )
        }
    }

    private fun checkSni(host: String, sni: String, port: Int, timeoutMs: Int = 7_000): SniCheckResult {
        val started = System.currentTimeMillis()
        return try {
            Socket().use { tcpSocket ->
                tcpSocket.connect(InetSocketAddress(host, port), timeoutMs)
                tcpSocket.soTimeout = timeoutMs
                val sslSocket = SSLContext.getDefault().socketFactory
                    .createSocket(tcpSocket, host, port, true) as SSLSocket
                sslSocket.use { tlsSocket ->
                    tlsSocket.sslParameters = tlsSocket.sslParameters.apply {
                        serverNames = listOf(SNIHostName(sni))
                    }
                    tlsSocket.startHandshake()
                    val latency = System.currentTimeMillis() - started
                    val writer = PrintWriter(tlsSocket.outputStream, false)
                    writer.print("HEAD / HTTP/1.1\r\nHost: $sni\r\nUser-Agent: mehrpol/1.0\r\nConnection: close\r\n\r\n")
                    writer.flush()
                    val statusLine = BufferedReader(InputStreamReader(tlsSocket.inputStream)).readLine()
                    val status = statusLine?.takeIf { it.startsWith("HTTP/") } ?: "TLS handshake succeeded"
                    SniCheckResult(
                        host = host,
                        sni = sni,
                        port = port,
                        status = status,
                        latencyMs = latency,
                        isValid = true,
                        message = "SNI is valid"
                    )
                }
            }
        } catch (e: Exception) {
            SniCheckResult(
                host = host,
                sni = sni,
                port = port,
                status = "Connection failed",
                latencyMs = System.currentTimeMillis() - started,
                isValid = false,
                message = "SNI is blocked or unavailable: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }

    private fun cloudflareSniCandidates(): List<String> {
        return CLOUDFLARE_IPV4_RANGES
            .flatMap { range -> sampleIpv4Range(range, CLOUDFLARE_SNI_CANDIDATES_PER_RANGE) }
            .distinct()
    }

    private fun sampleIpv4Range(cidr: String, count: Int): List<String> {
        val parts = cidr.split("/")
        if (parts.size != 2) return emptyList()
        val base = ipv4ToLong(parts[0]) ?: return emptyList()
        val prefix = parts[1].toIntOrNull() ?: return emptyList()
        if (prefix !in 0..32) return emptyList()

        val size = 1L shl (32 - prefix)
        val firstUsableOffset = if (size > 2) 1L else 0L
        val usableSize = if (size > 2) size - 2L else size
        if (usableSize <= 0L) return emptyList()

        val samples = count.coerceAtLeast(1)
        val step = (usableSize / (samples + 1L)).coerceAtLeast(1L)
        return (1..samples).map { index ->
            val offset = firstUsableOffset + (step * index).coerceAtMost(usableSize - 1L)
            longToIpv4(base + offset)
        }
    }

    private fun ipv4ToLong(ip: String): Long? {
        val octets = ip.split('.')
        if (octets.size != 4) return null
        return octets.fold(0L) { acc, octet ->
            val value = octet.toIntOrNull() ?: return null
            if (value !in 0..255) return null
            (acc shl 8) or value.toLong()
        }
    }

    private fun longToIpv4(value: Long): String {
        return listOf(
            (value shr 24) and 255L,
            (value shr 16) and 255L,
            (value shr 8) and 255L,
            value and 255L
        ).joinToString(".")
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        private const val CLOUDFLARE_SNI_CANDIDATES_PER_RANGE = 16
        private const val CLOUDFLARE_SNI_SCAN_CONCURRENCY = 24
        private const val CLOUDFLARE_SNI_SCAN_RESULT_LIMIT = 10

        private val CLOUDFLARE_IPV4_RANGES = listOf(
            "173.245.48.0/20",
            "103.21.244.0/22",
            "103.22.200.0/22",
            "103.31.4.0/22",
            "141.101.64.0/18",
            "108.162.192.0/18",
            "190.93.240.0/20",
            "188.114.96.0/20",
            "197.234.240.0/22",
            "198.41.128.0/17",
            "162.158.0.0/15",
            "104.16.0.0/13",
            "104.24.0.0/14",
            "172.64.0.0/13",
            "131.0.72.0/22"
        )

        private data class CloudflarePopInfo(
            val name: String,
            val countryCode: String,
            val region: String
        )

        private val CLOUDFLARE_POP_INFO = mapOf(
            "AMS" to CloudflarePopInfo("Amsterdam", "NL", "Europe"),
            "ARN" to CloudflarePopInfo("Stockholm", "SE", "Europe"),
            "CDG" to CloudflarePopInfo("Paris", "FR", "Europe"),
            "FRA" to CloudflarePopInfo("Frankfurt", "DE", "Europe"),
            "LHR" to CloudflarePopInfo("London", "GB", "Europe"),
            "MAD" to CloudflarePopInfo("Madrid", "ES", "Europe"),
            "MXP" to CloudflarePopInfo("Milan", "IT", "Europe"),
            "WAW" to CloudflarePopInfo("Warsaw", "PL", "Europe"),
            "IAD" to CloudflarePopInfo("Ashburn", "US", "US"),
            "ATL" to CloudflarePopInfo("Atlanta", "US", "US"),
            "BOS" to CloudflarePopInfo("Boston", "US", "US"),
            "DFW" to CloudflarePopInfo("Dallas", "US", "US"),
            "DEN" to CloudflarePopInfo("Denver", "US", "US"),
            "EWR" to CloudflarePopInfo("Newark", "US", "US"),
            "LAX" to CloudflarePopInfo("Los Angeles", "US", "US"),
            "MIA" to CloudflarePopInfo("Miami", "US", "US"),
            "ORD" to CloudflarePopInfo("Chicago", "US", "US"),
            "SEA" to CloudflarePopInfo("Seattle", "US", "US"),
            "SJC" to CloudflarePopInfo("San Jose", "US", "US"),
            "YYZ" to CloudflarePopInfo("Toronto", "CA", "North America"),
            "YVR" to CloudflarePopInfo("Vancouver", "CA", "North America"),
            "BOM" to CloudflarePopInfo("Mumbai", "IN", "Asia"),
            "DEL" to CloudflarePopInfo("Delhi", "IN", "Asia"),
            "SIN" to CloudflarePopInfo("Singapore", "SG", "Asia"),
            "HKG" to CloudflarePopInfo("Hong Kong", "HK", "Asia"),
            "NRT" to CloudflarePopInfo("Tokyo", "JP", "Asia"),
            "KIX" to CloudflarePopInfo("Osaka", "JP", "Asia"),
            "ICN" to CloudflarePopInfo("Seoul", "KR", "Asia"),
            "TPE" to CloudflarePopInfo("Taipei", "TW", "Asia"),
            "BKK" to CloudflarePopInfo("Bangkok", "TH", "Asia"),
            "KUL" to CloudflarePopInfo("Kuala Lumpur", "MY", "Asia"),
            "MNL" to CloudflarePopInfo("Manila", "PH", "Asia"),
            "SYD" to CloudflarePopInfo("Sydney", "AU", "Oceania"),
            "MEL" to CloudflarePopInfo("Melbourne", "AU", "Oceania"),
            "AKL" to CloudflarePopInfo("Auckland", "NZ", "Oceania"),
            "GRU" to CloudflarePopInfo("Sao Paulo", "BR", "South America"),
            "EZE" to CloudflarePopInfo("Buenos Aires", "AR", "South America"),
            "SCL" to CloudflarePopInfo("Santiago", "CL", "South America"),
            "JNB" to CloudflarePopInfo("Johannesburg", "ZA", "Africa"),
            "CPT" to CloudflarePopInfo("Cape Town", "ZA", "Africa"),
            "DXB" to CloudflarePopInfo("Dubai", "AE", "Middle East"),
            "DOH" to CloudflarePopInfo("Doha", "QA", "Middle East"),
            "TLV" to CloudflarePopInfo("Tel Aviv", "IL", "Middle East")
        )

    }
}
