package com.sergeysav.hexasphere.common.game.tile.type

import com.sergeysav.hexasphere.common.color.Color

interface TileType {
    val color: Color
    val unlocalizedName: String
}