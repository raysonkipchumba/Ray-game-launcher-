package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.SettingsModel

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ray_game_launcher_settings", Context.MODE_PRIVATE)

    fun getSettings(): SettingsModel {
        return SettingsModel(
            isGameTurboEnabled = prefs.getBoolean("isGameTurboEnabled", true),
            isInGameShortcutsEnabled = prefs.getBoolean("isInGameShortcutsEnabled", true),
            isContentRecommendationsEnabled = prefs.getBoolean("isContentRecommendationsEnabled", true),
            isRemoveFromHomeScreenEnabled = prefs.getBoolean("isRemoveFromHomeScreenEnabled", true),
            isHomeScreenShortcutEnabled = prefs.getBoolean("isHomeScreenShortcutEnabled", true),
            isPerformanceOptimizationEnabled = prefs.getBoolean("isPerformanceOptimizationEnabled", true),
            isBandwidthPriorityEnabled = prefs.getBoolean("isBandwidthPriorityEnabled", true),
            isWifiSpeedBoostEnabled = prefs.getBoolean("isWifiSpeedBoostEnabled", true),
            isAutoLowerBrightnessEnabled = prefs.getBoolean("isAutoLowerBrightnessEnabled", true),
            isRestrictGesturesEnabled = prefs.getBoolean("isRestrictGesturesEnabled", true)
        )
    }

    fun updateSettings(settings: SettingsModel) {
        prefs.edit().apply {
            putBoolean("isGameTurboEnabled", settings.isGameTurboEnabled)
            putBoolean("isInGameShortcutsEnabled", settings.isInGameShortcutsEnabled)
            putBoolean("isContentRecommendationsEnabled", settings.isContentRecommendationsEnabled)
            putBoolean("isRemoveFromHomeScreenEnabled", settings.isRemoveFromHomeScreenEnabled)
            putBoolean("isHomeScreenShortcutEnabled", settings.isHomeScreenShortcutEnabled)
            putBoolean("isPerformanceOptimizationEnabled", settings.isPerformanceOptimizationEnabled)
            putBoolean("isBandwidthPriorityEnabled", settings.isBandwidthPriorityEnabled)
            putBoolean("isWifiSpeedBoostEnabled", settings.isWifiSpeedBoostEnabled)
            putBoolean("isAutoLowerBrightnessEnabled", settings.isAutoLowerBrightnessEnabled)
            putBoolean("isRestrictGesturesEnabled", settings.isRestrictGesturesEnabled)
            apply()
        }
    }
}
