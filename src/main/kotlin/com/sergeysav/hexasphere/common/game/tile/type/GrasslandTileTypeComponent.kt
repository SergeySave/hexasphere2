package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color.Color

class GrasslandTileTypeComponent : PooledComponent(), TileType {

    override val color: Color = Color(r = 0x0d, g = 0x63, b = 0x1e)
    override val unlocalizedName = "tile.grassland.name"

    override fun reset() { }
}
