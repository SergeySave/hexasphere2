package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.PooledComponent
import com.sergeysav.hexasphere.common.color

class GrasslandTileTypeComponent : PooledComponent(), TileType {

    override val color: Int = color(r = 0x0d, g = 0x63, b = 0x1e)

    override fun reset() { }
}
