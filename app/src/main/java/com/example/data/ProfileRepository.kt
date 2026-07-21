package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.model.GamerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ray_gamer_profile_prefs", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<GamerProfile> = _profile.asStateFlow()

    private fun loadProfile(): GamerProfile {
        val defaultTag = "RayGamer_${(100..999).random()}"
        val defaultTitle = "P2P Challenger"
        val defaultBio = "Ready for local Wi-Fi Direct multiplayer sessions!"
        val defaultGenre = "Action & Emulators"
        val defaultAvatar = "cyber_controller"

        return GamerProfile(
            gamerTag = prefs.getString("gamerTag", defaultTag) ?: defaultTag,
            gamerTitle = prefs.getString("gamerTitle", defaultTitle) ?: defaultTitle,
            avatarId = prefs.getString("avatarId", defaultAvatar) ?: defaultAvatar,
            bio = prefs.getString("bio", defaultBio) ?: defaultBio,
            favoriteGenre = prefs.getString("favoriteGenre", defaultGenre) ?: defaultGenre,
            deviceModel = Build.MODEL ?: "Ray Android Device",
            level = prefs.getInt("level", 12),
            totalMatchesPlayed = prefs.getInt("totalMatchesPlayed", 48),
            totalWins = prefs.getInt("totalWins", 36)
        )
    }

    fun updateProfile(newProfile: GamerProfile) {
        prefs.edit().apply {
            putString("gamerTag", newProfile.gamerTag)
            putString("gamerTitle", newProfile.gamerTitle)
            putString("avatarId", newProfile.avatarId)
            putString("bio", newProfile.bio)
            putString("favoriteGenre", newProfile.favoriteGenre)
            putInt("level", newProfile.level)
            putInt("totalMatchesPlayed", newProfile.totalMatchesPlayed)
            putInt("totalWins", newProfile.totalWins)
            apply()
        }
        _profile.value = newProfile
    }
}
