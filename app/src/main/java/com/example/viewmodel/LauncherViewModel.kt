package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppRepository
import com.example.data.AppUsageInfo
import com.example.data.LocalMeshRepository
import com.example.data.MeshPeer
import com.example.data.ProfileRepository
import com.example.data.SettingsRepository
import com.example.data.SystemMonitorRepository
import com.example.model.AppModel
import com.example.model.GamerProfile
import com.example.model.SettingsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH,
    MAIN_DASHBOARD,
    ADD_GAME,
    SETTINGS
}

enum class DashboardTab {
    GAMEBOX,
    NEARBY_MESH
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepo = AppRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val sysMonitorRepo = SystemMonitorRepository(application)
    private val localMeshRepo = LocalMeshRepository(application)
    private val profileRepo = ProfileRepository(application)
    val soundManager = SoundManager()

    val gamerProfile: StateFlow<GamerProfile> = profileRepo.profile

    private val _screenState = MutableStateFlow(ScreenState.SPLASH)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _activeTab = MutableStateFlow(DashboardTab.GAMEBOX)
    val activeTab: StateFlow<DashboardTab> = _activeTab.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppModel>>(emptyList())
    val installedApps: StateFlow<List<AppModel>> = _installedApps.asStateFlow()

    private val _selectedGame = MutableStateFlow<AppModel?>(null)
    val selectedGame: StateFlow<AppModel?> = _selectedGame.asStateFlow()

    private val _settings = MutableStateFlow(SettingsModel())
    val settings: StateFlow<SettingsModel> = _settings.asStateFlow()

    val systemStatus = sysMonitorRepo.status
    val discoveredPeers: StateFlow<List<MeshPeer>> = localMeshRepo.discoveredPeers

    private val _usageStatsList = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    val usageStatsList: StateFlow<List<AppUsageInfo>> = _usageStatsList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showTurboOverlayBanner = MutableStateFlow(false)
    val showTurboOverlayBanner: StateFlow<Boolean> = _showTurboOverlayBanner.asStateFlow()

    private val _launchedGameTitle = MutableStateFlow("")
    val launchedGameTitle: StateFlow<String> = _launchedGameTitle.asStateFlow()

    init {
        sysMonitorRepo.startMonitoring()
        loadData()
        refreshUsageStats()
        startBootSequence()
    }

    private fun loadData() {
        viewModelScope.launch {
            val apps = appRepo.getInstalledApps()
            _installedApps.value = apps
            _settings.value = settingsRepo.getSettings()

            val addedGames = apps.filter { it.isAddedToGamebox }
            if (addedGames.isNotEmpty()) {
                _selectedGame.value = addedGames.first()
            } else if (apps.isNotEmpty()) {
                _selectedGame.value = apps.first()
            }
        }
    }

    fun refreshUsageStats() {
        viewModelScope.launch {
            val stats = sysMonitorRepo.getRecentUsageStats()
            _usageStatsList.value = stats
        }
    }

    private fun startBootSequence() {
        viewModelScope.launch {
            delay(150)
            soundManager.playStartupChime(getApplication())
            delay(1200)
            _screenState.value = ScreenState.MAIN_DASHBOARD
        }
    }

    fun selectDashboardTab(tab: DashboardTab) {
        soundManager.playClick()
        _activeTab.value = tab
    }

    fun selectGame(game: AppModel) {
        soundManager.playClick()
        _selectedGame.value = game
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleAppInGamebox(packageName: String, isAdded: Boolean) {
        soundManager.playClick()
        appRepo.setAppAddedToGamebox(packageName, isAdded)

        val updated = _installedApps.value.map {
            if (it.packageName == packageName) {
                it.copy(isAddedToGamebox = isAdded)
            } else it
        }
        _installedApps.value = updated

        val currentSelected = _selectedGame.value
        if (currentSelected?.packageName == packageName && !isAdded) {
            val nextAdded = updated.firstOrNull { it.isAddedToGamebox }
            _selectedGame.value = nextAdded
        }
    }

    fun updateSettings(newSettings: SettingsModel) {
        soundManager.playClick()
        _settings.value = newSettings
        settingsRepo.updateSettings(newSettings)
    }

    fun updateGamerProfile(newProfile: GamerProfile) {
        soundManager.playClick()
        profileRepo.updateProfile(newProfile)
        localMeshRepo.updateLocalProfile(newProfile)
    }

    fun openAddGameScreen() {
        soundManager.playClick()
        _searchQuery.value = ""
        _screenState.value = ScreenState.ADD_GAME
    }

    fun openSettingsScreen() {
        soundManager.playClick()
        refreshUsageStats()
        _screenState.value = ScreenState.SETTINGS
    }

    fun navigateBackToDashboard() {
        soundManager.playClick()
        _screenState.value = ScreenState.MAIN_DASHBOARD
    }

    fun scanMeshPeers() {
        soundManager.playClick()
        localMeshRepo.startP2pDiscovery()
    }

    fun launchGame(app: AppModel) {
        soundManager.playLaunch(getApplication())
        appRepo.recordGameSession(app.packageName, 1)

        viewModelScope.launch {
            _launchedGameTitle.value = app.appName
            _showTurboOverlayBanner.value = true

            localMeshRepo.broadcastActiveGame(app.packageName, app.appName)

            val pm = getApplication<Application>().packageManager
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)

            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    getApplication<Application>().startActivity(launchIntent)
                } catch (e: Exception) {
                    Toast.makeText(getApplication(), "Launching ${app.appName} with Game Turbo Boost", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(getApplication(), "Launching ${app.appName} in High Performance Mode", Toast.LENGTH_SHORT).show()
            }

            delay(3500)
            _showTurboOverlayBanner.value = false
        }
    }

    fun boostRam() {
        soundManager.playRamClean()
        viewModelScope.launch {
            sysMonitorRepo.cleanRam { freedMb, resultMsg ->
                Toast.makeText(getApplication(), "⚡ RAM Clean: $resultMsg", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onSystemTrimMemory(level: Int) {
        sysMonitorRepo.trimLauncherMemory()
        sysMonitorRepo.refreshMetrics()
    }

    override fun onCleared() {
        super.onCleared()
        sysMonitorRepo.stopMonitoring()
    }
}
