package com.example.ui.screens

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.data.MeshPeer
import com.example.model.AppModel
import com.example.model.GamerProfile
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DashboardTab

@Composable
fun MainDashboardScreen(
    activeTab: DashboardTab,
    installedApps: List<AppModel>,
    discoveredPeers: List<MeshPeer>,
    selectedGame: AppModel?,
    gamerProfile: GamerProfile,
    onSelectTab: (DashboardTab) -> Unit,
    onSelectGame: (AppModel) -> Unit,
    onLaunchGame: (AppModel) -> Unit,
    onOpenAddGame: () -> Unit,
    onRefreshScanMesh: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val isCompact = maxWidth < 600.dp

        if (isCompact) {
            // Compact Vertical Layout (e.g. Portrait Phone)
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Tab Selector Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xE60D131A))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isGameboxActive = activeTab == DashboardTab.GAMEBOX
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isGameboxActive) Color(0xFF152232) else Color.Transparent)
                            .clickable { onSelectTab(DashboardTab.GAMEBOX) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Gamebox",
                                tint = if (isGameboxActive) CyanAccent else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gamebox",
                                fontSize = 13.sp,
                                fontWeight = if (isGameboxActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isGameboxActive) Color.White else TextSecondary
                            )
                        }
                    }

                    val isMeshActive = activeTab == DashboardTab.NEARBY_MESH
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMeshActive) Color(0xFF152232) else Color.Transparent)
                            .clickable { onSelectTab(DashboardTab.NEARBY_MESH) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Nearby Mesh",
                                tint = if (isMeshActive) CyanAccent else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nearby Mesh",
                                fontSize = 13.sp,
                                fontWeight = if (isMeshActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMeshActive) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Main Content Viewport
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    when (activeTab) {
                        DashboardTab.GAMEBOX -> {
                            val addedApps = installedApps.filter { it.isAddedToGamebox }
                            if (addedApps.isEmpty()) {
                                EmptyGameboxView(onOpenAddGame = onOpenAddGame)
                            } else {
                                SmartCategorizedGameboxView(
                                    addedApps = addedApps,
                                    selectedGame = selectedGame ?: addedApps.first(),
                                    onSelectGame = onSelectGame,
                                    onLaunchGame = onLaunchGame,
                                    onOpenAddGame = onOpenAddGame,
                                    isCompact = true
                                )
                            }
                        }

                        DashboardTab.NEARBY_MESH -> {
                            NearbyMeshScreen(
                                discoveredPeers = discoveredPeers,
                                installedApps = installedApps,
                                gamerProfile = gamerProfile,
                                onRefreshScan = onRefreshScanMesh,
                                onLaunchGame = onLaunchGame
                            )
                        }
                    }
                }
            }
        } else {
            // Wide / Expanded Horizontal Layout (e.g. Landscape / Tablet)
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Navigation Sidebar
                Column(
                    modifier = Modifier
                        .width(180.dp)
                        .fillMaxHeight()
                        .background(Color(0xE60D131A))
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF1E2A38))),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "SYSTEM NAVIGATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )

                    val isGameboxActive = activeTab == DashboardTab.GAMEBOX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isGameboxActive) Color(0xFF152232) else Color.Transparent)
                            .clickable { onSelectTab(DashboardTab.GAMEBOX) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Gamebox",
                                tint = if (isGameboxActive) CyanAccent else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Gamebox",
                                fontSize = 14.sp,
                                fontWeight = if (isGameboxActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isGameboxActive) Color.White else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val isMeshActive = activeTab == DashboardTab.NEARBY_MESH
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMeshActive) Color(0xFF152232) else Color.Transparent)
                            .clickable { onSelectTab(DashboardTab.NEARBY_MESH) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Nearby Mesh",
                                tint = if (isMeshActive) CyanAccent else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Nearby Mesh",
                                fontSize = 14.sp,
                                fontWeight = if (isMeshActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMeshActive) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Right Main Content Viewport
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    when (activeTab) {
                        DashboardTab.GAMEBOX -> {
                            val addedApps = installedApps.filter { it.isAddedToGamebox }
                            if (addedApps.isEmpty()) {
                                EmptyGameboxView(onOpenAddGame = onOpenAddGame)
                            } else {
                                SmartCategorizedGameboxView(
                                    addedApps = addedApps,
                                    selectedGame = selectedGame ?: addedApps.first(),
                                    onSelectGame = onSelectGame,
                                    onLaunchGame = onLaunchGame,
                                    onOpenAddGame = onOpenAddGame,
                                    isCompact = false
                                )
                            }
                        }

                        DashboardTab.NEARBY_MESH -> {
                            NearbyMeshScreen(
                                discoveredPeers = discoveredPeers,
                                installedApps = installedApps,
                                gamerProfile = gamerProfile,
                                onRefreshScan = onRefreshScanMesh,
                                onLaunchGame = onLaunchGame
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartCategorizedGameboxView(
    addedApps: List<AppModel>,
    selectedGame: AppModel,
    onSelectGame: (AppModel) -> Unit,
    onLaunchGame: (AppModel) -> Unit,
    onOpenAddGame: () -> Unit,
    isCompact: Boolean = false
) {
    val categorizedApps = addedApps.groupBy { it.category }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val displayApps = if (selectedCategoryFilter != null) {
        addedApps.filter { it.category == selectedCategoryFilter }
    } else {
        addedApps
    }

    if (isCompact) {
        // Vertical Stack Layout for Compact Phones
        Column(modifier = Modifier.fillMaxSize()) {
            // Selected Game Hero Card on Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF101924), Color(0xFF080D14))
                        )
                    )
                    .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DrawableIconImage(
                            drawable = selectedGame.icon,
                            contentDescription = selectedGame.appName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedGame.appName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${selectedGame.category} • ${selectedGame.fpsTarget} FPS",
                                fontSize = 11.sp,
                                color = CyanAccent
                            )
                        }
                    }

                    Button(
                        onClick = { onLaunchGame(selectedGame) },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PLAY", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills Row (Horizontally Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedCategoryFilter == null) CyanAccent else Color(0xFF15202B))
                        .clickable { selectedCategoryFilter = null }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "All (${addedApps.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCategoryFilter == null) Color.Black else Color.White
                    )
                }

                categorizedApps.keys.forEach { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCategoryFilter == cat) CyanAccent else Color(0xFF15202B))
                            .clickable { selectedCategoryFilter = cat }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedCategoryFilter == cat) Color.Black else Color.White
                        )
                    }
                }
            }

            // Games List Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayApps, key = { it.packageName }) { app ->
                    val isSelected = selectedGame.packageName == app.packageName
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF1E2C3D) else DarkCardBackground)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyanAccent else Color(0xFF1E2A38),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectGame(app) }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DrawableIconImage(
                                drawable = app.icon,
                                contentDescription = app.appName,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.appName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📁 ${app.category}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Horizontal Layout for Landscape / Widescreen
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .fillMaxHeight()
                    .padding(end = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCategoryFilter == null) CyanAccent else Color(0xFF15202B))
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "All (${addedApps.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedCategoryFilter == null) Color.Black else Color.White
                        )
                    }

                    categorizedApps.keys.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategoryFilter == cat) CyanAccent else Color(0xFF15202B))
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategoryFilter == cat) Color.Black else Color.White
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayApps, key = { it.packageName }) { app ->
                        val isSelected = selectedGame.packageName == app.packageName
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E2C3D) else DarkCardBackground)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CyanAccent else Color(0xFF1E2A38),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectGame(app) }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DrawableIconImage(
                                    drawable = app.icon,
                                    contentDescription = app.appName,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = app.appName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "📁 ${app.category}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF101924), Color(0xFF080D14))
                        )
                    )
                    .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DrawableIconImage(
                            drawable = selectedGame.icon,
                            contentDescription = selectedGame.appName,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = selectedGame.appName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Category: ${selectedGame.category}",
                                    fontSize = 12.sp,
                                    color = CyanAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Package: ${selectedGame.packageName}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(title = "PLAY TIME", value = "${selectedGame.totalPlayTimeMinutes} mins")
                        StatCard(title = "GPU GOVERNOR", value = "Game Turbo Boost")
                        StatCard(title = "FPS TARGET", value = "${selectedGame.fpsTarget} FPS Locked")
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xED0D1520))
                        .border(1.dp, Color(0xFF1E2F45), RoundedCornerShape(14.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = { onLaunchGame(selectedGame) },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PLAY",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Game Turbo & Local Mesh Broadcaster Active",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyGameboxView(onOpenAddGame: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "Empty",
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No games added to Gamebox yet",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap '+ Add Games' to include installed apps on your device",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOpenAddGame,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(text = "+ Add Games", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131D28))
            .border(1.dp, Color(0xFF1E2A38), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = title, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DrawableIconImage(
    drawable: Drawable?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (drawable != null) {
        val bitmap = drawable.toBitmap()
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E2C3D)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = contentDescription,
                tint = CyanAccent,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
