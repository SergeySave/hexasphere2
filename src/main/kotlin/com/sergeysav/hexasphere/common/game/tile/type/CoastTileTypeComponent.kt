package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color

class CoastTileTypeComponent : PooledComponent(), TileType {

    override val color: Int = color(r = 0x0c, g = 0x16, b = 0xad)
    override val unlocalizedName = "tile.coast.name"

    override fun reset() { }
}
