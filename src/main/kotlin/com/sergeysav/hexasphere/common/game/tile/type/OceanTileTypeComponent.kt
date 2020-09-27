package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color

class OceanTileTypeComponent : PooledComponent(), TileType {

    override val color: Int = color(r = 0x44, g = 0x44, b = 0xdd)

    override fun reset() { }
}
