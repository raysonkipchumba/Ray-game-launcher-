package com.example.model

data class CatalogGameModel(
    val id: String,
    val title: String,
    val category: String,
    val rating: String,
    val sizeMb: String,
    val description: String,
    val isInstalled: Boolean = false,
    val type: GameType = GameType.CASUAL_MINI
)

enum class GameType {
    NATIVE_APP,
    SPACE_RUNNER,
    PUZZLE_HEX,
    SPEED_RACER,
    BRICK_BREAKER,
    CASUAL_MINI
}
