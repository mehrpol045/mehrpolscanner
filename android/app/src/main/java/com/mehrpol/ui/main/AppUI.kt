package com.mehrpol.ui.main
import android.app.NotificationChannel
import android.Manifest
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.geometry.Size
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
import kotlin.math.absoluteValue
import com.mehrpol.BestIpWidgetProvider
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
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
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Warp") },
                    label = { Text("Warp", fontSize = 10.sp, maxLines = 1) },
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
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Proxy Checker") },
                    label = { Text("Proxy", fontSize = 10.sp, maxLines = 1) },
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
                    icon = { Icon(Icons.Default.Subscriptions, contentDescription = "Subscriptions") },
                    label = { Text("Sub", fontSize = 10.sp, maxLines = 1) },
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
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
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
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AutoGraph, contentDescription = "Monitor") },
                    label = { Text("Monitor") },
                    selected = selectedTab == 7,
                    onClick = { selectedTab = 7 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Radar, contentDescription = "Diagnostics") },
                    label = { Text("Diagnostics", fontSize = 10.sp, maxLines = 1) },
                    selected = selectedTab == 8,
                    onClick = { selectedTab = 8 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = "Domains") },
                    label = { Text("Domains", fontSize = 10.sp, maxLines = 1) },
                    selected = selectedTab == 9,
                    onClick = { selectedTab = 9 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dns, contentDescription = "DNS") },
                    label = { Text("DNS", fontSize = 10.sp, maxLines = 1) },
                    selected = selectedTab == 10,
                    onClick = { selectedTab = 10 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MehrpolCyan,
                        indicatorColor = MehrpolCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "DNS Hunter") },
                    label = { Text("Hunter", fontSize = 10.sp, maxLines = 1) },
                    selected = selectedTab == 11,
                    onClick = { selectedTab = 11 },
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
                    label = { Text("Settings", fontSize = 10.sp, maxLines = 1) },
                    selected = selectedTab == 12,
                    onClick = { selectedTab = 12 },
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
                0 -> HomeScreen(
                    uiState = uiState,
                    context = context,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onRunSpeedTest = viewModel::runSpeedTest
                )
                1 -> WarpScannerScreen(
                    state = uiState.warpScanner,
                    onRun = viewModel::runWarpScan,
                    context = context
                )
                2 -> ProxyCheckerScreen(
                    state = uiState.proxyChecker,
                    onRun = viewModel::runProxyCheck,
                    context = context
                )
                3 -> SubscriptionParserScreen(
                    state = uiState.subscriptionParser,
                    onRun = viewModel::parseSubscription,
                    context = context
                )
                4 -> HistoryScreen(uiState.history)
                5 -> SniCheckScreen(
                    state = uiState.sniCheck,
                    spoofState = uiState.sniSpoof,
                    config = uiState.config,
                    onRunCheck = viewModel::runSniCheck,
                    onRunSpoofCheck = viewModel::runSniSpoofCheck,
                    onConfigChanged = viewModel::updateConfig
                )
                6 -> FavoritesScreen(
                    favorites = uiState.favorites,
                    autoReplacedConfig = uiState.autoReplacedConfig,
                    onRemoveFavorite = viewModel::removeFavorite,
                    context = context
                )
                7 -> MonitorScreen(
                    state = uiState.monitor,
                    favorites = uiState.favorites,
                    onToggleSchedule = viewModel::startOrStopMonitor,
                    onRunFlood = viewModel::runPingFlood,
                    onIntervalChanged = viewModel::updateMonitorInterval,
                    onClearNotification = viewModel::clearMonitorNotification,
                    context = context
                )
                8 -> DiagnosticsScreen(
                    state = uiState.diagnostics,
                    onStart = viewModel::runDiagnostics
                )
                9 -> DomainsScreen(
                    state = uiState.domains,
                    onAddDomain = viewModel::addCustomDomain,
                    onCheckAll = viewModel::checkAllDomains,
                    onCheckDomain = viewModel::checkDomain
                )
                10 -> DnsScreen(
                    state = uiState.dns,
                    onTestAll = viewModel::testAllDnsProviders
                )
                11 -> DnsHunterScreen(
                    state = uiState.dnsHunter,
                    onRun = viewModel::runDnsHunter
                )
                else -> SettingsScreen(
                    uiState = uiState,
                    onConfigChanged = viewModel::updateConfig,
                    onGeneratedConfig = viewModel::updateGeneratedConfig,
                    onThemeChanged = viewModel::updateTheme,
                    onLanguageChanged = viewModel::updateLanguage,
                    onBuildBackup = viewModel::buildSettingsBackup,
                    onRestoreBackup = viewModel::restoreSettingsBackup
                )
            }
        }
    }
}
@Composable
fun HomeScreen(uiState: ScanUiState, context: Context, onToggleFavorite: (IpResult) -> Unit, onRunSpeedTest: () -> Unit) {
    val healthyResults = remember(uiState.results) { healthyExportResults(uiState.results) }
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf(ResultSortOption.LATENCY) }
    val visibleResults = remember(uiState.results, selectedRegion, selectedSort) {
        sortResults(filterResultsByRegion(uiState.results, selectedRegion), selectedSort)
    }
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
        SpeedTestPanel(state = uiState.speedTest, onRun = onRunSpeedTest)
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
        ResultControls(
            selectedRegion = selectedRegion,
            selectedSort = selectedSort,
            onRegionSelected = { selectedRegion = it },
            onSortSelected = { selectedSort = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Discovered IPs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Cloudflare ranges load automatically in Random mode", fontSize = 12.sp, color = Color.Gray)
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
            items(visibleResults) { res ->
                val isFavorite = uiState.favorites.any { it.ip == res.ip && it.port == res.port }
                IpResultCard(
                    result = res,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(res) },
                    onCopyIp = {
                        copyText(context, "IP", res.ip)
                        Toast.makeText(context, "IP copied: ${res.ip}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

enum class ResultSortOption(val label: String) {
    LATENCY("Latency"),
    JITTER("Jitter"),
    SPEED("Download")
}

private val RESULT_REGION_OPTIONS = listOf("All", "Asia", "Europe", "US", "North America", "Middle East", "Oceania", "South America", "Africa", "Unknown")

private fun filterResultsByRegion(results: List<IpResult>, region: String): List<IpResult> {
    return if (region == "All") results else results.filter { it.region == region }
}

private fun sortResults(results: List<IpResult>, sort: ResultSortOption): List<IpResult> {
    return when (sort) {
        ResultSortOption.LATENCY -> results.sortedBy { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }
        ResultSortOption.JITTER -> results.sortedBy { if (it.jitterMs > 0) it.jitterMs else Int.MAX_VALUE }
        ResultSortOption.SPEED -> results.sortedByDescending { it.phase2Speed }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultControls(
    selectedRegion: String,
    selectedSort: ResultSortOption,
    onRegionSelected: (String) -> Unit,
    onSortSelected: (ResultSortOption) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CompactDropdown(
            label = "Region",
            value = selectedRegion,
            options = RESULT_REGION_OPTIONS,
            onSelected = onRegionSelected,
            modifier = Modifier.weight(1f)
        )
        CompactDropdown(
            label = "Sort",
            value = selectedSort.label,
            options = ResultSortOption.entries.map { it.label },
            onSelected = { label -> ResultSortOption.entries.firstOrNull { it.label == label }?.let(onSortSelected) },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelected(option)
                    expanded = false
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
private fun IpResultCard(result: IpResult, isFavorite: Boolean, onToggleFavorite: () -> Unit, onCopyIp: () -> Unit) {
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
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                            Icon(if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "Favorite IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                        }
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
                    Text("Jitter: ${result.jitterMs}ms", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.ip, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${countryFlag(result.countryCode)} ${result.datacenterName} ${result.colo}", fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Port: ${result.port} | ${result.region} | Jitter: ${result.jitterMs} ms", fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "Favorite IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onCopyIp, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("${result.latencyMs} ms", color = if (result.isHealthy) MehrpolSuccess else MehrpolError, fontWeight = FontWeight.Bold)
                    Text("Loss: ${String.format(Locale.US, "%.2f", result.loss)}%", fontSize = 12.sp, color = Color.Gray)
                    if (result.phase2Speed > 0) Text(formatSpeed(result.phase2Speed), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun SpeedTestPanel(state: SpeedTestUiState, onRun: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Speed Test", fontWeight = FontWeight.Bold, color = MehrpolCyan)
                    Text("Download and upload check across healthy IPs", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Button(
                    onClick = onRun,
                    enabled = !state.isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background)
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (state.isRunning) "Testing" else "Run", fontWeight = FontWeight.Bold)
                }
            }
            if (state.isRunning || state.progress > 0f) {
                LinearProgressIndicator(
                    progress = { state.progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MehrpolCyan,
                    trackColor = Color.Gray.copy(alpha = 0.25f)
                )
            }
            state.error?.let { Text(it, color = MehrpolError, fontSize = 12.sp) }
            state.results.take(5).forEach { result ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(result.endpoint, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Text(
                        if (result.isRunning) "Queued" else String.format(Locale.US, "%.1f / %.1f Mbps", result.downloadMbps, result.uploadMbps),
                        color = if (result.downloadMbps > 0.0) MehrpolSuccess else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WarpScannerScreen(state: WarpScannerUiState, onRun: () -> Unit, context: Context) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Warp Scanner", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Find the lowest-latency WireGuard endpoint and generate a config", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            Button(
                onClick = onRun,
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isRunning) "Scanning..." else "Scan Warp Endpoints", fontWeight = FontWeight.Bold)
            }
        }
        state.error?.let { item { Text(it, color = MehrpolError, fontWeight = FontWeight.Medium) } }
        if (state.generatedConfig.isNotBlank()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Best WireGuard config", color = MehrpolSuccess, fontWeight = FontWeight.Bold)
                        Text(state.generatedConfig, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 8, overflow = TextOverflow.Ellipsis)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    copyText(context, "WireGuard config", state.generatedConfig)
                                    Toast.makeText(context, "Config copied", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier.weight(1f)
                            ) { Text("Copy", fontWeight = FontWeight.Bold) }
                            OutlinedButton(
                                onClick = { shareText(context, "mehrpol WireGuard config", state.generatedConfig) },
                                border = BorderStroke(1.dp, MehrpolCyan),
                                modifier = Modifier.weight(1f)
                            ) { Text("Export", color = MehrpolCyan, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
        items(state.results) { result -> WarpEndpointRow(result) }
    }
}

@Composable
private fun WarpEndpointRow(result: WarpEndpointResult) {
    val color = if (result.isReachable) MehrpolSuccess else MehrpolError
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (result.isReachable) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.endpoint, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.US, "loss %.0f%%", result.packetLoss), color = Color.Gray, fontSize = 12.sp)
            }
            Text(if (result.latencyMs >= 0) "${result.latencyMs} ms" else "failed", color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProxyCheckerScreen(state: ProxyCheckerUiState, onRun: (String) -> Unit, context: Context) {
    var input by remember(state.input) { mutableStateOf(state.input) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Proxy Checker", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Paste VLESS, VMess, Trojan, Shadowsocks, or Clash entries", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Configs or subscription text") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = { onRun(input) },
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.isRunning) "Testing..." else "Test Configs", fontWeight = FontWeight.Bold) }
        }
        state.error?.let { item { Text(it, color = MehrpolError, fontWeight = FontWeight.Medium) } }
        items(state.results) { result -> ProxyResultRow(result, context) }
    }
}

@Composable
fun SubscriptionParserScreen(state: SubscriptionParserUiState, onRun: (String) -> Unit, context: Context) {
    var input by remember(state.input) { mutableStateOf(state.input) }
    val best = state.results.firstOrNull { it.isWorking }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Subscription Parser", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Accepts subscription URL, base64 text, V2Ray/Xray links, and Clash lists", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("URL or encoded subscription") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = { onRun(input) },
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.isRunning) "Parsing..." else "Parse and Test", fontWeight = FontWeight.Bold) }
        }
        state.error?.let { item { Text(it, color = MehrpolError, fontWeight = FontWeight.Medium) } }
        if (best != null) {
            item {
                Button(
                    onClick = {
                        copyText(context, "Best config", best.config)
                        Toast.makeText(context, "Best config copied", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MehrpolSuccess, contentColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Copy Best Config (${best.latencyMs} ms)", fontWeight = FontWeight.Bold) }
            }
        }
        item { Text("Parsed ${state.parsedConfigs.size} configs", color = Color.Gray, fontSize = 12.sp) }
        items(state.results) { result -> ProxyResultRow(result, context) }
    }
}

@Composable
private fun ProxyResultRow(result: ProxyCheckResult, context: Context) {
    val color = if (result.isWorking) MehrpolSuccess else MehrpolError
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (result.isWorking) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${result.protocol} ${result.host}:${result.port}", color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(result.detail, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(if (result.isWorking) "${result.latencyMs} ms" else "Failed", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            IconButton(onClick = {
                copyText(context, "Proxy config", result.config)
                Toast.makeText(context, "Config copied", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy config", tint = MehrpolCyan)
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

private fun shareText(context: Context, title: String, text: String) {
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_SUBJECT, title)
        putExtra(android.content.Intent.EXTRA_TEXT, text)
    }
    context.startActivity(android.content.Intent.createChooser(intent, title))
}

private fun buildShareResultsText(results: List<IpResult>): String {
    return results.take(100).joinToString("
") { result ->
        val status = if (result.phase2Status || result.isHealthy) "healthy" else "failed"
        "${result.ip}:${result.port} | $status | ${result.latencyMs} ms | ${result.colo} | ${formatSpeed(result.phase2Speed)}"
    }
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
    spoofState: SniSpoofUiState,
    config: ScanConfig,
    onRunCheck: (String, String, String) -> Unit,
    onRunSpoofCheck: (String, String, String) -> Unit,
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
        item {
            SniSpoofSection(
                state = spoofState,
                onRunSpoofCheck = onRunSpoofCheck
            )
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
fun DiagnosticsScreen(state: DiagnosticsUiState, onStart: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Diagnostics", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Pinpoint DNS, routing, socket, HTTP, and HTTPS failures", fontSize = 12.sp, color = Color.Gray)
        }
        item { RadarPulseIcon(state.isRunning) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                IpStatusCardView(state.ipv4Status, Modifier.weight(1f))
                IpStatusCardView(state.ipv6Status, Modifier.weight(1f))
            }
        }
        item {
            Button(
                onClick = onStart,
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.background)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Running...")
                } else {
                    Text("Start Diagnostic", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Text("Diagnostic checklist", fontWeight = FontWeight.Bold, color = MehrpolCyan)
        }
        val rows = state.results.ifEmpty {
            listOf("DNS Resolution", "IPv4 Socket", "IPv6 Socket", "TCP routing", "HTTP", "HTTPS").map {
                ProbeResult(it, false, 0, "Not tested")
            }
        }
        items(rows) { result -> ProbeResultRow(result, state.results.isNotEmpty()) }
    }
}

@Composable
private fun RadarPulseIcon(isRunning: Boolean) {
    val transition = rememberInfiniteTransition(label = "radar")
    val pulse by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Restart),
        label = "pulse"
    )
    Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.minDimension * 0.22f
        val sweepRadius = baseRadius + (size.minDimension * 0.22f * if (isRunning) pulse else 0.45f)
        drawCircle(MehrpolCyan.copy(alpha = 0.10f), radius = sweepRadius, center = center, style = Stroke(width = 3.dp.toPx()))
        drawCircle(MehrpolCyan.copy(alpha = 0.22f), radius = baseRadius * 1.35f, center = center, style = Stroke(width = 2.dp.toPx()))
        drawCircle(MehrpolCyan.copy(alpha = 0.35f), radius = baseRadius * 0.72f, center = center, style = Stroke(width = 2.dp.toPx()))
        drawCircle(MehrpolCyan, radius = 10.dp.toPx(), center = center)
        drawLine(MehrpolCyan.copy(alpha = 0.8f), center, Offset(center.x + baseRadius * 1.55f, center.y - baseRadius * 0.55f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
private fun IpStatusCardView(status: IpStatusCard, modifier: Modifier = Modifier) {
    val color = when (status.isAvailable) {
        true -> MehrpolSuccess
        false -> MehrpolError
        null -> Color.Gray
    }
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(status.label, fontWeight = FontWeight.Bold)
            Text(
                text = when (status.isAvailable) {
                    true -> "Available"
                    false -> "Blocked"
                    null -> "Not tested"
                },
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(status.latencyMs?.let { "$it ms" } ?: "--", color = Color.Gray, fontSize = 12.sp)
            Text(status.detail, color = Color.Gray, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ProbeResultRow(result: ProbeResult, hasRun: Boolean) {
    val success = hasRun && result.isSuccess
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (success) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (success) MehrpolSuccess else if (hasRun) MehrpolError else Color.Gray
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.name, fontWeight = FontWeight.Bold)
                Text(result.detail, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(if (hasRun) "${result.latencyMs} ms" else "--", color = if (success) MehrpolSuccess else Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DomainsScreen(
    state: DomainsUiState,
    onAddDomain: (String) -> Unit,
    onCheckAll: () -> Unit,
    onCheckDomain: (String) -> Unit
) {
    var customDomain by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Domains", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Green means reachable, red means blocked or failing", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            OutlinedTextField(
                value = customDomain,
                onValueChange = { customDomain = it },
                label = { Text("Custom domain") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        onAddDomain(customDomain)
                        customDomain = ""
                    },
                    border = BorderStroke(1.dp, MehrpolCyan),
                    modifier = Modifier.weight(1f)
                ) { Text("Add", color = MehrpolCyan, fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onCheckAll,
                    enabled = !state.isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.weight(1f)
                ) { Text(if (state.isRunning) "Checking..." else "Check All", fontWeight = FontWeight.Bold) }
            }
        }
        items(state.domains) { domain ->
            DomainResultCard(
                result = state.results[domain] ?: DomainCheckResult(domain),
                isRunning = state.isRunning,
                onCheck = { onCheckDomain(domain) }
            )
        }
    }
}

@Composable
private fun DomainResultCard(result: DomainCheckResult, isRunning: Boolean, onCheck: () -> Unit) {
    val color = when (result.isAccessible) {
        true -> MehrpolSuccess
        false -> MehrpolError
        null -> Color.Gray
    }
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(result.domain, fontWeight = FontWeight.Bold)
                Text(result.detail, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(result.latencyMs?.let { "$it ms" } ?: "--", color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onCheck, enabled = !isRunning, border = BorderStroke(1.dp, MehrpolCyan)) {
                Text("Check", color = MehrpolCyan, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DnsScreen(state: DnsUiState, onTestAll: () -> Unit) {
    val maxLatency = state.results.filter { it.isReachable }.maxOfOrNull { it.latencyMs.coerceAtLeast(1) } ?: 1L
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("DNS", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Providers are sorted by fastest successful response", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            Button(
                onClick = onTestAll,
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.isRunning) "Testing..." else "Test All", fontWeight = FontWeight.Bold) }
        }
        if (state.results.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Text("Run Test All to compare DNS latency", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
            }
        } else {
            items(state.results) { result -> DnsProviderRow(result, maxLatency) }
        }
    }
}

@Composable
private fun DnsProviderRow(result: DnsTestResult, maxLatency: Long) {
    val color = if (result.isReachable) MehrpolSuccess else MehrpolError
    val fraction = if (result.isReachable) {
        (result.latencyMs.coerceAtLeast(1).toFloat() / maxLatency.toFloat()).coerceIn(0.05f, 1f)
    } else {
        1f
    }
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(result.provider.name, fontWeight = FontWeight.Bold)
                    Text(result.provider.address, color = Color.Gray, fontSize = 12.sp)
                }
                Text(if (result.isReachable) "${result.latencyMs} ms" else "Failed", color = color, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(Color.Gray.copy(alpha = 0.18f), RoundedCornerShape(4.dp))) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).background(color, RoundedCornerShape(4.dp)))
            }
            Text(result.detail, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DnsHunterScreen(state: DnsHunterUiState, onRun: (String) -> Unit) {
    var domain by remember(state.domain) { mutableStateOf(state.domain) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("DNS Hunter", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Compares Iranian resolver answers with a trusted baseline", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            OutlinedTextField(
                value = domain,
                onValueChange = { domain = it },
                label = { Text("Domain") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = { onRun(domain) },
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.isRunning) "Testing..." else "Run DNS Hunter", fontWeight = FontWeight.Bold) }
        }
        state.error?.let { error ->
            item { Text(error, color = MehrpolError, fontWeight = FontWeight.Medium) }
        }
        if (state.baselineIps.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Trusted baseline", fontWeight = FontWeight.Bold, color = MehrpolCyan)
                        Text(state.baselineIps.joinToString(", "), color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
        items(state.results) { result -> DnsHunterResultCard(result) }
    }
}

@Composable
private fun DnsHunterResultCard(result: DnsHunterResult) {
    val color = if (result.isHijacked) MehrpolError else MehrpolSuccess
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (result.isHijacked) Icons.Default.Close else Icons.Default.Check,
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.datacenter, fontWeight = FontWeight.Bold)
                Text(result.resolver, color = Color.Gray, fontSize = 12.sp)
                Text(result.resolvedIps.joinToString(", ").ifBlank { "No answer" }, color = color, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(result.detail, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("${result.latencyMs} ms", color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SniSpoofSection(
    state: SniSpoofUiState,
    onRunSpoofCheck: (String, String, String) -> Unit
) {
    var host by remember { mutableStateOf("") }
    var sniValues by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("443") }
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("SNI Spoof Check", fontWeight = FontWeight.Bold, color = MehrpolCyan)
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Host or IP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sniValues,
                onValueChange = { sniValues = it },
                label = { Text("SNI values, separated by comma or newline") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = port,
                onValueChange = { port = it.filter(Char::isDigit).take(5) },
                label = { Text("Port") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onRunSpoofCheck(host, sniValues, port) },
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolPrimary, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.isRunning) "Checking..." else "Check SNI Spoof", fontWeight = FontWeight.Bold) }
            state.error?.let { Text(it, color = MehrpolError, fontWeight = FontWeight.Medium) }
            state.results.forEach { result ->
                val open = result.isValid
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (open) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (open) MehrpolSuccess else MehrpolError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.sni, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(result.message, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(if (open) "Open" else "Blocked", color = if (open) MehrpolSuccess else MehrpolError, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun FavoritesScreen(
    favorites: List<FavoriteIp>,
    autoReplacedConfig: String,
    onRemoveFavorite: (String, Int) -> Unit,
    context: Context
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Favorites", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Save scan results with the star button", fontSize = 12.sp, color = Color.Gray)
        }
        if (autoReplacedConfig.isNotBlank()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Auto-replaced config", fontWeight = FontWeight.Bold, color = MehrpolSuccess)
                        Text(autoReplacedConfig, fontSize = 12.sp, color = Color.Gray, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Button(
                            onClick = {
                                copyText(context, "Auto-replaced config", autoReplacedConfig)
                                Toast.makeText(context, "Config copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Copy Updated Config", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
        if (favorites.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Text("No favorite IPs yet", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
            }
        } else {
            item { FavoriteComparisonChart(favorites.take(5)) }
            items(favorites) { favorite ->
                FavoriteCard(favorite = favorite, onRemove = { onRemoveFavorite(favorite.ip, favorite.port) })
            }
        }
    }
}

@Composable
private fun FavoriteCard(favorite: FavoriteIp, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${favorite.ip}:${favorite.port}", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${countryFlag(favorite.countryCode)} ${favorite.datacenterName} ${favorite.colo}", color = Color.Gray, fontSize = 12.sp)
                Text("${favorite.latencyMs} ms | jitter ${favorite.jitterMs} ms | ${favorite.region}", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove favorite", tint = MehrpolError)
            }
        }
    }
}

@Composable
private fun FavoriteComparisonChart(favorites: List<FavoriteIp>) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Compare up to 5", fontWeight = FontWeight.Bold, color = MehrpolCyan)
            val maxLatency = favorites.maxOfOrNull { it.latencyMs.coerceAtLeast(1) } ?: 1
            favorites.forEach { favorite ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(favorite.ip.substringAfterLast('.'), modifier = Modifier.width(34.dp), fontSize = 11.sp, color = Color.Gray)
                    Box(modifier = Modifier.weight(1f).height(18.dp).background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(4.dp))) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((favorite.latencyMs.coerceAtLeast(1).toFloat() / maxLatency.toFloat()).coerceIn(0.05f, 1f))
                                .background(if (favorite.latencyMs == favorites.minOf { it.latencyMs }) MehrpolSuccess else MehrpolCyan, RoundedCornerShape(4.dp))
                        )
                    }
                    Text("${favorite.latencyMs} ms", modifier = Modifier.width(58.dp), fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MonitorScreen(
    state: MonitorUiState,
    favorites: List<FavoriteIp>,
    onToggleSchedule: () -> Unit,
    onRunFlood: () -> Unit,
    onIntervalChanged: (Int) -> Unit,
    onClearNotification: () -> Unit,
    context: Context
) {
    LaunchedEffect(state.notificationMessage) {
        state.notificationMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            sendBetterIpNotification(context, it)
            onClearNotification()
        }
    }
    LaunchedEffect(favorites, state.lastSamples) {
        BestIpWidgetProvider.update(
            context,
            buildWidgetText(favorites, state.lastSamples)
        )
    }
    var intervalText by remember(state.intervalSeconds) { mutableStateOf(state.intervalSeconds.toString()) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { onToggleSchedule() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text("Monitor", style = MaterialTheme.typography.headlineSmall, color = MehrpolCyan, fontWeight = FontWeight.Bold)
            Text("Background tests run while the app process is alive", fontSize = 12.sp, color = Color.Gray)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = intervalText,
                        onValueChange = {
                            intervalText = it.filter(Char::isDigit).take(4)
                            intervalText.toIntOrNull()?.let(onIntervalChanged)
                        },
                        label = { Text("Interval seconds") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (!state.isScheduled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onToggleSchedule()
                            }
                        },
                        enabled = favorites.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (state.isScheduled) MehrpolError else MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.isScheduled) "Stop Scheduled Tests" else "Schedule Saved IP Tests", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onRunFlood,
                        enabled = favorites.isNotEmpty() && !state.isFloodRunning,
                        border = BorderStroke(1.dp, MehrpolCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isFloodRunning) "Testing..." else "Run Ping Flood Stability Test", color = MehrpolCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item { BestIpWidgetPreview(favorites, state.lastSamples) }
        if (state.lastSamples.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Text("No monitor samples yet", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
            }
        } else {
            items(state.lastSamples) { sample -> MonitorSampleCard(sample) }
        }
    }
}


private fun buildWidgetText(favorites: List<FavoriteIp>, samples: List<MonitorSample>): String {
    val bestSample = samples.firstOrNull { it.latencyMs > 0 }
    if (bestSample != null) return "${bestSample.ip} | ${bestSample.latencyMs} ms"
    val bestFavorite = favorites.minByOrNull { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }
    return bestFavorite?.let { "${it.ip} | ${it.latencyMs} ms" } ?: "No saved IP"
}

private fun sendBetterIpNotification(context: Context, message: String) {
    val channelId = "mehrpol_monitor"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Monitor alerts", NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("mehrpol better IP found")
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setAutoCancel(true)
        .build()
    try {
        NotificationManagerCompat.from(context).notify(4401, notification)
    } catch (_: SecurityException) {
        Toast.makeText(context, "Notification permission is required for push alerts", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun BestIpWidgetPreview(favorites: List<FavoriteIp>, samples: List<MonitorSample>) {
    val bestSample = samples.firstOrNull { it.latencyMs > 0 }
    val bestFavorite = favorites.minByOrNull { if (it.latencyMs > 0) it.latencyMs else Int.MAX_VALUE }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101820)),
        modifier = Modifier.fillMaxWidth().border(1.dp, MehrpolCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Home Widget", color = Color.Gray, fontSize = 12.sp)
            Text(bestSample?.ip ?: bestFavorite?.ip ?: "No saved IP", fontWeight = FontWeight.Bold, color = MehrpolCyan)
            Text(
                text = bestSample?.let { "${it.latencyMs} ms | ${it.stabilityPercent}% stable" }
                    ?: bestFavorite?.let { "${it.latencyMs} ms | ${it.datacenterName}" }
                    ?: "Save favorites to populate the widget",
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun MonitorSampleCard(sample: MonitorSample) {
    Card(colors = CardDefaults.cardColors(containerColor = MehrpolDarkSurface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${sample.ip}:${sample.port}", fontWeight = FontWeight.Bold)
                Text("${sample.checkedAt} | ${sample.stabilityPercent}% stability", color = Color.Gray, fontSize = 12.sp)
            }
            Text(if (sample.latencyMs > 0) "${sample.latencyMs} ms" else "Failed", color = if (sample.isBetter) MehrpolSuccess else Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigGeneratorSection(
    config: ScanConfig,
    onConfigChanged: (ScanConfig) -> Unit,
    onGeneratedConfig: (String) -> Unit,
    context: Context
) {
    var showQr by remember { mutableStateOf(false) }
    val generated = config.generatedConfig
    val qrContent = generated.ifBlank { config.configUrl }
    if (showQr && qrContent.isNotBlank()) {
        QrCodeDialog(
            content = qrContent,
            onDismiss = { showQr = false },
            onShare = {
                copyText(context, "Generated config", qrContent)
                Toast.makeText(context, "Config copied for sharing", Toast.LENGTH_SHORT).show()
            }
        )
    }
    SettingSection("Config Generators", "build fragment, WARP, VMess, VLESS, or Shadowsocks configs") {
        OutlinedTextField(
            value = config.cleanIp,
            onValueChange = { onConfigChanged(config.copy(cleanIp = it.trim())) },
            label = { Text("Clean IP") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = config.fragmentInterval,
                onValueChange = { onConfigChanged(config.copy(fragmentInterval = it.filter(Char::isDigit).take(4))) },
                label = { Text("Interval") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.fragmentPackets,
                onValueChange = { onConfigChanged(config.copy(fragmentPackets = it.filter(Char::isDigit).take(3))) },
                label = { Text("Packets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CompactDropdown(
            label = "Protocol",
            value = config.protocolType,
            options = listOf("VLESS", "VMess", "Shadowsocks", "WARP Fragment"),
            onSelected = { onConfigChanged(config.copy(protocolType = it)) },
            modifier = Modifier.fillMaxWidth()
        )
        if (config.protocolType == "Shadowsocks") {
            Spacer(modifier = Modifier.height(8.dp))
            CompactDropdown(
                label = "SS Method",
                value = config.shadowsocksMethod,
                options = listOf("chacha20-ietf-poly1305", "aes-128-gcm", "aes-256-gcm"),
                onSelected = { onConfigChanged(config.copy(shadowsocksMethod = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val content = buildGeneratedConfig(config)
                    onGeneratedConfig(content)
                    copyText(context, "Generated config", content)
                    Toast.makeText(context, "Generated config copied", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.weight(1f)
            ) { Text("Generate", fontWeight = FontWeight.Bold) }
            OutlinedButton(
                onClick = { showQr = true },
                enabled = qrContent.isNotBlank(),
                border = BorderStroke(1.dp, MehrpolCyan),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null, tint = MehrpolCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("QR", color = MehrpolCyan, fontWeight = FontWeight.Bold)
            }
        }
        if (generated.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(generated, fontSize = 12.sp, color = Color.Gray, maxLines = 5, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun QrCodeDialog(content: String, onDismiss: () -> Unit, onShare: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Config QR") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                SimpleQrCode(content, modifier = Modifier.size(220.dp))
                Text(content, color = Color.Gray, fontSize = 11.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        },
        confirmButton = {
            TextButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        containerColor = MehrpolDarkSurface
    )
}

@Composable
private fun SimpleQrCode(content: String, modifier: Modifier = Modifier) {
    val matrix = remember(content) { buildQrMatrix(content) }
    Canvas(modifier = modifier.background(Color.White, RoundedCornerShape(4.dp)).padding(10.dp)) {
        val cell = size.minDimension / matrix.size.toFloat()
        matrix.forEachIndexed { y, row ->
            row.forEachIndexed { x, dark ->
                if (dark) {
                    drawRect(Color.Black, topLeft = Offset(x * cell, y * cell), size = Size(cell, cell))
                }
            }
        }
    }
}

private data class QrVersionSpec(
    val version: Int,
    val dataCodewords: Int,
    val ecCodewordsPerBlock: Int,
    val blockDataSizes: List<Int>
)

private val QR_VERSION_SPECS = listOf(
    QrVersionSpec(1, 19, 7, listOf(19)),
    QrVersionSpec(2, 34, 10, listOf(34)),
    QrVersionSpec(3, 55, 15, listOf(55)),
    QrVersionSpec(4, 80, 20, listOf(80)),
    QrVersionSpec(5, 108, 26, listOf(108)),
    QrVersionSpec(6, 136, 18, listOf(68, 68)),
    QrVersionSpec(7, 156, 20, listOf(78, 78)),
    QrVersionSpec(8, 194, 24, listOf(97, 97)),
    QrVersionSpec(9, 232, 30, listOf(116, 116)),
    QrVersionSpec(10, 274, 18, listOf(68, 68, 69, 69))
)

private val QR_ALIGNMENT_POSITIONS = mapOf(
    1 to emptyList<Int>(),
    2 to listOf(6, 18),
    3 to listOf(6, 22),
    4 to listOf(6, 26),
    5 to listOf(6, 30),
    6 to listOf(6, 34),
    7 to listOf(6, 22, 38),
    8 to listOf(6, 24, 42),
    9 to listOf(6, 26, 46),
    10 to listOf(6, 28, 50)
)

private fun buildQrMatrix(content: String): List<List<Boolean>> {
    val payload = content.toByteArray(Charsets.UTF_8)
    val spec = QR_VERSION_SPECS.firstOrNull { canFitQrPayload(payload.size, it.version, it.dataCodewords) }
        ?: QR_VERSION_SPECS.last()
    val data = encodeQrData(payload.take(qrByteCapacity(spec.version, spec.dataCodewords)).toByteArray(), spec)
    val codewords = addQrErrorCorrection(data, spec)
    val size = spec.version * 4 + 17
    val modules = Array(size) { BooleanArray(size) }
    val reserved = Array(size) { BooleanArray(size) }
    drawQrFunctionPatterns(modules, reserved, spec.version)
    drawQrCodewords(modules, reserved, codewords)
    drawQrFormatBits(modules, reserved, mask = 0)
    return modules.map { it.toList() }
}

private fun canFitQrPayload(byteCount: Int, version: Int, dataCodewords: Int): Boolean {
    return 4 + qrCharCountBits(version) + byteCount * 8 <= dataCodewords * 8
}

private fun qrByteCapacity(version: Int, dataCodewords: Int): Int {
    return ((dataCodewords * 8) - 4 - qrCharCountBits(version)) / 8
}

private fun qrCharCountBits(version: Int): Int = if (version <= 9) 8 else 16

private fun encodeQrData(payload: ByteArray, spec: QrVersionSpec): List<Int> {
    val bits = mutableListOf<Int>()
    appendBits(bits, 0x4, 4)
    appendBits(bits, payload.size, qrCharCountBits(spec.version))
    payload.forEach { appendBits(bits, it.toInt() and 0xFF, 8) }
    val capacityBits = spec.dataCodewords * 8
    repeat(minOf(4, capacityBits - bits.size).coerceAtLeast(0)) { bits += 0 }
    while (bits.size % 8 != 0) bits += 0
    val bytes = bits.chunked(8).map { chunk -> chunk.fold(0) { acc, bit -> (acc shl 1) or bit } }.toMutableList()
    var pad = 0
    while (bytes.size < spec.dataCodewords) {
        bytes += if (pad % 2 == 0) 0xEC else 0x11
        pad++
    }
    return bytes
}

private fun appendBits(bits: MutableList<Int>, value: Int, count: Int) {
    for (i in count - 1 downTo 0) bits += (value ushr i) and 1
}

private fun addQrErrorCorrection(data: List<Int>, spec: QrVersionSpec): List<Int> {
    val blocks = mutableListOf<List<Int>>()
    var offset = 0
    spec.blockDataSizes.forEach { size ->
        blocks += data.subList(offset, offset + size)
        offset += size
    }
    val ecBlocks = blocks.map { reedSolomonRemainder(it, spec.ecCodewordsPerBlock) }
    val result = mutableListOf<Int>()
    val maxDataSize = blocks.maxOf { it.size }
    for (i in 0 until maxDataSize) {
        blocks.forEach { block -> if (i < block.size) result += block[i] }
    }
    for (i in 0 until spec.ecCodewordsPerBlock) {
        ecBlocks.forEach { block -> result += block[i] }
    }
    return result
}

private fun reedSolomonRemainder(data: List<Int>, degree: Int): List<Int> {
    val generator = reedSolomonGenerator(degree)
    val result = MutableList(degree) { 0 }
    data.forEach { value ->
        val factor = value xor result.removeAt(0)
        result += 0
        for (i in 0 until degree) {
            result[i] = result[i] xor gfMultiply(generator[i], factor)
        }
    }
    return result
}

private fun reedSolomonGenerator(degree: Int): List<Int> {
    var result = mutableListOf(1)
    repeat(degree) { i ->
        val next = MutableList(result.size + 1) { 0 }
        result.forEachIndexed { index, coefficient ->
            next[index] = next[index] xor gfMultiply(coefficient, 1)
            next[index + 1] = next[index + 1] xor gfMultiply(coefficient, gfPow(2, i))
        }
        result = next
    }
    return result.drop(1)
}

private fun gfMultiply(a: Int, b: Int): Int {
    var x = a
    var y = b
    var result = 0
    while (y != 0) {
        if ((y and 1) != 0) result = result xor x
        x = if ((x and 0x80) != 0) ((x shl 1) xor 0x11D) and 0xFF else (x shl 1) and 0xFF
        y = y ushr 1
    }
    return result
}

private fun gfPow(value: Int, power: Int): Int {
    var result = 1
    repeat(power) { result = gfMultiply(result, value) }
    return result
}

private fun drawQrFunctionPatterns(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, version: Int) {
    val size = modules.size
    drawFinderPattern(modules, reserved, 3, 3)
    drawFinderPattern(modules, reserved, size - 4, 3)
    drawFinderPattern(modules, reserved, 3, size - 4)
    for (i in 8 until size - 8) {
        setQrFunction(modules, reserved, 6, i, i % 2 == 0)
        setQrFunction(modules, reserved, i, 6, i % 2 == 0)
    }
    QR_ALIGNMENT_POSITIONS[version].orEmpty().forEach { y ->
        QR_ALIGNMENT_POSITIONS[version].orEmpty().forEach { x ->
            val nearFinder = (x == 6 && y == 6) || (x == 6 && y == size - 7) || (x == size - 7 && y == 6)
            if (!nearFinder) drawAlignmentPattern(modules, reserved, x, y)
        }
    }
    setQrFunction(modules, reserved, 8, size - 8, true)
    if (version >= 7) drawQrVersionBits(modules, reserved, version)
    for (i in 0..8) {
        setReserved(reserved, 8, i)
        setReserved(reserved, i, 8)
    }
    for (i in 0..7) {
        setReserved(reserved, size - 1 - i, 8)
        setReserved(reserved, 8, size - 1 - i)
    }
}

private fun drawFinderPattern(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, centerX: Int, centerY: Int) {
    for (dy in -4..4) {
        for (dx in -4..4) {
            val x = centerX + dx
            val y = centerY + dy
            if (x !in modules.indices || y !in modules.indices) continue
            val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
            setQrFunction(modules, reserved, x, y, dist != 2 && dist != 4)
        }
    }
}

private fun drawAlignmentPattern(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, centerX: Int, centerY: Int) {
    for (dy in -2..2) {
        for (dx in -2..2) {
            val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
            setQrFunction(modules, reserved, centerX + dx, centerY + dy, dist != 1)
        }
    }
}

private fun drawQrVersionBits(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, version: Int) {
    val size = modules.size
    val bits = qrVersionBits(version)
    for (i in 0 until 18) {
        val bit = ((bits ushr i) and 1) != 0
        val x = size - 11 + (i % 3)
        val y = i / 3
        setQrFunction(modules, reserved, x, y, bit)
        setQrFunction(modules, reserved, y, x, bit)
    }
}

private fun qrVersionBits(version: Int): Int {
    var remainder = version
    repeat(12) {
        remainder = (remainder shl 1) xor if (((remainder ushr 11) and 1) != 0) 0x1F25 else 0
    }
    return (version shl 12) or (remainder and 0xFFF)
}

private fun drawQrCodewords(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, codewords: List<Int>) {
    val bits = codewords.flatMap { byte -> (7 downTo 0).map { (byte ushr it) and 1 } }
    val size = modules.size
    var bitIndex = 0
    var x = size - 1
    var upward = true
    while (x > 0) {
        if (x == 6) x--
        val yRange = if (upward) size - 1 downTo 0 else 0 until size
        for (y in yRange) {
            for (dx in 0..1) {
                val xx = x - dx
                if (reserved[y][xx]) continue
                val bit = bitIndex < bits.size && bits[bitIndex] == 1
                modules[y][xx] = bit xor qrMask(0, xx, y)
                bitIndex++
            }
        }
        upward = !upward
        x -= 2
    }
}

private fun qrMask(mask: Int, x: Int, y: Int): Boolean {
    return when (mask) {
        0 -> (x + y) % 2 == 0
        else -> false
    }
}

private fun drawQrFormatBits(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, mask: Int) {
    val size = modules.size
    val bits = qrFormatBits(mask)
    for (i in 0..5) setQrFunction(modules, reserved, 8, i, ((bits ushr i) and 1) != 0)
    setQrFunction(modules, reserved, 8, 7, ((bits ushr 6) and 1) != 0)
    setQrFunction(modules, reserved, 8, 8, ((bits ushr 7) and 1) != 0)
    setQrFunction(modules, reserved, 7, 8, ((bits ushr 8) and 1) != 0)
    for (i in 9..14) setQrFunction(modules, reserved, 14 - i, 8, ((bits ushr i) and 1) != 0)
    for (i in 0..7) setQrFunction(modules, reserved, size - 1 - i, 8, ((bits ushr i) and 1) != 0)
    for (i in 8..14) setQrFunction(modules, reserved, 8, size - 15 + i, ((bits ushr i) and 1) != 0)
}

private fun qrFormatBits(mask: Int): Int {
    val data = (1 shl 3) or mask
    var remainder = data
    repeat(10) {
        remainder = (remainder shl 1) xor if (((remainder ushr 9) and 1) != 0) 0x537 else 0
    }
    return ((data shl 10) or (remainder and 0x3FF)) xor 0x5412
}

private fun setQrFunction(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, x: Int, y: Int, dark: Boolean) {
    modules[y][x] = dark
    reserved[y][x] = true
}

private fun setReserved(reserved: Array<BooleanArray>, x: Int, y: Int) {
    if (y in reserved.indices && x in reserved.indices) reserved[y][x] = true
}

private fun buildGeneratedConfig(config: ScanConfig): String {
    val ip = config.cleanIp.ifBlank { "162.159.192.1" }
    val port = config.selectedPorts.firstOrNull() ?: 443
    val interval = config.fragmentInterval.ifBlank { "30" }
    val packets = config.fragmentPackets.ifBlank { "8" }
    return when (config.protocolType) {
        "VMess" -> buildVmessLink(ip, port)
        "Shadowsocks" -> buildShadowsocksLink(ip, port, config.shadowsocksMethod)
        "WARP Fragment" -> "warp://$ip:$port?ifp=$packets&ifps=$interval&ifpd=3#mehrpol-warp-$ip"
        else -> "vless://00000000-0000-0000-0000-000000000000@$ip:$port?encryption=none&security=tls&type=tcp&fp=randomized&fragment=$packets,$interval#mehrpol-$ip"
    }
}

private fun buildVmessLink(ip: String, port: Int): String {
    val json = """{"v":"2","ps":"mehrpol-$ip","add":"$ip","port":"$port","id":"00000000-0000-0000-0000-000000000000","aid":"0","scy":"auto","net":"tcp","type":"none","host":"","path":"/","tls":"tls","sni":""}"""
    return "vmess://${android.util.Base64.encodeToString(json.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)}"
}

private fun buildShadowsocksLink(ip: String, port: Int, method: String): String {
    val userInfo = android.util.Base64.encodeToString("$method:mehrpol".toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
    return "ss://$userInfo@$ip:$port#mehrpol-$ip"
}

private fun formatSpeed(speed: Double): String {
    return if (speed > 1024 * 1024) String.format(Locale.US, "%.2f MB/s", speed / (1024 * 1024)) else String.format(Locale.US, "%.0f KB/s", speed / 1024)
}

private fun countryFlag(countryCode: String): String {
    if (countryCode.length != 2) return ""
    val first = Character.codePointAt(countryCode.uppercase(Locale.US), 0) - 'A'.code + 0x1F1E6
    val second = Character.codePointAt(countryCode.uppercase(Locale.US), 1) - 'A'.code + 0x1F1E6
    return String(Character.toChars(first)) + String(Character.toChars(second))
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
fun SettingsScreen(
    uiState: ScanUiState,
    onConfigChanged: (ScanConfig) -> Unit,
    onGeneratedConfig: (String) -> Unit,
    onThemeChanged: (AppThemeMode) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onBuildBackup: () -> String,
    onRestoreBackup: (String) -> Unit
) {
    val config = uiState.config
    val settings = uiState.settings
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
        item {
            SettingSection("Appearance", "Material 3 theme and language") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.themeMode == AppThemeMode.DARK,
                        onClick = { onThemeChanged(AppThemeMode.DARK) },
                        label = { Text("Dark") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MehrpolCyan, selectedLabelColor = MaterialTheme.colorScheme.background)
                    )
                    FilterChip(
                        selected = settings.themeMode == AppThemeMode.LIGHT,
                        onClick = { onThemeChanged(AppThemeMode.LIGHT) },
                        label = { Text("Light") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MehrpolCyan, selectedLabelColor = MaterialTheme.colorScheme.background)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CompactDropdown(
                    label = "Language",
                    value = settings.language.label,
                    options = AppLanguage.entries.map { it.label },
                    onSelected = { label -> AppLanguage.entries.firstOrNull { it.label == label }?.let(onLanguageChanged) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (settings.language == AppLanguage.FARSI) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("پشتیبانی فارسی فعال است", color = MehrpolSuccess, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            var restoreText by remember { mutableStateOf("") }
            SettingSection("Backup and Restore", "export scanner settings as JSON") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val backup = onBuildBackup()
                            copyText(context, "mehrpol backup", backup)
                            Toast.makeText(context, "Backup copied", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MehrpolCyan, contentColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.weight(1f)
                    ) { Text("Backup", fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = { shareText(context, "mehrpol backup", settings.backupText.ifBlank { onBuildBackup() }) },
                        border = BorderStroke(1.dp, MehrpolCyan),
                        modifier = Modifier.weight(1f)
                    ) { Text("Share", color = MehrpolCyan, fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = restoreText,
                    onValueChange = { restoreText = it },
                    label = { Text("Paste backup JSON") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onRestoreBackup(restoreText) },
                    enabled = restoreText.isNotBlank(),
                    border = BorderStroke(1.dp, MehrpolCyan),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Restore", color = MehrpolCyan, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            SettingSection("Share Results", "send current scan results as text") {
                Button(
                    onClick = { shareText(context, "mehrpol scan results", buildShareResultsText(uiState.results.ifEmpty { uiState.persistedResults })) },
                    enabled = uiState.results.isNotEmpty() || uiState.persistedResults.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MehrpolPrimary, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Results", fontWeight = FontWeight.Bold)
                }
            }
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
            ConfigGeneratorSection(
                config = config,
                onConfigChanged = onConfigChanged,
                onGeneratedConfig = onGeneratedConfig,
                context = context
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
