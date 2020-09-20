package com.sergeysav.hexasphere.common.icosahedron

import org.joml.Vector3d
import org.joml.Vector3dc

class SubdividedIcosahedron(subdivisionLevel: Int, normalize: Boolean) {
    val vertices: List<Vector3dc>
    val faces: List<Triangle>

    init {
        val subdivisionLimit = subdivisionLevel + 1
        val verts = mutableListOf<Vector3dc>()
        val tris = mutableListOf<Triangle>()
        val originalVerts = IntArray(Icosahedron.vertices.size) { -1 }
        val subdividedEdges = mutableMapOf<Pair<Int, Int>, IntArray>()
        for (face in Icosahedron.faces) {
            val minVert = minOf(face.v1, face.v2, face.v3)
            val maxVert = maxOf(face.v1, face.v2, face.v3)
            val centVert = face.v1 + face.v2 + face.v3 - minVert - maxVert
            val edges = listOf(minVert to centVert, minVert to maxVert, centVert to maxVert)

            // First we want to ensure that the edges are subdivided
            for (edge in edges) {
                if (!subdividedEdges.containsKey(edge)) {
                    val subdivided = IntArray(subdivisionLimit + 1)
                    if (originalVerts[edge.first] == -1) {
                        val newVec = Vector3d(Icosahedron.vertices[edge.first])
                        verts.add(newVec)
                        originalVerts[edge.first] = verts.lastIndex
                        subdivided[0] = verts.lastIndex
                    } else {
                        subdivided[0] = originalVerts[edge.first]
                    }
                    for (i in 1..subdivisionLevel) {
                        val newVec = Icosahedron.vertices[edge.first].lerp(Icosahedron.vertices[edge.second],
                                i.toDouble() / subdivisionLimit, Vector3d())
                        if (normalize) {
                            newVec.normalize()
                        }
                        verts.add(newVec)
                        subdivided[i] = verts.lastIndex
                    }
                    if (originalVerts[edge.second] == -1) {
                        val newVec = Vector3d(Icosahedron.vertices[edge.second])
                        verts.add(newVec)
                        originalVerts[edge.second] = verts.lastIndex
                        subdivided[subdivided.lastIndex] = verts.lastIndex
                    } else {
                        subdivided[subdivided.lastIndex] = originalVerts[edge.second]
                    }
                    subdividedEdges[edge] = subdivided
                }
            }

            // Now we want to subdivide the face itself
            val pathTop = subdividedEdges[edges[0]]!!
            val pathBottom = subdividedEdges[edges[1]]!!
            var pathRight = subdividedEdges[edges[2]]!!
            for (col in subdivisionLevel downTo 0) {
                val pathLeft = IntArray(col + 1) { -1 }
                val top = pathTop[col]
                val bottom = pathBottom[col]

                for (row in 0..col) {
                    when (row) {
                        0 -> pathLeft[row] = top
                        col -> pathLeft[row] = bottom
                        else -> {
                            val newVec = verts[top].lerp(verts[bottom], row / col.toDouble(), Vector3d())
                            if (normalize) {
                                newVec.normalize()
                            }
                            verts.add(newVec)
                            pathLeft[row] = verts.lastIndex
                        }
                    }
                    if (row != 0) {
                        tris.add(Triangle(pathLeft[row - 1], pathRight[row], pathLeft[row]))
                    }
                    tris.add(Triangle(pathLeft[row], pathRight[row], pathRight[row + 1]))
                }

                pathRight = pathLeft
            }
        }

        vertices = verts
        faces = tris
    }

    override fun toString(): String {
        return "SubdividedIcosahedron(vertices=$vertices, faces=$faces)"
    }
}

fun Icosahedron.subdivide(level: Int, normalize: Boolean = true) = SubdividedIcosahedron(level, normalize)
fun Icosahedron.wrap() = SubdividedIcosahedron(0, false)
