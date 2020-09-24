package com.sergeysav.hexasphere.common.hexasphere

import org.joml.Vector3d
import org.joml.Vector3dc

sealed class HexasphereTile {

    abstract val vertices: List<Vector3dc>
    abstract val centroid: Vector3dc

    data class Pentagon(
         val v1: Vector3dc,
         val v2: Vector3dc,
         val v3: Vector3dc,
         val v4: Vector3dc,
         val v5: Vector3dc
    ) : HexasphereTile() {
        override val vertices = listOf(v1, v2, v3, v4, v5)
        override val centroid: Vector3dc = Vector3d(v1).add(v2).add(v3).add(v4).add(v5).div(5.0)
    }

    data class Hexagon(
            val v1: Vector3dc,
            val v2: Vector3dc,
            val v3: Vector3dc,
            val v4: Vector3dc,
            val v5: Vector3dc,
            val v6: Vector3dc
    ) : HexasphereTile() {
        override val vertices = listOf(v1, v2, v3, v4, v5, v6)
        override val centroid: Vector3dc = Vector3d(v1).add(v2).add(v3).add(v4).add(v5).add(v6).div(6.0)
    }
}