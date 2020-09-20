package com.sergeysav.hexasphere.common.icosahedron

data class Triangle(
        val v1: Int,
        val v2: Int,
        val v3: Int
)

fun Triangle.vertices() = arrayOf(v1, v2, v3)
