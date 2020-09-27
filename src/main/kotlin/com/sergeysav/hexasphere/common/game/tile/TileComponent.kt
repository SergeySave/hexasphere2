package com.sergeysav.hexasphere.common.game.tile

import com.artemis.PooledComponent
import com.artemis.annotations.EntityId
import com.artemis.utils.IntBag
import org.joml.Vector3d

class TileComponent : PooledComponent() {

    val centroid = Vector3d()
    @JvmField
    @EntityId
    val adjacent = IntBag(6)

    override fun reset() {
        centroid.zero()
        adjacent.clear()
    }
}