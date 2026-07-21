package com.example.model

import android.graphics.drawable.Drawable

data class AppModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isGame: Boolean = false,
    var isAddedToGamebox: Boolean = false,
    val category: String = "Action",
    val sizeMb: String = "45MB",
    val rating: String = "4.5",
    val bannerGradientIndex: Int = 0,
    val totalPlayTimeMinutes: Int = 120,
    val fpsTarget: Int = 60
)
