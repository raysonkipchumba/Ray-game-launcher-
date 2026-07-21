package com.example.data

import com.example.model.CatalogGameModel
import com.example.model.GameType

class CatalogRepository {
    fun getCatalogGames(): List<CatalogGameModel> {
        return listOf(
            CatalogGameModel(
                id = "cat_space_runner",
                title = "Space Runner",
                category = "Action",
                rating = "4.8",
                sizeMb = "10MB",
                description = "Dodge retro space obstacles and reach ultra hyper speeds!",
                isInstalled = true,
                type = GameType.SPACE_RUNNER
            ),
            CatalogGameModel(
                id = "cat_puzzle_hex",
                title = "Puzzle Hex",
                category = "Puzzle",
                rating = "4.5",
                sizeMb = "18MB",
                description = "Connect matching neon hexagonal color blocks to score combos.",
                isInstalled = true,
                type = GameType.PUZZLE_HEX
            ),
            CatalogGameModel(
                id = "cat_brick_breaker",
                title = "Brick Breaker Retro",
                category = "Arcade",
                rating = "4.6",
                sizeMb = "12MB",
                description = "Classic paddle arcade brick breaker with laser power-ups.",
                isInstalled = true,
                type = GameType.BRICK_BREAKER
            ),
            CatalogGameModel(
                id = "cat_big_shot_boxing",
                title = "Big Shot Boxing",
                category = "Action",
                rating = "4.1",
                sizeMb = "21MB",
                description = "Train your boxer, execute jabs, and climb the championship ladder.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_race_master",
                title = "Race Master Velocity",
                category = "Racing",
                rating = "4.6",
                sizeMb = "122MB",
                description = "High speed tactical street racing through cyber city tracks.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_merge_renovation",
                title = "Merge for Renovation",
                category = "Casual",
                rating = "4.3",
                sizeMb = "62MB",
                description = "Combine tools and items to renovate futuristic game dens.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_radioline",
                title = "Radioline Global",
                category = "Music & Audio",
                rating = "4.2",
                sizeMb = "12MB",
                description = "Synthwave retro music streaming and chiptune radio station.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_egypt_run",
                title = "Egypt Run",
                category = "Arcade",
                rating = "4.4",
                sizeMb = "45MB",
                description = "Endless temple runner dodging traps and ancient hazards.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_unblock_car",
                title = "UnblockCar",
                category = "Puzzle",
                rating = "4.0",
                sizeMb = "15MB",
                description = "Slide cars out of the gridlock traffic puzzle in minimum moves.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            ),
            CatalogGameModel(
                id = "cat_magic_fluid",
                title = "Magic Fluid Wallpaper",
                category = "Utilities",
                rating = "4.7",
                sizeMb = "8MB",
                description = "Interactive fluid simulation wallpaper with neon glow effects.",
                isInstalled = false,
                type = GameType.CASUAL_MINI
            )
        )
    }
}
