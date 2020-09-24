package com.sergeysav.hexasphere.common.hexasphere

import com.sergeysav.hexasphere.common.icosahedron.*
import org.joml.Vector3d
import kotlin.math.atan2

class Hexasphere private constructor(
        val tiles: List<HexasphereTile>,
        val adjacencies: List<IntArray>,
        val hexagons: Int
) {
    val pentagons: Int
        get() = 12

    companion object {
        fun fromDualOf(ico: SubdividedIcosahedron): Hexasphere {
            val faceMap = mutableMapOf<Int, MutableList<Triangle>>()
            for (face in ico.faces) {
                for (vert in face.vertices()) {
                    faceMap[vert] = faceMap.getOrElse(vert, ::mutableListOf).apply {
                        add(face)
                    }
                }
            }
            var hexagons = 0
            val tileMap = mutableMapOf<Int, HexasphereTile>()
            val tiles = mutableListOf<HexasphereTile>()
            val up = Vector3d()
            val right = Vector3d()
            val delta = Vector3d()
            for ((vert, faces) in faceMap) {
                var vertices = faces.map {
                    val center = Vector3d()
                    center.add(ico.vertices[it.v1])
                    center.add(ico.vertices[it.v2])
                    center.add(ico.vertices[it.v3])
                    center.normalize()
                }
                val center = ico.vertices[vert]
                center.cross(vertices[0], up).normalize() // up now points up when looking at the tile from outside
                vertices[0].sub(center, right).normalize() // right now points right when looking at the tile from outside
                vertices = vertices.sortedBy {
                    it.sub(center, delta).normalize()
                    atan2(up.dot(delta), right.dot(delta))
                }
                val tile = if (vertices.size == 5) {
                    HexasphereTile.Pentagon(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4])
                } else {
                    hexagons++
                    HexasphereTile.Hexagon(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5])
                }
                tiles.add(tile)
                tileMap[vert] = tile
            }
            val adj = MutableList(ico.vertices.size) { IntArray(0) }
            for ((vert, faces) in faceMap) {
                adj[vert] = faces.asSequence()
                        .flatMap { sequenceOf(it.v1, it.v2, it.v3) }
                        .filter { it != vert }
                        .distinct().toList().toIntArray()
            }
            return Hexasphere(tiles, adj, hexagons)
        }
    }
}

fun Hexasphere.Companion.withSubdivisionLevel(level: Int) = fromDualOf(Icosahedron.subdivide(level, true))
