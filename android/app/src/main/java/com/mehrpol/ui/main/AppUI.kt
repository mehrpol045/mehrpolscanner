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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.AutoGraph
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
import kotlinx.coroutines.launch
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
    var selectedDestination by remember { mutableStateOf(DrawerDestination.Home) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
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
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MehrpolDrawerContent(
                selectedDestination = selectedDestination,
                onDestinationSelected = { destination ->
                    selectedDestination = destination
                    scope.launch { drawerState.close() }
                },
                onInfoClick = {
                    showInfoDialog = true
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        text = selectedDestination.title,
                        color = Color(0xFFECF0F8),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF00D4FF))
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF384760))
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF06080D),
                    titleContentColor = Color(0xFFECF0F8)
                )
            )
        },
            floatingActionButton = {
                if (selectedDestination == DrawerDestination.Home) {
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
                when (selectedDestination) {
                    DrawerDestination.Home -> HomeScreen(
                        uiState = uiState,
                        context = context,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onConfigChanged = viewModel::updateConfig
                    )
                    DrawerDestination.History -> HistoryScreen(uiState.history)
                    DrawerDestination.Sni -> SniCheckScreen(
                        state = uiState.sniCheck,
                        spoofState = uiState.sniSpoof,
                        config = uiState.config,
                        onRunCheck = viewModel::runSniCheck,
                        onRunSpoofCheck = viewModel::runSniSpoofCheck,
                        onConfigChanged = viewModel::updateConfig
                    )
                    DrawerDestination.Favorites -> FavoritesScreen(
                        favorites = uiState.favorites,
                        autoReplacedConfig = uiState.autoReplacedConfig,
                        onRemoveFavorite = viewModel::removeFavorite,
                        context = context
                    )
                    DrawerDestination.Monitor -> MonitorScreen(
                        state = uiState.monitor,
                        favorites = uiState.favorites,
                        onToggleSchedule = viewModel::startOrStopMonitor,
                        onRunFlood = viewModel::runPingFlood,
                        onIntervalChanged = viewModel::updateMonitorInterval,
                        onClearNotification = viewModel::clearMonitorNotification,
                        context = context
                    )
                    DrawerDestination.Diagnostics -> DiagnosticsScreen(
                        state = uiState.diagnostics,
                        onStart = viewModel::runDiagnostics
                    )
                    DrawerDestination.Domains -> DomainsScreen(
                        state = uiState.domains,
                        onAddDomain = viewModel::addCustomDomain,
                        onCheckAll = viewModel::checkAllDomains,
                        onCheckDomain = viewModel::checkDomain
                    )
                    DrawerDestination.Dns -> DnsScreen(
                        state = uiState.dns,
                        onTestAll = viewModel::testAllDnsProviders
                    )
                    DrawerDestination.DnsHunter -> DnsHunterScreen(
                        state = uiState.dnsHunter,
                        onRun = viewModel::runDnsHunter
                    )
                    DrawerDestination.Settings -> SettingsScreen(
                        config = uiState.config,
                        onConfigChanged = viewModel::updateConfig,
                        onGeneratedConfig = viewModel::updateGeneratedConfig
                    )
                }
            }
        }
    }

}

private enum class DrawerCategory(val title: String) {
    Scanner("Scanner"),
    Tools("Network Tools"),
    Library("Library"),
    App("App")
}

private enum class DrawerDestination(
    val title: String,
    val category: DrawerCategory,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Home("Home", DrawerCategory.Scanner, Icons.Default.Home),
    History("History", DrawerCategory.Library, Icons.Default.History),
    Favorites("Favorites", DrawerCategory.Library, Icons.Default.Star),
    Sni("SNI Check", DrawerCategory.Tools, Icons.Default.Security),
    Monitor("Monitor", DrawerCategory.Scanner, Icons.Default.AutoGraph),
    Diagnostics("Diagnostics", DrawerCategory.Tools, Icons.Default.Radar),
    Domains("Domains", DrawerCategory.Tools, Icons.Default.Language),
    Dns("DNS", DrawerCategory.Tools, Icons.Default.Dns),
    DnsHunter("DNS Hunter", DrawerCategory.Tools, Icons.Default.Search),
    Settings("Settings", DrawerCategory.App, Icons.Default.Settings)
}

@Composable
private fun MehrpolDrawerContent(
    selectedDestination: DrawerDestination,
    onDestinationSelected: (DrawerDestination) -> Unit,
    onInfoClick: () -> Unit
) {
    val w = 280
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(w.dp)
            .background(Color(0xFF0B0F18))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1520))
                .padding(start = 20.dp, top = 28.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x1800D4FF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x4400D4FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Radar, contentDescription = null,
                            tint = Color(0xFF00D4FF), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row {
                            Text("mehrpol", fontWeight = FontWeight.Bold,
                                fontSize = 17.sp, color = Color(0xFFECF0F8))
                            Text("scanner", fontWeight = FontWeight.Bold,
                                fontSize = 17.sp, color = Color(0xFF00D4FF))
                        }
                        Text("v\${BuildConfig.VERSION_NAME}",
                            fontSize = 10.sp, color = Color(0xFF384760),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1E2D45)))
        Spacer(modifier = Modifier.height(8.dp))
        DrawerCategory.entries.forEach { category ->
            val items = DrawerDestination.entries.filter { it.category == category }
            if (items.isEmpty()) return@forEach
            Text(
                text = category.title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF384760),
                modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 6.dp)
            )
            items.forEach { destination ->
                val isSelected = selectedDestination == destination
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (isSelected) Color(0x0800D4FF) else Color.Transparent)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color(0x2200D4FF) else Color.Transparent,
                            RoundedCornerShape(11.dp)
                        )
                        .clickable { onDestinationSelected(destination) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isSelected) Color(0x1800D4FF) else Color(0xFF161E2E),
                                RoundedCornerShape(9.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0x3300D4FF) else Color(0xFF1E2D45),
                                RoundedCornerShape(9.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF00D4FF) else Color(0xFF7A8BA8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = destination.title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF00D4FF) else Color(0xFF7A8BA8)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
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
fun SettingsScreen(config: ScanConfig, onConfigChanged: (ScanConfig) -> Unit, onGeneratedConfig: (String) -> Unit) {
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
