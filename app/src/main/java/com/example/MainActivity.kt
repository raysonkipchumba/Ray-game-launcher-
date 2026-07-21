package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.model.AppModel
import com.example.ui.screens.AddGameScreen
import com.example.ui.screens.DynamicPerformanceIsland
import com.example.ui.screens.GameTurboOverlayBanner
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TopStatusBar
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val screenState by viewModel.screenState.collectAsState()
                val activeTab by viewModel.activeTab.collectAsState()
                val installedApps by viewModel.installedApps.collectAsState()
                val discoveredPeers by viewModel.discoveredPeers.collectAsState()
                val selectedGame by viewModel.selectedGame.collectAsState()
                val settings by viewModel.settings.collectAsState()
                val gamerProfile by viewModel.gamerProfile.collectAsState()
                val systemStatus by viewModel.systemStatus.collectAsState()
                val usageStatsList by viewModel.usageStatsList.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val showTurboBanner by viewModel.showTurboOverlayBanner.collectAsState()
                val launchedTitle by viewModel.launchedGameTitle.collectAsState()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    containerColor = DarkBackground,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (screenState) {
                            ScreenState.SPLASH -> {
                                SplashScreen()
                            }

                            ScreenState.MAIN_DASHBOARD -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    TopStatusBar(
                                        status = systemStatus,
                                        gamerProfile = gamerProfile,
                                        onAddGameClick = { viewModel.openAddGameScreen() },
                                        onSettingsClick = { viewModel.openSettingsScreen() },
                                        onRamBoostClick = { viewModel.boostRam() },
                                        onProfileClick = { viewModel.openSettingsScreen() }
                                    )
                                    MainDashboardScreen(
                                        activeTab = activeTab,
                                        installedApps = installedApps,
                                        discoveredPeers = discoveredPeers,
                                        selectedGame = selectedGame,
                                        gamerProfile = gamerProfile,
                                        onSelectTab = { viewModel.selectDashboardTab(it) },
                                        onSelectGame = { viewModel.selectGame(it) },
                                        onLaunchGame = { viewModel.launchGame(it) },
                                        onOpenAddGame = { viewModel.openAddGameScreen() },
                                        onRefreshScanMesh = { viewModel.scanMeshPeers() }
                                    )
                                }
                            }

                            ScreenState.ADD_GAME -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    TopStatusBar(
                                        status = systemStatus,
                                        gamerProfile = gamerProfile,
                                        onAddGameClick = { viewModel.openAddGameScreen() },
                                        onSettingsClick = { viewModel.openSettingsScreen() },
                                        onRamBoostClick = { viewModel.boostRam() },
                                        onProfileClick = { viewModel.openSettingsScreen() }
                                    )
                                    AddGameScreen(
                                        installedApps = installedApps,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                        onToggleApp = { pkg, isAdded -> viewModel.toggleAppInGamebox(pkg, isAdded) },
                                        onBackClick = { viewModel.navigateBackToDashboard() }
                                    )
                                }
                            }

                            ScreenState.SETTINGS -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    TopStatusBar(
                                        status = systemStatus,
                                        gamerProfile = gamerProfile,
                                        onAddGameClick = { viewModel.openAddGameScreen() },
                                        onSettingsClick = { viewModel.openSettingsScreen() },
                                        onRamBoostClick = { viewModel.boostRam() },
                                        onProfileClick = { viewModel.openSettingsScreen() }
                                    )
                                    SettingsScreen(
                                        settings = settings,
                                        gamerProfile = gamerProfile,
                                        systemStatus = systemStatus,
                                        usageStatsList = usageStatsList,
                                        onUpdateSettings = { viewModel.updateSettings(it) },
                                        onUpdateGamerProfile = { viewModel.updateGamerProfile(it) },
                                        onCleanRamClick = { viewModel.boostRam() },
                                        onRefreshUsageStats = { viewModel.refreshUsageStats() },
                                        onBackClick = { viewModel.navigateBackToDashboard() },
                                        onOpenAddGame = { viewModel.openAddGameScreen() }
                                    )
                                }
                            }
                        }

                        // Top Center Floating In-Game Dynamic Performance Island HUD Overlay
                        if (screenState != ScreenState.SPLASH) {
                            Box(
                                modifier = Modifier.align(Alignment.TopCenter)
                            ) {
                                DynamicPerformanceIsland(
                                    status = systemStatus,
                                    activeGameTitle = launchedTitle.ifBlank { selectedGame?.appName },
                                    nearbyPeersCount = discoveredPeers.size,
                                    onRamBoostClick = { viewModel.boostRam() }
                                )
                            }
                        }

                        // Game Turbo Launch Overlay Banner
                        GameTurboOverlayBanner(
                            visible = showTurboBanner,
                            gameTitle = launchedTitle
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUsageStats()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Respond to Android system memory pressure signals by clearing launcher caches and freeing memory
        viewModel.onSystemTrimMemory(level)
    }
}
