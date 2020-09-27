package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color

class CoastTileTypeComponent : PooledComponent(), TileType {

    override val color: Int = color(r = 0x88, g = 0x88, b = 0xff)

    override fun reset() { }
}
