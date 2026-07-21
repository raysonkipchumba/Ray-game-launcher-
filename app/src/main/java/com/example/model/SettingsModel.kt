package com.example.model

data class SettingsModel(
    // General Settings
    val isGameTurboEnabled: Boolean = true,
    val isInGameShortcutsEnabled: Boolean = true,
    val isContentRecommendationsEnabled: Boolean = true,
    val isRemoveFromHomeScreenEnabled: Boolean = true,
    val isHomeScreenShortcutEnabled: Boolean = true,

    // Performance Mode
    val isPerformanceOptimizationEnabled: Boolean = true,
    val isBandwidthPriorityEnabled: Boolean = true,
    val isWifiSpeedBoostEnabled: Boolean = true,
    val isAutoLowerBrightnessEnabled: Boolean = true,
    val whitelistApps: List<String> = emptyList(),

    // Game DND
    val isRestrictGesturesEnabled: Boolean = true,
    val dndFeaturesCount: Int = 0
)
