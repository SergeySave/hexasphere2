package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color.Color

class OceanTileTypeComponent : PooledComponent(), TileType {

    override val color: Color = Color(r = 0x07, g = 0x0b, b = 0x4a)
    override val unlocalizedName = "tile.ocean.name"

    override fun reset() { }
}
