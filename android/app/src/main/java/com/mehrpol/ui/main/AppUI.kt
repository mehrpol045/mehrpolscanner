package com.mehrpol.ui.main
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import com.mehrpol.BuildConfig
import com.mehrpol.R
import com.mehrpol.theme.MehrpolCyan
import com.mehrpol.theme.MehrpolDarkSurface
import com.mehrpol.theme.MehrpolError
import com.mehrpol.theme.MehrpolPrimary
import com.mehrpol.theme.MehrpolSuccess
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUI(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            viewModel.dismissError()
        }
    }
    if (showInfoDialog) {
        InfoDialog(onDismiss = { showInfoDialog = false })
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showInfoDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground_raw),
                        contentDescription = stringResource(R.string.info_app_logo),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MehrpolCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                IconButton(onClick = { showInfoDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = stringResource(R.string.title_info),
                        tint = MehrpolCyan
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Security, contentDescription = "SNI Check") },
                    label = { Text("SNI") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                SmallFloatingActionButton(
                    onClick = { viewModel.toggleScan() },
                    containerColor = if (uiState.isRunning) MehrpolError else MehrpolCyan,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isRunning) "Stop scan" else "Start scan"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> HomeScreen(uiState, context)
                1 -> HistoryScreen(uiState.history)
                2 -> SniCheckScreen(
                    state = uiState.sniCheck,
                    config = uiState.config,
                    onRunCheck = viewModel::runSniCheck,
                    onConfigChanged = viewModel::updateConfig
                )
                else -> SettingsScreen(uiState.config) { newConfig ->
                    viewModel.updateConfig(newConfig)
                }
            }
        }
    }
}
@Composable
fun HomeScreen(uiState: ScanUiState, context: Context) {
    val healthyResults = remember(uiState.results) { healthyExportResults(uiState.results) }
    var showExportDialog by remember { mutableStateOf(false) }
    var selectedCount by remember { mutableStateOf(ExportCountOption.THREE) }
    var pendingDownload by remember { mutableStateOf<GeneratedExport?>(null) }
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        val export = pendingDownload
        if (uri != null && export != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(export.content.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Downloaded ${export.format.label}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Download failed: ${e.message ?: e.javaClass.simpleName}", Toast.LENGTH_LONG).show()
            }
        }
        pendingDownload = null
    }
    if (showExportDialog) {
        ExportDialog(
            results = healthyResults,
            config = uiState.config,
            selectedCount = selectedCount,
            onSelectedCountChange = { selectedCount = it },
            onDismiss = { showExportDialog = false },
            onCopy = { export ->
                copyText(context, export.format.label, export.content)
                Toast.makeText(context, "Copied ${export.format.label}", Toast.LENGTH_SHORT).show()
            },
            onDownload = { export ->
                pendingDownload = export
                documentLauncher.launch(export.fileName)
            }
        )
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatCard("Tested", uiState.tested.toString(), Modifier.weight(1f))
            StatCard("In-Flight", uiState.inFlight.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatCard("Healthy", uiState.healthy.toString(), Modifier.weight(1f), MehrpolSuccess)
            StatCard("Failed", uiState.failed.toString(), Modifier.weight(1f), MehrpolError)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LatencySparkline(samples = uiState.latencySamples, isRunning = uiState.isRunning)
        Spacer(modifier = Modifier.height(8.dp))
        if (uiState.hasCompletedScan || healthyResults.isNotEmpty()) {
            HealthyExportControls(
                selectedCount = selectedCount,
                enabled = healthyResults.isNotEmpty(),
                buttonText = if (uiState.hasCompletedScan) "Export Healthy IPs" else "Export",
                onSelectedCountChange = { selectedCount = it },
                onExport = { showExportDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text("Discovered IPs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val phase1Ips = uiState.results.filter { !it.isPhase2 && it.isHealthy }.map { it.ip }.distinct().joinToString("\n")
                    if (phase1Ips.isNotEmpty()) {
                        copyText(context, "mehrpol IPs", phase1Ips)
                        val count = uiState.results.count { !it.isPhase2 && it.isHealthy }
                        Toast.makeText(context, "Copied $count Phase 1 IPs", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = uiState.results.any { !it.isPhase2 && it.isHealthy },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MehrpolCyan),
                border = BorderStroke(1.dp, MehrpolCyan)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Phase 1", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MehrpolCyan)
            }
            if (uiState.results.any { it.isPhase2 }) {
                OutlinedButton(
                    onClick = {
                        val phase2Ips = uiState.results.filter { it.isPhase2 && it.phase2Status }.map { it.ip }.distinct().joinToString("\n")
                        if (phase2Ips.isNotEmpty()) {
                            copyText(context, "mehrpol Phase 2 IPs", phase2Ips)
                            val count = uiState.results.count { it.isPhase2 && it.phase2Status }
                            Toast.makeText(context, "Copied $count Phase 2 IPs", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.results.any { it.isPhase2 && it.phase2Status },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MehrpolPrimary),
                    border = BorderStroke(1.dp, MehrpolPrimary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Phase 2", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MehrpolPrimary)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (uiState.isPhase2) {
            val progress = if (uiState.totalPhase2 > 0) uiState.tested.toFloat() / uiState.totalPhase2 else 0f
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Xray Validating Candidates...",
                        color = MehrpolPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.tested} / ${uiState.totalPhase2}",
                        color = MehrpolPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MehrpolPrimary,
                    trackColor = MehrpolDarkSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(uiState.results) { res ->
                IpResultCard(result = res, onCopyIp = {
                    copyText(context, "IP", res.ip)
                    Toast.makeText(context, "IP copied: ${res.ip}", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthyExportControls(
    selectedCount: ExportCountOption,
    enabled: Boolean,
    buttonText: String,
    onSelectedCountChange: (ExportCountOption) -> Unit,
    onExport: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Healthy IPs in config", fontSize = 12.sp, color = Color.Gray)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ExportCountOption.entries.forEach { option ->
                FilterChip(
                    selected = selectedCount == option,
                    onClick = { onSelectedCountChange(option) },
                    label = { Text(option.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MehrpolCyan,
                        selectedLabelColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
        Button(
            onClick = onExport,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(buttonText, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun LatencySparkline(samples: List<Int>, isRunning: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Latency", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = if (samples.isNotEmpty()) "${samples.last()} ms" else if (isRunning) "Waiting" else "No samples",
                    color = if (samples.isNotEmpty()) MehrpolCyan else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Canvas(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                val trackColor = Color.Gray.copy(alpha = 0.25f)
                drawLine(trackColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1.dp.toPx())
                if (samples.size < 2) return@Canvas
                val min = samples.minOrNull() ?: 0
                val max = samples.maxOrNull() ?: min
                val range = (max - min).takeIf { it > 0 } ?: 1
                val stepX = size.width / (samples.size - 1)
                val path = Path()
                samples.forEachIndexed { index, sample ->
                    val x = stepX * index
                    val normalized = (sample - min).toFloat() / range.toFloat()
                    val y = size.height - (normalized * size.height)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = MehrpolCyan,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}
@Composable
private fun IpResultCard(result: IpResult, onCopyIp: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface)
    ) {
        if (result.isPhase2) {
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            "${result.ip}:${result.port}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        IconButton(onClick = onCopyIp, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                    Icon(
                        imageVector = if (result.phase2Status) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (result.phase2Status) "Passed" else "Failed",
                        tint = if (result.phase2Status) MehrpolSuccess else MehrpolError,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Type: ${result.phase2Type}", fontSize = 11.sp, color = Color.Gray)
                    val speedStr = if (result.phase2Speed > 1024 * 1024) {
                        String.format(Locale.US, "%.2f MB/s", result.phase2Speed / (1024 * 1024))
                    } else {
                        String.format(Locale.US, "%.0f KB/s", result.phase2Speed / 1024)
                    }
                    Text("Speed: ${if (result.phase2Speed > 0) speedStr else "-"}", fontSize = 11.sp, color = Color.Gray)
                    Text("Latency: ${if (result.latencyMs > 0) "${result.latencyMs}ms" else "-"}", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.ip, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Port: ${result.port} | Colo: ${result.colo}", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = onCopyIp, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("${result.latencyMs} ms", color = if (result.isHealthy) MehrpolSuccess else MehrpolError, fontWeight = FontWeight.Bold)
                    Text("Loss: ${String.format(Locale.US, "%.2f", result.loss)}%", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
@Composable
fun HistoryScreen(history: List<ScanHistoryEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("History", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
        }
        if (history.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Text("No scans yet", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
            }
        } else {
            items(history) { entry ->
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.date, fontWeight = FontWeight.Bold)
                            Text("${entry.ipCount} IPs tested", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("${entry.healthyCount} healthy", color = MehrpolSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
enum class ExportFormat(val label: String, val extension: String) {
    V2RAY("V2Ray links", "txt"),
    XRAY("Xray JSON", "json"),
    CLASH("Clash YAML", "yaml"),
    CSV("CSV", "csv")
}
enum class ExportCountOption(val label: String, val limit: Int?) {
    ONE("1", 1),
    THREE("3", 3),
    FIVE("5", 5),
    TEN("10", 10),
    ALL("All", null)
}
data class GeneratedExport(
    val format: ExportFormat,
    val fileName: String,
    val content: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportDialog(
    results: List<IpResult>,
    config: ScanConfig,
    selectedCount: ExportCountOption,
    onSelectedCountChange: (ExportCountOption) -> Unit,
    onDismiss: () -> Unit,
    onCopy: (GeneratedExport) -> Unit,
    onDownload: (GeneratedExport) -> Unit
) {
    val selectedResults = remember(results, selectedCount) { selectExportResults(results, selectedCount) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Healthy IPs") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Include", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    ExportCountOption.entries.forEach { option ->
                        FilterChip(
                            selected = selectedCount == option,
                            onClick = { onSelectedCountChange(option) },
                            label = { Text(option.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MehrpolCyan,
                                selectedLabelColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
                Text("${selectedResults.size} healthy IPs selected", color = MehrpolCyan, fontWeight = FontWeight.Medium)
                ExportFormat.entries.forEach { format ->
                    val export = remember(selectedResults, config, format) { buildExport(format, selectedResults, config) }
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(format.label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = { onCopy(export) }, enabled = selectedResults.isNotEmpty()) {
                                Text("Copy", color = MehrpolCyan)
                            }
                            TextButton(onClick = { onDownload(export) }, enabled = selectedResults.isNotEmpty()) {
                                Text("Download", color = MehrpolCyan)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        containerColor = MehrpolDarkSurface
    )
}
private fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}
private fun healthyExportResults(results: List<IpResult>): List<IpResult> {
    val phase2 = results.filter { it.isPhase2 }
    val healthy = if (phase2.isNotEmpty()) {
        phase2.filter { it.phase2Status }
    } else {
        results.filter { it.isHealthy }
    }
    return healthy.distinctBy { "${it.ip}:${it.port}" }.sortedBy { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }
}
private fun selectExportResults(results: List<IpResult>, count: ExportCountOption): List<IpResult> {
    return count.limit?.let { results.take(it) } ?: results
}
private fun buildExport(format: ExportFormat, results: List<IpResult>, config: ScanConfig): GeneratedExport {
    val content = when (format) {
        ExportFormat.V2RAY -> buildV2RayLinks(results, config)
        ExportFormat.XRAY -> buildXrayJson(results, config)
        ExportFormat.CLASH -> buildClashYaml(results, config)
        ExportFormat.CSV -> buildCsv(results)
    }
    return GeneratedExport(format, "mehrpol-healthy-ips.${format.extension}", content)
}
private fun buildV2RayLinks(results: List<IpResult>, config: ScanConfig): String {
    return results.mapIndexed { index, result ->
        val template = config.configUrl.trim()
        if (template.contains("://")) {
            replaceProxyEndpoint(template, result.ip, result.port, "mehrpol-${index + 1}-${result.ip}")
        } else {
            "vless://${result.ip}:${result.port}?encryption=none&security=tls&type=tcp#mehrpol-${index + 1}-${result.ip}"
        }
    }.joinToString("\n")
}
private fun buildXrayJson(results: List<IpResult>, config: ScanConfig): String {
    if (results.isEmpty()) {
        return """{
  "remarks": "Generated by mehrpol",
  "outbounds": []
}"""
    }
    val vnext = results.joinToString(",\n") { result ->
        """        {
          "address": "${result.ip}",
          "port": ${result.port},
          "users": [
            {
              "id": "00000000-0000-0000-0000-000000000000",
              "encryption": "none"
            }
          ]
        }"""
    }
    val remarks = jsonEscape(config.configUrl.takeIf { it.isNotBlank() }?.let { "Template: $it" } ?: "Generated by mehrpol")
    return """{
  "remarks": "$remarks",
  "outbounds": [
    {
      "protocol": "vless",
      "settings": {
        "vnext": [
$vnext
        ]
      },
      "streamSettings": {
        "network": "tcp",
        "security": "tls"
      }
    }
  ]
}"""
}
private fun buildClashYaml(results: List<IpResult>, config: ScanConfig): String {
    if (results.isEmpty()) return "proxies: []\nproxy-groups: []\nrules: []\n"
    val proxies = results.mapIndexed { index, result ->
        """  - name: mehrpol-${index + 1}-${result.ip}
    type: vless
    server: ${result.ip}
    port: ${result.port}
    uuid: 00000000-0000-0000-0000-000000000000
    network: tcp
    tls: true
    udp: true"""
    }.joinToString("\n")
    val names = results.mapIndexed { index, result -> "mehrpol-${index + 1}-${result.ip}" }
        .joinToString("\n") { "      - $it" }
    val note = config.configUrl.takeIf { it.isNotBlank() }?.let { "# Template: $it\n" } ?: ""
    return """${note}proxies:
$proxies
proxy-groups:
  - name: mehrpol-auto
    type: select
    proxies:
$names
rules:
  - MATCH,mehrpol-auto
"""
}
private fun buildCsv(results: List<IpResult>): String {
    val rows = results.joinToString("\n") { result ->
        listOf(result.ip, result.port.toString(), result.latencyMs.toString(), String.format(Locale.US, "%.2f", result.loss), result.colo)
            .joinToString(",") { csvEscape(it) }
    }
    return "ip,port,latency_ms,loss_percent,colo\n$rows"
}
private fun replaceProxyEndpoint(template: String, ip: String, port: Int, fragment: String): String {
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
private fun jsonEscape(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
}
private fun csvEscape(value: String): String {
    return if (value.any { it == ',' || it == '"' || it == '\n' }) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }
}
@Composable
fun SniCheckScreen(
    state: SniCheckUiState,
    config: ScanConfig,
    onRunCheck: (String, String, String) -> Unit,
    onConfigChanged: (ScanConfig) -> Unit
) {
    var host by remember { mutableStateOf("") }
    var sni by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("443") }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("SNI Check", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
        }
        item {
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Host or IP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = sni,
                onValueChange = { sni = it },
                label = { Text("SNI value") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = port,
                onValueChange = { port = it.filter(Char::isDigit).take(5) },
                label = { Text("Port") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = { onRunCheck(host, sni, port) },
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.background)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Checking...")
                } else {
                    Text("Run SNI Check", fontWeight = FontWeight.Bold)
                }
            }
        }
        state.error?.let { err ->
            item {
                Text(err, color = MehrpolError, fontWeight = FontWeight.Medium)
            }
        }
        if (state.isRangeScan && (state.isRunning || state.scanResults.isNotEmpty())) {
            item {
                SniRangeScanSummary(state)
            }
            if (state.scanResults.isNotEmpty()) {
                item {
                    Button(
                        onClick = {
                            val best = state.scanResults.first()
                            onConfigChanged(config.withSniEndpoint(context, best))
                            copyText(context, "Best SNI IP", "${best.host}:${best.port}")
                            Toast.makeText(context, "Best IP applied and copied: ${best.host}:${best.port}", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MehrpolPrimary, contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use in Config", fontWeight = FontWeight.Bold)
                    }
                }
                items(state.scanResults) { result ->
                    SniScanResultRow(
                        result = result,
                        onCopy = {
                            copyText(context, "SNI IP", "${result.host}:${result.port}")
                            Toast.makeText(context, "Copied ${result.host}:${result.port}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
        state.result?.let { result ->
            item {
                SniSingleResultCard(result)
            }
        }
    }
}
@Composable
private fun SniRangeScanSummary(state: SniCheckUiState) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cloudflare scan", fontWeight = FontWeight.Bold, color = MehrpolCyan)
                Text("${state.scanResults.size} valid", color = MehrpolSuccess, fontWeight = FontWeight.Bold)
            }
            if (state.totalCount > 0) {
                LinearProgressIndicator(
                    progress = { state.scannedCount.toFloat() / state.totalCount.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MehrpolCyan,
                    trackColor = Color.Gray.copy(alpha = 0.25f)
                )
                Text(
                    text = if (state.isRunning) "${state.scannedCount} / ${state.totalCount} IPs tested" else "Top ${state.scanResults.size} IPs sorted by latency",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
private fun SniSingleResultCard(result: SniCheckResult) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (result.isValid) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (result.isValid) "Valid" else "Invalid",
                    tint = if (result.isValid) MehrpolSuccess else MehrpolError
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (result.isValid) "Valid" else "Invalid",
                    color = if (result.isValid) MehrpolSuccess else MehrpolError,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            InfoValueRow("Endpoint", "${result.host}:${result.port}")
            InfoValueRow("SNI", result.sni)
            InfoValueRow("Status", result.status)
            InfoValueRow("Latency", "${result.latencyMs} ms")
            Text(result.message, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Composable
private fun SniScanResultRow(result: SniCheckResult, onCopy: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${result.host}:${result.port}", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(result.status, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("${result.latencyMs} ms", color = MehrpolSuccess, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
            }
        }
    }
}
private fun ScanConfig.withSniEndpoint(context: Context, result: SniCheckResult): ScanConfig {
    val selectedIpFile = File(context.cacheDir, "sni-best-ip.txt").apply {
        writeText(result.host)
    }
    val template = configUrl.trim()
    return if (template.contains("://")) {
        copy(
            sourceType = "From File",
            sourceFile = selectedIpFile.absolutePath,
            configUrl = replaceProxyEndpoint(template, result.host, result.port, "mehrpol-sni-${result.host}")
        )
    } else {
        copy(
            sourceType = "From File",
            sourceFile = selectedIpFile.absolutePath,
            portType = "CustomPorts",
            selectedPorts = setOf(result.port),
            configUrl = ""
        )
    }
}


@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.White) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(title, color = Color.Gray, fontSize = 11.sp)
            Text(value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun SettingsScreen(config: ScanConfig, onConfigChanged: (ScanConfig) -> Unit) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "ips.txt")
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                onConfigChanged(config.copy(sourceFile = tempFile.absolutePath, sourceType = "From File"))
                Toast.makeText(context, "File selected", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load file", Toast.LENGTH_SHORT).show()
            }
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("mehrpol Settings", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        // 1. Source
        item {
            SettingSection("Source", "") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = config.sourceType == "Random", onClick = { onConfigChanged(config.copy(sourceType = "Random")) }, colors = RadioButtonDefaults.colors(selectedColor = MehrpolCyan))
                    Text("Random", modifier = Modifier.clickable { onConfigChanged(config.copy(sourceType = "Random")) })
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = config.sourceType == "From File", onClick = { onConfigChanged(config.copy(sourceType = "From File")) }, colors = RadioButtonDefaults.colors(selectedColor = MehrpolCyan))
                    Text("From File", modifier = Modifier.clickable { onConfigChanged(config.copy(sourceType = "From File")) })
                }
                if (config.sourceType == "From File") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { filePickerLauncher.launch("text/plain") }, colors = ButtonDefaults.buttonColors(containerColor = MehrpolDarkSurface)) {
                        Text(if (config.sourceFile.isNotEmpty()) "File Selected" else "Import .txt File", color = MehrpolCyan)
                    }
                }
            }
        }
        // 2. Count
        item {
            SettingDropdown(
                label = "Count",
                description = "IPs to probe in Phase 1",
                options = listOf("1000", "5000", "20000", "Custom"),
                selectedOption = config.countType,
                customValue = config.customCount,
                onOptionSelected = { onConfigChanged(config.copy(countType = it)) },
                onCustomValueChanged = { onConfigChanged(config.copy(customCount = it)) },
                isNumericCustom = true
            )
        }
        // 3. Workers
        item {
            SettingDropdown(
                label = "Workers",
                description = "concurrent probes",
                options = listOf("50- default (restricted net)", "100 - balanced", "200 - fast (good connections)", "Custom"),
                selectedOption = config.workerType,
                customValue = config.customWorkers,
                onOptionSelected = { onConfigChanged(config.copy(workerType = it)) },
                onCustomValueChanged = { onConfigChanged(config.copy(customWorkers = it)) },
                isNumericCustom = true
            )
        }
        // 4. Timeout
        item {
            SettingDropdown(
                label = "Timeout",
                description = "per-probe deadline",
                options = listOf("2s - aggressive (fast net)", "3s- balanced", "5s - default (restricted net)", "Custom"),
                selectedOption = config.timeoutType,
                customValue = config.customTimeout,
                onOptionSelected = { onConfigChanged(config.copy(timeoutType = it)) },
                onCustomValueChanged = { onConfigChanged(config.copy(customTimeout = it)) },
                isNumericCustom = false // e.g., "10s"
            )
        }
        // 5. Ports
        item {
            SettingSection("Ports", "selecting multiple ports multiplies work") {
                val portOptions = listOf("Config", "443", "8443", "2053", "2083", "2087", "2096")
                Column {
                    portOptions.chunked(3).forEach { rowOptions ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowOptions.forEach { opt ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (opt == "Config") {
                                        RadioButton(
                                            selected = config.portType == "Config",
                                            onClick = { onConfigChanged(config.copy(portType = "Config")) },
                                            colors = RadioButtonDefaults.colors(selectedColor = MehrpolCyan)
                                        )
                                        Text("Config", modifier = Modifier.clickable { onConfigChanged(config.copy(portType = "Config")) })
                                    } else {
                                        Checkbox(
                                            checked = config.portType == "CustomPorts" && config.selectedPorts.contains(opt.toInt()),
                                            onCheckedChange = { checked ->
                                                val newSet = config.selectedPorts.toMutableSet()
                                                if (checked) newSet.add(opt.toInt()) else newSet.remove(opt.toInt())
                                                onConfigChanged(config.copy(portType = "CustomPorts", selectedPorts = newSet, configUrl = ""))
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = MehrpolCyan)
                                        )
                                        Text(opt)
                                    }
                                }
                            }
                        }
                    }
                    if (config.portType == "Config") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = config.configUrl,
                            onValueChange = { onConfigChanged(config.copy(configUrl = it)) },
                            label = { Text("Config URL (vless://...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        // 6. Top N
        item {
            SettingDropdown(
                label = "Top N",
                description = "Phase 2 picks - used only when a config URL is entered",
                options = listOf("10", "25", "50", "100", "ALL", "Custom"),
                selectedOption = config.topNType,
                customValue = config.customTopN,
                onOptionSelected = { onConfigChanged(config.copy(topNType = it)) },
                onCustomValueChanged = { onConfigChanged(config.copy(customTopN = it)) },
                isNumericCustom = true
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
@Composable
fun SettingSection(label: String, description: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (description.isNotEmpty()) {
            Text(description, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
        content()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDropdown(
    label: String,
    description: String,
    options: List<String>,
    selectedOption: String,
    customValue: String,
    onOptionSelected: (String) -> Unit,
    onCustomValueChanged: (String) -> Unit,
    isNumericCustom: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    SettingSection(label, description) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        if (selectedOption == "Custom") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = customValue,
                onValueChange = onCustomValueChanged,
                label = { Text("Enter Custom Value") },
                keyboardOptions = if (isNumericCustom) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.info_app_name_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MehrpolCyan
                    )
                    TextButton(onClick = onDismiss) {
                        Text("X", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MehrpolCyan.copy(alpha = 0.15f),
                                        MehrpolCyan.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground_raw),
                                    contentDescription = stringResource(R.string.info_app_logo),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.info_app_name_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.info_overview_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = stringResource(R.string.info_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val githubLink = stringResource(R.string.project_main_github)
                        val telegramLink = stringResource(R.string.project_main_telegram)
                        Text(stringResource(R.string.info_project_links), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        InfoLinkRow(
                            title = stringResource(R.string.info_main_github),
                            link = githubLink,
                            onOpen = { uriHandler.openUri("https://$githubLink") }
                        )
                        InfoLinkRow(
                            title = stringResource(R.string.info_main_telegram),
                            link = telegramLink,
                            onOpen = { uriHandler.openUri("https://$telegramLink") }
                        )
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.info_version_info), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        InfoValueRow(label = stringResource(R.string.info_app_version), value = BuildConfig.VERSION_NAME)
                    }
                }
            }
        }
    }
}
@Composable
private fun InfoLinkRow(title: String, link: String, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onOpen)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = link,
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                color = MehrpolCyan,
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = Icons.Filled.ExitToApp,
            contentDescription = stringResource(R.string.info_open_link),
            tint = MehrpolCyan
        )
    }
}
@Composable
private fun InfoValueRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF333333))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
