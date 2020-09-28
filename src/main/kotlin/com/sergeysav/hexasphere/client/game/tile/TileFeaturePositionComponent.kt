package com.sergeysav.hexasphere.client.game.tile

import com.artemis.PooledComponent
import org.joml.Vector3d

class TileFeaturePositionComponent : PooledComponent() {

    var radius: Double = 0.0
    val positions = Array(3) { Array(it + 1) { Vector3d() } }

    override fun reset() {
        radius = 0.0
        for (i in positions.indices) {
            for (j in positions[i].indices) {
                positions[i][j].zero()
            }
        }
    }
}