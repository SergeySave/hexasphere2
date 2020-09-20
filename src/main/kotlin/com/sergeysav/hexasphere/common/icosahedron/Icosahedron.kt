package com.sergeysav.hexasphere.common.icosahedron

import org.joml.Vector3d
import org.joml.Vector3dc

object Icosahedron {
    val vertices: List<Vector3dc>
    val faces: List<Triangle>

    init {
        val one = 1.0
        val phi = 1.618033988749894848204586834

        vertices = listOf(
                Vector3d(0.0, one, phi),
                Vector3d(0.0, -one, phi),
                Vector3d(phi, 0.0, one),
                Vector3d(-phi, 0.0, one),
                Vector3d(0.0, one, -phi),
                Vector3d(0.0, -one, -phi),
                Vector3d(phi, 0.0, -one),
                Vector3d(-phi, 0.0, -one),
                Vector3d(one, phi, 0.0),
                Vector3d(one, -phi, 0.0),
                Vector3d(-one, phi, 0.0),
                Vector3d(-one, -phi, 0.0)
        ).map { it.normalize() }
        faces = listOf(
                Triangle(0, 1, 2),
                Triangle(0, 1, 3),
                Triangle(0, 2, 8),
                Triangle(0, 3, 10),
                Triangle(0, 8, 10),

                Triangle(6, 2, 8),
                Triangle(6, 4, 8),
                Triangle(6, 4, 5),
                Triangle(6, 5, 9),
                Triangle(6, 2, 9),

                Triangle(1, 2, 9),
                Triangle(4, 8, 10),
                Triangle(3, 7, 10),
                Triangle(4, 7, 10),
                Triangle(4, 5, 7),

                Triangle(1, 9, 11),
                Triangle(1, 3, 11),
                Triangle(3, 7, 11),
                Triangle(5, 7, 11),
                Triangle(5, 9, 11)
        )
    }
}
