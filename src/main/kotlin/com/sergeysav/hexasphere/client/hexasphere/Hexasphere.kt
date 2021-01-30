package com.sergeysav.hexasphere.client.hexasphere

import com.artemis.ArchetypeBuilder
import com.artemis.World
import com.sergeysav.hexasphere.client.game.tile.TileFeaturePositionComponent
import com.sergeysav.hexasphere.client.game.tile.TileGeometryComponent
import com.sergeysav.hexasphere.common.Vectors
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.HexasphereTile
import com.sergeysav.hexasphere.common.hexasphere.addComponents
import com.sergeysav.hexasphere.common.hexasphere.addToWorld
import com.sergeysav.hexasphere.common.hexasphere.computeTileRotation
import org.joml.Matrix3d
import org.joml.Vector3d
import kotlin.math.sqrt

private val threePacking = 1 + 2 / sqrt(3.0)

fun Hexasphere.clientAddToWorld(world: World) {
    val archetype = ArchetypeBuilder()
        .add(TileGeometryComponent::class.java)
        .add(TileFeaturePositionComponent::class.java)
        .addComponents()
        .build(world)

    val geomMapper = world.getMapper(TileGeometryComponent::class.java)
    val featMapper = world.getMapper(TileFeaturePositionComponent::class.java)

    val tileEntityMap = mutableMapOf<HexasphereTile, Int>()
    val mat = Matrix3d()
    val matInv = Matrix3d()
    val temp1 = Vector3d()
    val temp2 = Vector3d()
    val temp3 = Vector3d()

    for (tile in tiles) {
        val tileEntity = world.create(archetype)
        tileEntityMap[tile] = tileEntity
        geomMapper[tileEntity].apply {
            when (tile) {
                is HexasphereTile.Pentagon -> {
                    vertices[0].set(tile.v1)
                    vertices[1].set(tile.v2)
                    vertices[2].set(tile.v3)
                    vertices[3].set(tile.v4)
                    vertices[4].set(tile.v5)
                }
                is HexasphereTile.Hexagon -> {
                    vertices[0].set(tile.v1)
                    vertices[1].set(tile.v2)
                    vertices[2].set(tile.v3)
                    vertices[3].set(tile.v4)
                    vertices[4].set(tile.v5)
                    vertices[5].set(tile.v6)
                }
            }
        }
        featMapper[tileEntity].apply {
            // mat flattens the tile to the z = 0 plane
            computeTileRotation(tile.centroid, mat)
            mat.invert(matInv)
            var inscribedRadius = Double.POSITIVE_INFINITY

            for (i in tile.vertices.indices) {
                // temp1 = flatten(vertex i + 1)
                tile.vertices[(i + 1) % tile.vertices.size].sub(tile.centroid, temp1).mul(mat)
                temp1.z = 0.0
                // temp2 = flatten(vertex i)
                tile.vertices[i].sub(tile.centroid, temp2).mul(mat)
                temp2.z = 0.0
                // temp2 = direction vector from flattened vertex i to flattened vertex i + 1
                temp2.sub(temp1).normalize()
                // temp3 = vector from flattened vertex i to centroid
                Vectors.ZERO3d.sub(temp1, temp3)
                // project vector to centroid onto the vector to flattened vertex i + 1
                val projection = temp3.dot(temp2)
                // temp2 = the projected point
                temp2.mul(projection).add(temp1)
                // compute the distance from the centroid to the projected point
                inscribedRadius = minOf(inscribedRadius, temp2.distance(Vectors.ZERO3d))
            }
            this.radius = inscribedRadius / (2.2) // 2.2 is rounded up from 2.154 https://en.wikipedia.org/wiki/Circle_packing_in_a_circle
            tile.vertices[0].sub(tile.centroid, temp1).mul(mat)
            temp1.z = 0.0
            temp1.normalize(inscribedRadius)

            this.positions[0][0].set(Vectors.ZERO3d)//.set(tile.centroid)

            temp1.div(+2.0, this.positions[1][0])//.mul(matInv).add(tile.centroid)
            temp1.div(-2.0, this.positions[1][1])//.mul(matInv).add(tile.centroid)

            temp1.rotateZ(Math.toRadians(000.0), this.positions[2][0]).div(threePacking)//.mul(matInv).add(tile.centroid)
            temp1.rotateZ(Math.toRadians(120.0), this.positions[2][1]).div(threePacking)//.mul(matInv).add(tile.centroid)
            temp1.rotateZ(Math.toRadians(240.0), this.positions[2][2]).div(threePacking)//.mul(matInv).add(tile.centroid)
        }
    }

    addToWorld(world, tileEntityMap)
}
