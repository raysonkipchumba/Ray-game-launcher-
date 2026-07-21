package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUsageInfo
import com.example.data.SystemStatus
import com.example.model.GamerProfile
import com.example.model.SettingsModel
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBackground
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SettingsCategory {
    GAMER_PROFILE,
    GENERAL,
    PERFORMANCE_MODE,
    REAL_RAM_CLEANER,
    GAME_DND
}

@Composable
fun SettingsScreen(
    settings: SettingsModel,
    gamerProfile: GamerProfile,
    systemStatus: SystemStatus,
    usageStatsList: List<AppUsageInfo>,
    onUpdateSettings: (SettingsModel) -> Unit,
    onUpdateGamerProfile: (GamerProfile) -> Unit,
    onCleanRamClick: () -> Unit,
    onRefreshUsageStats: () -> Unit,
    onBackClick: () -> Unit,
    onOpenAddGame: () -> Unit
) {
    var activeCategory by remember { mutableStateOf(SettingsCategory.REAL_RAM_CLEANER) }
    var showUninstallerDialog by remember { mutableStateOf(false) }
    var showMemoryWhitelistDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Profile State for Editing
    var tagText by remember(gamerProfile) { mutableStateOf(gamerProfile.gamerTag) }
    var titleText by remember(gamerProfile) { mutableStateOf(gamerProfile.gamerTitle) }
    var bioText by remember(gamerProfile) { mutableStateOf(gamerProfile.bio) }
    var genreText by remember(gamerProfile) { mutableStateOf(gamerProfile.favoriteGenre) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(12.dp)
    ) {
        val isCompact = maxWidth < 600.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings & Memory Diagnostics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (isCompact) {
                // Horizontal Scrollable Category Filter Pills for Compact Displays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryTabChip(
                        title = "Real RAM",
                        isSelected = activeCategory == SettingsCategory.REAL_RAM_CLEANER,
                        onClick = { activeCategory = SettingsCategory.REAL_RAM_CLEANER }
                    )
                    CategoryTabChip(
                        title = "Player Profile",
                        isSelected = activeCategory == SettingsCategory.GAMER_PROFILE,
                        onClick = { activeCategory = SettingsCategory.GAMER_PROFILE }
                    )
                    CategoryTabChip(
                        title = "General",
                        isSelected = activeCategory == SettingsCategory.GENERAL,
                        onClick = { activeCategory = SettingsCategory.GENERAL }
                    )
                    CategoryTabChip(
                        title = "Performance",
                        isSelected = activeCategory == SettingsCategory.PERFORMANCE_MODE,
                        onClick = { activeCategory = SettingsCategory.PERFORMANCE_MODE }
                    )
                    CategoryTabChip(
                        title = "Game DND",
                        isSelected = activeCategory == SettingsCategory.GAME_DND,
                        onClick = { activeCategory = SettingsCategory.GAME_DND }
                    )
                }

                // Details Content in full width column
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D131A))
                        .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeCategory) {
                        SettingsCategory.REAL_RAM_CLEANER -> {
                            item {
                                Text(
                                    text = "REAL RAM CLEANER & SYSTEM USAGE STATS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            item {
                                RealRamCleanerSection(
                                    systemStatus = systemStatus,
                                    usageStatsList = usageStatsList,
                                    onCleanRamClick = onCleanRamClick,
                                    onRefreshUsageStats = onRefreshUsageStats,
                                    onOpenUninstaller = { showUninstallerDialog = true },
                                    onOpenWhitelist = { showMemoryWhitelistDialog = true }
                                )
                            }
                        }

                        SettingsCategory.GAMER_PROFILE -> {
                            item {
                                Text(
                                    text = "PLAYER PROFILE & P2P MESH BROADCASTER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            item {
                                GamerProfileSection(
                                    gamerProfile = gamerProfile,
                                    tagText = tagText,
                                    titleText = titleText,
                                    bioText = bioText,
                                    genreText = genreText,
                                    onTagChange = { tagText = it },
                                    onTitleChange = { titleText = it },
                                    onBioChange = { bioText = it },
                                    onGenreChange = { genreText = it },
                                    onSave = {
                                        onUpdateGamerProfile(
                                            gamerProfile.copy(
                                                gamerTag = tagText,
                                                gamerTitle = titleText,
                                                bio = bioText,
                                                favoriteGenre = genreText
                                            )
                                        )
                                        Toast.makeText(context, "Gamer Profile Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        SettingsCategory.GENERAL -> {
                            item {
                                Text(
                                    text = "GENERAL LAUNCHER PREFERENCES",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            item {
                                GeneralSettingsSection(
                                    settings = settings,
                                    onUpdateSettings = onUpdateSettings
                                )
                            }
                        }

                        SettingsCategory.PERFORMANCE_MODE -> {
                            item {
                                Text(
                                    text = "GAME TURBO PERFORMANCE GOVERNOR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            item {
                                PerformanceModeSection(
                                    settings = settings,
                                    onUpdateSettings = onUpdateSettings
                                )
                            }
                        }

                        SettingsCategory.GAME_DND -> {
                            item {
                                Text(
                                    text = "DO NOT DISTURB & NOTIFICATION SHIELD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )
                            }
                            item {
                                GameDndSection(
                                    settings = settings,
                                    onUpdateSettings = onUpdateSettings
                                )
                            }
                        }
                    }
                }
            } else {
                // Two-Column Layout (Left Category Sidebar vs Right Details Content)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Sidebar Column: Category Tabs
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D131A))
                            .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        CategoryTabItem(
                            title = "Real RAM & Usage",
                            icon = Icons.Default.CleaningServices,
                            isSelected = activeCategory == SettingsCategory.REAL_RAM_CLEANER,
                            onClick = { activeCategory = SettingsCategory.REAL_RAM_CLEANER }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategoryTabItem(
                            title = "Player Profile",
                            icon = Icons.Default.Badge,
                            isSelected = activeCategory == SettingsCategory.GAMER_PROFILE,
                            onClick = { activeCategory = SettingsCategory.GAMER_PROFILE }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategoryTabItem(
                            title = "General Settings",
                            icon = Icons.Default.Settings,
                            isSelected = activeCategory == SettingsCategory.GENERAL,
                            onClick = { activeCategory = SettingsCategory.GENERAL }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategoryTabItem(
                            title = "Performance Mode",
                            icon = Icons.Default.FlashOn,
                            isSelected = activeCategory == SettingsCategory.PERFORMANCE_MODE,
                            onClick = { activeCategory = SettingsCategory.PERFORMANCE_MODE }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategoryTabItem(
                            title = "Game DND",
                            icon = Icons.Default.DoNotDisturbOn,
                            isSelected = activeCategory == SettingsCategory.GAME_DND,
                            onClick = { activeCategory = SettingsCategory.GAME_DND }
                        )
                    }

                    // Right Content Column
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D131A))
                            .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (activeCategory) {
                            SettingsCategory.REAL_RAM_CLEANER -> {
                                item {
                                    Text(
                                        text = "REAL RAM CLEANER & SYSTEM USAGE STATS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                                item {
                                    RealRamCleanerSection(
                                        systemStatus = systemStatus,
                                        usageStatsList = usageStatsList,
                                        onCleanRamClick = onCleanRamClick,
                                        onRefreshUsageStats = onRefreshUsageStats,
                                        onOpenUninstaller = { showUninstallerDialog = true },
                                        onOpenWhitelist = { showMemoryWhitelistDialog = true }
                                    )
                                }
                            }

                            SettingsCategory.GAMER_PROFILE -> {
                                item {
                                    Text(
                                        text = "PLAYER PROFILE & P2P MESH BROADCASTER",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                                item {
                                    GamerProfileSection(
                                        gamerProfile = gamerProfile,
                                        tagText = tagText,
                                        titleText = titleText,
                                        bioText = bioText,
                                        genreText = genreText,
                                        onTagChange = { tagText = it },
                                        onTitleChange = { titleText = it },
                                        onBioChange = { bioText = it },
                                        onGenreChange = { genreText = it },
                                        onSave = {
                                            onUpdateGamerProfile(
                                                gamerProfile.copy(
                                                    gamerTag = tagText,
                                                    gamerTitle = titleText,
                                                    bio = bioText,
                                                    favoriteGenre = genreText
                                                )
                                            )
                                            Toast.makeText(context, "Gamer Profile Updated!", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }

                            SettingsCategory.GENERAL -> {
                                item {
                                    Text(
                                        text = "GENERAL LAUNCHER PREFERENCES",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                                item {
                                    GeneralSettingsSection(
                                        settings = settings,
                                        onUpdateSettings = onUpdateSettings
                                    )
                                }
                            }

                            SettingsCategory.PERFORMANCE_MODE -> {
                                item {
                                    Text(
                                        text = "GAME TURBO PERFORMANCE GOVERNOR",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                                item {
                                    PerformanceModeSection(
                                        settings = settings,
                                        onUpdateSettings = onUpdateSettings
                                    )
                                }
                            }

                            SettingsCategory.GAME_DND -> {
                                item {
                                    Text(
                                        text = "DO NOT DISTURB & NOTIFICATION SHIELD",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                                item {
                                    GameDndSection(
                                        settings = settings,
                                        onUpdateSettings = onUpdateSettings
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showUninstallerDialog) {
        AlertDialog(
            onDismissRequest = { showUninstallerDialog = false },
            title = { Text(text = "Uninstall Games", color = Color.White) },
            text = { Text(text = "Ray Game Launcher can manage system uninstall intents for your installed games.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showUninstallerDialog = false }) {
                    Text(text = "Close", color = CyanAccent)
                }
            },
            containerColor = DarkCardBackground
        )
    }

    if (showMemoryWhitelistDialog) {
        AlertDialog(
            onDismissRequest = { showMemoryWhitelistDialog = false },
            title = { Text(text = "Memory Exceptions Whitelist", color = Color.White) },
            text = { Text(text = "All active foreground games are automatically protected during RAM cleaning.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showMemoryWhitelistDialog = false }) {
                    Text(text = "OK", color = CyanAccent)
                }
            },
            containerColor = DarkCardBackground
        )
    }
}

@Composable
fun CategoryTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF1E2E42) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) CyanAccent else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCardBackground)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BluePrimary,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = Color(0xFF1E2A38)
                )
            )
        }
    }
}

@Composable
fun SettingActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCardBackground)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = TextMuted
            )
        }
    }
}

@Composable
fun UsageStatsAppCard(appInfo: AppUsageInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2E42)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appInfo.appName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = appInfo.packageName,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(appInfo.lastTimeUsed))
                Text(
                    text = "Last active: $timeStr",
                    fontSize = 10.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                val foregroundMinutes = (appInfo.totalForegroundTimeMs / 1000 / 60)
                Text(
                    text = "Foreground today: $foregroundMinutes m",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun CategoryTabChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CyanAccent else Color(0xFF15202B))
            .border(1.dp, if (isSelected) CyanAccent else Color(0xFF1E2A38), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else Color.White
        )
    }
}

@Composable
fun RealRamCleanerSection(
    systemStatus: SystemStatus,
    usageStatsList: List<AppUsageInfo>,
    onCleanRamClick: () -> Unit,
    onRefreshUsageStats: () -> Unit,
    onOpenUninstaller: () -> Unit,
    onOpenWhitelist: () -> Unit
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardBackground)
                .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "RAM",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RAM Usage: ${systemStatus.ramUsedPercent}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "${systemStatus.ramUsedMb} MB / ${systemStatus.ramTotalGb} GB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { systemStatus.ramUsedPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyanAccent,
                    trackColor = Color(0xFF18222E)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Available RAM: ${systemStatus.ramAvailMb} MB",
                        fontSize = 11.sp,
                        color = GreenAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "OS Trim Active",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCleanRamClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Clean",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (systemStatus.isBoosting) "MEASURING & CLEANING RAM..." else "CLEAN RAM & MEASURE MEMORY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                if (systemStatus.lastBoostResultMsg != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF11202E))
                            .border(1.dp, CyanAccent, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "📊 Last Diagnostic Result: ${systemStatus.lastBoostResultMsg}",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (!systemStatus.hasUsageStatsAccess) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF151D28))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "🔒 Grant Usage Access permission to display app memory and usage telemetry.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Open System Settings -> Usage Access", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F354D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Grant Usage Access Permission in Settings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENTLY ACTIVE BACKGROUND APPS (${usageStatsList.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            IconButton(
                onClick = onRefreshUsageStats,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = CyanAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (usageStatsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCardBackground)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (systemStatus.hasUsageStatsAccess) "No recent app usage statistics available."
                    else "Grant Usage Access above to inspect recently active background applications.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        } else {
            usageStatsList.forEach { appInfo ->
                UsageStatsAppCard(appInfo = appInfo)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onOpenWhitelist,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2A3A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Whitelist",
                    modifier = Modifier.size(14.dp),
                    tint = CyanAccent
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Memory Exceptions", fontSize = 11.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onOpenUninstaller,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281A1A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Uninstall",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFFF5252)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Uninstall Games", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun GamerProfileSection(
    gamerProfile: GamerProfile,
    tagText: String,
    titleText: String,
    bioText: String,
    genreText: String,
    onTagChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardBackground)
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = tagText,
                onValueChange = onTagChange,
                label = { Text("Gamer Tag / Nickname", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = Color(0xFF263548),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = titleText,
                onValueChange = onTitleChange,
                label = { Text("Player Title / Badge", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = Color(0xFF263548),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = bioText,
                onValueChange = onBioChange,
                label = { Text("Gamer Bio", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = Color(0xFF263548),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = genreText,
                onValueChange = onGenreChange,
                label = { Text("Favorite Gaming Genre", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = Color(0xFF263548),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SAVE PROFILE TO DEVICE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun GeneralSettingsSection(
    settings: SettingsModel,
    onUpdateSettings: (SettingsModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingToggleRow(
            title = "Game Turbo",
            subtitle = "Improve your gaming experience with GPU background priority",
            checked = settings.isGameTurboEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isGameTurboEnabled = it)) }
        )
        SettingToggleRow(
            title = "In-game shortcuts",
            subtitle = "Swipe from the top side edge of the screen to view HUD",
            checked = settings.isInGameShortcutsEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isInGameShortcutsEnabled = it)) }
        )
        SettingToggleRow(
            title = "Content recommendations",
            subtitle = "Receive gaming-related content recommendations offline",
            checked = settings.isContentRecommendationsEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isContentRecommendationsEnabled = it)) }
        )
        SettingToggleRow(
            title = "Remove added games from Home screen",
            subtitle = "Don't show the games that were added to the gamebox on the Home screen",
            checked = settings.isRemoveFromHomeScreenEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isRemoveFromHomeScreenEnabled = it)) }
        )
        SettingToggleRow(
            title = "Home screen shortcut",
            subtitle = "Create Ray Game Launcher quick access icon",
            checked = settings.isHomeScreenShortcutEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isHomeScreenShortcutEnabled = it)) }
        )
    }
}

@Composable
fun PerformanceModeSection(
    settings: SettingsModel,
    onUpdateSettings: (SettingsModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingToggleRow(
            title = "Performance optimization",
            subtitle = "Avoid overheating and hardware-related issues",
            checked = settings.isPerformanceOptimizationEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isPerformanceOptimizationEnabled = it)) }
        )
        SettingToggleRow(
            title = "Bandwidth priority",
            subtitle = "Give more bandwidth to the games running in the foreground",
            checked = settings.isBandwidthPriorityEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isBandwidthPriorityEnabled = it)) }
        )
        SettingToggleRow(
            title = "Wi-Fi speed boost",
            subtitle = "Increase connection speed by 10%-20% when connected to routers",
            checked = settings.isWifiSpeedBoostEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isWifiSpeedBoostEnabled = it)) }
        )
        SettingToggleRow(
            title = "Lower screen brightness automatically",
            subtitle = "Lower screen brightness automatically to save power after 3 minutes of inactivity",
            checked = settings.isAutoLowerBrightnessEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isAutoLowerBrightnessEnabled = it)) }
        )
    }
}

@Composable
fun GameDndSection(
    settings: SettingsModel,
    onUpdateSettings: (SettingsModel) -> Unit
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingActionRow(
            title = "Enhanced experience",
            subtitle = "Suppress notifications, call popups, and alarm dialogs",
            onClick = {
                Toast.makeText(context, "DND Gaming Mode: System notifications suppressed", Toast.LENGTH_SHORT).show()
            }
        )
        SettingToggleRow(
            title = "Restrict buttons and gestures",
            subtitle = "Restrict buttons and full screen gestures during games",
            checked = settings.isRestrictGesturesEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(isRestrictGesturesEnabled = it)) }
        )
    }
}
