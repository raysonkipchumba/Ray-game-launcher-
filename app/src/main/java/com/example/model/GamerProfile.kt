package com.example.model

data class GamerProfile(
    val gamerTag: String = "RayGamer",
    val gamerTitle: String = "P2P Challenger",
    val avatarId: String = "cyber_controller", // cyber_controller, neon_dragon, samurai_helmet, arcade_master
    val bio: String = "Ready for zero-lag local P2P matches!",
    val favoriteGenre: String = "Action & Emulators",
    val deviceModel: String = android.os.Build.MODEL ?: "Ray Android Device",
    val level: Int = 12,
    val totalMatchesPlayed: Int = 48,
    val totalWins: Int = 36
)
