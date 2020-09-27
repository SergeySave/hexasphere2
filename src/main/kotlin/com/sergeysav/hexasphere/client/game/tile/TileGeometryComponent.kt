package com.sergeysav.hexasphere.client.game.tile

import com.artemis.PooledComponent
import org.joml.Vector3d

class TileGeometryComponent : PooledComponent() {

    var vBufferPos: Int = -1
    var iBufferPos: Int = -1
    val vertices = Array(6) { Vector3d() }

    override fun reset() {
        for (element in vertices) {
            element.zero()
        }
        vBufferPos = -1
        iBufferPos = -1
    }
}