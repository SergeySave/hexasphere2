package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color

class OceanTileTypeComponent : PooledComponent(), TileType {

    override val color: Int = color(r = 0x07, g = 0x0b, b = 0x4a)

    override fun reset() { }
}
