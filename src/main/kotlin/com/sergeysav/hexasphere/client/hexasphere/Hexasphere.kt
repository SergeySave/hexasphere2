package com.sergeysav.hexasphere.client.hexasphere

import com.artemis.ArchetypeBuilder
import com.artemis.World
import com.sergeysav.hexasphere.client.game.tile.TileGeometryComponent
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.HexasphereTile
import com.sergeysav.hexasphere.common.hexasphere.addToWorld

fun Hexasphere.clientAddToWorld(world: World) {
    val archetype = ArchetypeBuilder()
        .add(TileGeometryComponent::class.java)
        .add(TileComponent::class.java)
        .build(world)

    val geomMapper = world.getMapper(TileGeometryComponent::class.java)

    val tileEntityMap = mutableMapOf<HexasphereTile, Int>()

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
    }

    addToWorld(world, tileEntityMap)
}
