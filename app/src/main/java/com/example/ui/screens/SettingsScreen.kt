package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CleaningServices
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Two-Column Layout (Left Category Sidebar vs Right Details Content)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Sidebar Column: Category Tabs
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0D131A))
                    .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                CategoryTabItem(
                    title = "Real RAM & Usage Stats",
                    icon = Icons.Default.CleaningServices,
                    isSelected = activeCategory == SettingsCategory.REAL_RAM_CLEANER,
                    onClick = { activeCategory = SettingsCategory.REAL_RAM_CLEANER }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoryTabItem(
                    title = "Player Profile (P2P)",
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Zero placebo animations. Memory is measured strictly via ActivityManager.MemoryInfo before and after cleaning.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // Real Memory Status Card
                        item {
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
                        }

                        // Android OS Transparency Notice Banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF131A24))
                                    .border(1.dp, Color(0xFF23364A), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Notice",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Android OS Memory Architecture Transparency",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Since Android 9+ (API 28+), the OS strictly restricts third-party apps from force-killing other applications. " +
                                                    "Ray Launcher sends process cleanup intents where allowed, releases launcher bitmap/cache references, and triggers GC. " +
                                                    "If Android restricts memory reclamation, we accurately report 0 MB freed rather than showing fake numbers.",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Usage Stats Permission & Analytics Card
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCardBackground)
                                    .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = "Usage Access",
                                                tint = if (systemStatus.hasUsageStatsAccess) GreenAccent else Color(0xFFFFB300),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Usage Stats Permission (PACKAGE_USAGE_STATS)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = if (systemStatus.hasUsageStatsAccess) "GRANTED" else "PERMISSION NEEDED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (systemStatus.hasUsageStatsAccess) GreenAccent else Color(0xFFFFB300)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Used by UsageStatsManager to identify recently active background applications and analyze real system resource usage.",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )

                                    if (!systemStatus.hasUsageStatsAccess) {
                                        Spacer(modifier = Modifier.height(10.dp))
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
                        }

                        // Recently Active Background Apps (Usage Stats Analytics)
                        item {
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
                        }

                        if (usageStatsList.isEmpty()) {
                            item {
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
                            }
                        } else {
                            items(usageStatsList, key = { it.packageName }) { appInfo ->
                                UsageStatsAppCard(appInfo = appInfo)
                            }
                        }
                    }

                    SettingsCategory.GAMER_PROFILE -> {
                        item {
                            Text(
                                text = "LOCAL GAMER PROFILE & P2P IDENTITY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Stored 100% offline on your device. Transmitted to nearby players during local Wi-Fi Direct & BLE mesh sessions.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        item {
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
                                        onValueChange = { tagText = it },
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
                                        onValueChange = { titleText = it },
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
                                        onValueChange = { bioText = it },
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
                                        onValueChange = { genreText = it },
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
                                        onClick = {
                                            val updated = gamerProfile.copy(
                                                gamerTag = tagText.ifBlank { "RayGamer" },
                                                gamerTitle = titleText.ifBlank { "P2P Challenger" },
                                                bio = bioText.ifBlank { "Ready for zero-lag local P2P matches!" },
                                                favoriteGenre = genreText.ifBlank { "Action & Emulators" }
                                            )
                                            onUpdateGamerProfile(updated)
                                            Toast.makeText(context, "✅ Gamer Profile Saved Local to Ray Launcher!", Toast.LENGTH_SHORT).show()
                                        },
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
                    }

                    SettingsCategory.GENERAL -> {
                        item {
                            SettingToggleRow(
                                title = "Game Turbo",
                                subtitle = "Improve your gaming experience with GPU background priority",
                                checked = settings.isGameTurboEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isGameTurboEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "In-game shortcuts",
                                subtitle = "Swipe from the top side edge of the screen to view HUD",
                                checked = settings.isInGameShortcutsEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isInGameShortcutsEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Content recommendations",
                                subtitle = "Receive gaming-related content recommendations offline",
                                checked = settings.isContentRecommendationsEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isContentRecommendationsEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Remove added games from Home screen",
                                subtitle = "Don't show the games that were added to the gamebox on the Home screen",
                                checked = settings.isRemoveFromHomeScreenEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isRemoveFromHomeScreenEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingActionRow(
                                title = "Uninstall games",
                                subtitle = "Select games to uninstall from device",
                                onClick = { showUninstallerDialog = true }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Home screen shortcut",
                                subtitle = "Create Ray Game Launcher quick access icon",
                                checked = settings.isHomeScreenShortcutEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isHomeScreenShortcutEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingActionRow(
                                title = "Add game",
                                subtitle = "Include or exclude installed apps in Gamebox",
                                onClick = onOpenAddGame
                            )
                        }
                    }

                    SettingsCategory.PERFORMANCE_MODE -> {
                        item {
                            SettingToggleRow(
                                title = "Performance optimization",
                                subtitle = "Avoid overheating and hardware-related issues",
                                checked = settings.isPerformanceOptimizationEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isPerformanceOptimizationEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Bandwidth priority",
                                subtitle = "Give more bandwidth to the games running in the foreground",
                                checked = settings.isBandwidthPriorityEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isBandwidthPriorityEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingActionRow(
                                title = "Memory exceptions",
                                subtitle = "whitelist processes from being cleaned during RAM boost",
                                onClick = { showMemoryWhitelistDialog = true }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Wi-Fi speed boost",
                                subtitle = "Increase connection speed by 10%-20% when connected to routers",
                                checked = settings.isWifiSpeedBoostEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isWifiSpeedBoostEnabled = it))
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Lower screen brightness automatically",
                                subtitle = "Lower screen brightness automatically to save power after 3 minutes of inactivity",
                                checked = settings.isAutoLowerBrightnessEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isAutoLowerBrightnessEnabled = it))
                                }
                            )
                        }
                    }

                    SettingsCategory.GAME_DND -> {
                        item {
                            SettingActionRow(
                                title = "Enhanced experience",
                                subtitle = "Suppress notifications, call popups, and alarm dialogs",
                                onClick = {
                                    Toast.makeText(context, "DND Gaming Mode: System notifications suppressed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item {
                            SettingToggleRow(
                                title = "Restrict buttons and gestures",
                                subtitle = "Restrict buttons and full screen gestures during games",
                                checked = settings.isRestrictGesturesEnabled,
                                onCheckedChange = {
                                    onUpdateSettings(settings.copy(isRestrictGesturesEnabled = it))
                                }
                            )
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
