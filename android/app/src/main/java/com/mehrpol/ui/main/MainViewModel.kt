package com.mehrpol.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
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
    val customTopN: String = ""
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
    val phase2Status: Boolean = false
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
    val sniCheck: SniCheckUiState = SniCheckUiState()
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

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
            val res = IpResult(ip, port.toInt(), latencyMs.toInt(), loss, colo, isHealthy, isPhase2, phase2Type, phase2Speed, phase2Status)
            val current = _uiState.value
            val newList = current.results.toMutableList()
            newList.add(0, res)
            val newSamples = if (res.latencyMs > 0) {
                (current.latencySamples + res.latencyMs).takeLast(60)
            } else {
                current.latencySamples
            }
            _uiState.value = current.copy(results = newList, latencySamples = newSamples)
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
    }
}
