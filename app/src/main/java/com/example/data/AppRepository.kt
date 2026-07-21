package com.example.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Build
import com.example.model.AppModel

class AppRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ray_game_launcher_apps", Context.MODE_PRIVATE)

    fun getInstalledApps(): List<AppModel> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            packageManager.queryIntentActivities(mainIntent, 0)
        } catch (e: Exception) {
            emptyList()
        }

        val appList = mutableListOf<AppModel>()

        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            // Exclude self from list
            if (packageName == context.packageName) continue

            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            var isGame = false
            var categoryName = "Applications"

            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                
                // Determine if game & extract real category
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (appInfo.category) {
                        ApplicationInfo.CATEGORY_GAME -> {
                            isGame = true
                            categoryName = "Gaming"
                        }
                        ApplicationInfo.CATEGORY_AUDIO -> categoryName = "Music & Audio"
                        ApplicationInfo.CATEGORY_VIDEO -> categoryName = "Video & Media"
                        ApplicationInfo.CATEGORY_IMAGE -> categoryName = "Graphics & Photography"
                        ApplicationInfo.CATEGORY_MAPS -> categoryName = "Navigation & Travel"
                        ApplicationInfo.CATEGORY_NEWS -> categoryName = "News & Info"
                        ApplicationInfo.CATEGORY_PRODUCTIVITY -> categoryName = "Productivity"
                        ApplicationInfo.CATEGORY_SOCIAL -> categoryName = "Social & Communication"
                        else -> {
                            @Suppress("DEPRECATION")
                            if ((appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0) {
                                isGame = true
                                categoryName = "Gaming"
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    if ((appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0) {
                        isGame = true
                        categoryName = "Gaming"
                    }
                }

                // Fine-grained heuristic categorization for launcher UI
                val lowerPkg = packageName.lowercase()
                val lowerName = appName.lowercase()

                if (lowerPkg.contains("emulator") || lowerName.contains("emulator") || 
                    lowerPkg.contains("ppsspp") || lowerPkg.contains("dolphin") || 
                    lowerPkg.contains("citra") || lowerPkg.contains("retroarch")) {
                    isGame = true
                    categoryName = "Emulators"
                } else if (lowerPkg.contains("racing") || lowerName.contains("racing") || 
                           lowerPkg.contains("speed") || lowerName.contains("asphalt")) {
                    isGame = true
                    categoryName = "Racing"
                } else if (lowerPkg.contains("puzzle") || lowerName.contains("puzzle") || 
                           lowerPkg.contains("chess") || lowerName.contains("sudoku")) {
                    isGame = true
                    categoryName = "Puzzle"
                } else if (lowerPkg.contains("action") || lowerName.contains("action") || 
                           lowerPkg.contains("fight") || lowerName.contains("shooter") || 
                           lowerName.contains("brawl") || lowerPkg.contains("bomba")) {
                    isGame = true
                    categoryName = "Action"
                } else if (lowerPkg.contains("arcade") || lowerName.contains("arcade") || 
                           lowerPkg.contains("jump") || lowerPkg.contains("runner")) {
                    isGame = true
                    categoryName = "Arcade"
                } else if (isGame && categoryName == "Gaming") {
                    categoryName = "Action & Gaming"
                }

            } catch (e: Exception) {
                isGame = false
                categoryName = "Applications"
            }

            // Persistence setting check
            val isAdded = if (prefs.contains(packageName)) {
                prefs.getBoolean(packageName, false)
            } else {
                isGame // Auto-add detected games by default
            }

            val playTimeMinutes = prefs.getInt("play_time_$packageName", 0)

            appList.add(
                AppModel(
                    packageName = packageName,
                    appName = appName,
                    icon = icon,
                    isGame = isGame,
                    isAddedToGamebox = isAdded,
                    category = categoryName,
                    sizeMb = "Installed",
                    rating = "4.8",
                    totalPlayTimeMinutes = playTimeMinutes,
                    fpsTarget = 60
                )
            )
        }

        return appList.sortedBy { it.appName }
    }

    fun setAppAddedToGamebox(packageName: String, isAdded: Boolean) {
        prefs.edit().putBoolean(packageName, isAdded).apply()
    }

    fun recordGameSession(packageName: String, sessionMinutes: Int = 1) {
        val current = prefs.getInt("play_time_$packageName", 0)
        prefs.edit().putInt("play_time_$packageName", current + sessionMinutes).apply()
    }
}
