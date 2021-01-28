package com.sergeysav.hexasphere.common.game.tile.feature

interface TileFeature {

    val featureNumber: Int
    val unlocalizedName: String

    companion object {
        const val MAX_FEATURES_PER_TILE = 1
    }
}