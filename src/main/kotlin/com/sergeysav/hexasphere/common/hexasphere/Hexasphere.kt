package com.sergeysav.hexasphere.common.hexasphere

import com.artemis.ArchetypeBuilder
import com.artemis.World
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.color.Color
import com.sergeysav.hexasphere.common.game.Groups
import com.sergeysav.hexasphere.common.game.player.Player
import com.sergeysav.hexasphere.common.game.player.PlayerComponent
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.game.tile.feature.CityFeatureComponent
import com.sergeysav.hexasphere.common.game.tile.type.GrasslandTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import com.sergeysav.hexasphere.common.game.tile.type.setType
import com.sergeysav.hexasphere.common.icosahedron.Icosahedron
import com.sergeysav.hexasphere.common.icosahedron.SubdividedIcosahedron
import com.sergeysav.hexasphere.common.icosahedron.Triangle
import com.sergeysav.hexasphere.common.icosahedron.subdivide
import com.sergeysav.hexasphere.common.icosahedron.vertices
import org.joml.Vector3d
import java.util.Random
import kotlin.math.PI
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
            val tileMap = mutableMapOf<Int, Int>()
            val tileMapInv = mutableMapOf<HexasphereTile, Int>()
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
                center.cross(vertices[0], up).normalize().mul(-1.0) // up points perpendicular to vertices[0]
                center.cross(up, right).normalize() // now right points in the same direction as vertices[0]

                vertices = vertices.sortedBy {
                    // Sort by positive angle around the center using [up] as the y axis and and [right] as the x axis
                    // sorted this way so that vertices[0] doesnt change (though that probably doesn't matter any more)
                    it.sub(center, delta).normalize()
                    (atan2(up.dot(delta), right.dot(delta)) + 2 * PI) % (2 * PI)
                }
                val tile = if (vertices.size == 5) {
                    HexasphereTile.Pentagon(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4])
                } else {
                    hexagons++
                    HexasphereTile.Hexagon(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5])
                }
                tileMap[vert] = tiles.size
                tiles.add(tile)
                tileMapInv[tile] = vert
            }

            val adj = MutableList(tiles.size) { IntArray(0) }
            for (tile in tiles) {
                val vert = tileMapInv[tile]!!
                val faces = faceMap[vert]!!

                val center = ico.vertices[vert]
                // This MUST be sorted the same way as the tile vertices
                center.cross(tile.vertices[0], up).normalize().mul(-1.0) // up points perpendicular to vertices[0]
                center.cross(up, right).normalize() // now right points in the same direction as vertices[0]

                // Compute the adjacent vertices
                // First take all of the icosahedral faces adjacent to this vertex (in the icosahedron)
                adj[tileMap[vert]!!] = faces.asSequence()
                        // Split the list of faces into a list of all of the vertices in all of the faces (with repeats)
                    .flatMap { sequenceOf(it.v1, it.v2, it.v3) }
                        // Remove the center (the icosahedral vertex)
                    .filter { it != vert }
                        // Remove duplicates
                    .distinct()
                        // Sort them the same way that the vertices were sorted
                    .sortedBy {
                        // Sort by positive angle around the center using [up] as the y axis and and [right] as the x axis
                        // sorted this way so that adjacent[i] corresponds to vertex[i]
                        ico.vertices[it].sub(center, delta).normalize()
                        (atan2(up.dot(delta), right.dot(delta)) + 2 * PI) % (2 * PI)
                    }
                        // Now map the adjacent icosahedral vertices to tiles/faces in the dual (i.e. the hexasphere)
                    .map { tileMap[it]!! }
                        // Convert to an array
                    .toList().toIntArray()
            }
            return Hexasphere(tiles, adj, hexagons)
        }
    }
}

fun Hexasphere.Companion.withSubdivisionLevel(level: Int) = fromDualOf(Icosahedron.subdivide(level, true))

fun ArchetypeBuilder.addComponents() = this.add(TileComponent::class.java)

fun Hexasphere.addToWorld(world: World, tileEntityMap: Map<HexasphereTile, Int>? = null) {
    val map = tileEntityMap ?: run {
        val archetype = ArchetypeBuilder().addComponents().build(world)
        val tempMap = mutableMapOf<HexasphereTile, Int>()

        for (tile in tiles) {
            val tileEntity = world.create(archetype)
            tempMap[tile] = tileEntity
        }

        tempMap
    }

    val tileMapper = world.getMapper(TileComponent::class.java)
    val groupSystem = world.getSystem(GroupManager::class.java)
    val tileTypeSystem = world.getSystem(TileTypeSystem::class.java)
    val cityMapper = world.getMapper(CityFeatureComponent::class.java)

    val player1 = Player(Color.RED, Color(0xff, 0xa5, 0x00))
    val player2 = Player(Color.BLUE, Color(0x00, 0xff, 0xff))

    val playerMapper = world.getMapper(PlayerComponent::class.java)
    val random = Random()

    for (tileIdx in tiles.indices) {
        val tile = tiles[tileIdx]
        val tileEntity = map[tile]!!
        groupSystem.add(tileEntity, Groups.DIRTY_TILE)
        tileMapper[tileEntity].apply {
            this.centroid.set(tile.centroid)
            for (adj in adjacencies[tileIdx]) {
                this.adjacent.add(map[tiles[adj]] ?: error("This should be impossible"))
            }
        }
        tileTypeSystem.setType<GrasslandTileTypeComponent>(tileEntity)
    }

    for (i in 0 until 32) {
        val tile = map.values.random()
        cityMapper.create(tile)
        val player = if (random.nextBoolean()) player1 else player2
        playerMapper.create(tile).apply {
            this.player = player
        }
        val adjacent = tileMapper[tile].adjacent
        for (j in 0 until adjacent.size()) {
            playerMapper.create(adjacent[j]).apply {
                this.player = player
            }
        }
    }
}
