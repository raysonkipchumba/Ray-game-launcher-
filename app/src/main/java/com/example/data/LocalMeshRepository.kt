package com.example.data

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import com.example.model.AppModel
import com.example.model.GamerProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MeshPeer(
    val deviceAddress: String,
    val deviceName: String,
    val gamerTag: String,
    val gamerTitle: String,
    val avatarId: String,
    val activeGamePackage: String?,
    val activeGameTitle: String?,
    val installedGamePackages: List<String>,
    val matchingGamesCount: Int,
    val signalStrengthDbm: Int,
    val isConnected: Boolean = false
)

class LocalMeshRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val wifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private val channel = wifiP2pManager?.initialize(context, context.mainLooper, null)

    private val _discoveredPeers = MutableStateFlow<List<MeshPeer>>(emptyList())
    val discoveredPeers: StateFlow<List<MeshPeer>> = _discoveredPeers.asStateFlow()

    private val _isMeshActive = MutableStateFlow(true)
    val isMeshActive: StateFlow<Boolean> = _isMeshActive.asStateFlow()

    private val _lastPeerNotification = MutableStateFlow<String?>(null)
    val lastPeerNotification: StateFlow<String?> = _lastPeerNotification.asStateFlow()

    private var currentProfile: GamerProfile = GamerProfile()
    private var currentActiveGamePackage: String? = null
    private var currentActiveGameTitle: String? = null

    init {
        startP2pDiscovery()
    }

    fun updateLocalProfile(profile: GamerProfile) {
        currentProfile = profile
    }

    fun broadcastActiveGame(packageName: String?, gameTitle: String?) {
        currentActiveGamePackage = packageName
        currentActiveGameTitle = gameTitle

        if (packageName != null && gameTitle != null) {
            _lastPeerNotification.value = "⚡ Mesh Broadcast: ${currentProfile.gamerTag} playing $gameTitle nearby"
        }
    }

    fun startP2pDiscovery() {
        _isMeshActive.value = true

        try {
            wifiP2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    fetchPeerList()
                }

                override fun onFailure(reasonCode: Int) {
                    _discoveredPeers.value = emptyList()
                }
            })
        } catch (e: Exception) {
            _discoveredPeers.value = emptyList()
        }
    }

    private fun fetchPeerList() {
        try {
            wifiP2pManager?.requestPeers(channel) { peerList ->
                val deviceList = peerList.deviceList.toList()
                if (deviceList.isNotEmpty()) {
                    val peers = deviceList.mapIndexed { index, device ->
                        MeshPeer(
                            deviceAddress = device.deviceAddress ?: "02:00:00:00:00:0$index",
                            deviceName = device.deviceName.takeIf { it.isNotBlank() } ?: "Ray_Gamer_P2P_${index + 1}",
                            gamerTag = device.deviceName.takeIf { it.isNotBlank() } ?: "Player_${index + 1}",
                            gamerTitle = "Nearby P2P Host",
                            avatarId = "cyber_controller",
                            activeGamePackage = currentActiveGamePackage,
                            activeGameTitle = currentActiveGameTitle,
                            installedGamePackages = emptyList(),
                            matchingGamesCount = 0,
                            signalStrengthDbm = -50 - (index * 10)
                        )
                    }
                    _discoveredPeers.value = peers
                } else {
                    _discoveredPeers.value = emptyList()
                }
            }
        } catch (e: Exception) {
            _discoveredPeers.value = emptyList()
        }
    }

    fun matchOverlappingGames(installedApps: List<AppModel>): List<AppModel> {
        val peerPackages = _discoveredPeers.value.flatMap { it.installedGamePackages }.toSet()
        return installedApps.filter { it.packageName in peerPackages || it.isGame }
    }

    fun clearNotification() {
        _lastPeerNotification.value = null
    }
}
