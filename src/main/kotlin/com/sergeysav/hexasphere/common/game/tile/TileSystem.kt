package com.sergeysav.hexasphere.common.game.tile

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import org.joml.Vector3d
import org.joml.Vector3fc

class TileSystem : BaseEntitySystem(Aspect.all(TileComponent::class.java)) {

    private val vec3 = Vector3d()
    private lateinit var tileMapper: ComponentMapper<TileComponent>

    fun getClosestTile(position: Vector3fc): Int {
        val tiles = subscription.entities
        var minimum = Double.POSITIVE_INFINITY
        var minTile: Int = -1

        for (i in 0 until tiles.size()) {
            val tile = tiles[i]
            val dist2 = tileMapper[tile].centroid.distanceSquared(vec3.set(position))
            if (dist2 < minimum) {
                minimum = dist2
                minTile = tile
            }
        }

        return minTile
    }

    override fun begin() {
        isEnabled = false
    }

    override fun processSystem() {}
}