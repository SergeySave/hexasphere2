package com.sergeysav.hexasphere.common.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.game.tile.TileCleanerSystem
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem

object Game {

    inline fun create(inner: WorldConfigurationBuilder.()->Unit): World {
        val configuration = WorldConfigurationBuilder()
            .apply(inner)
            .with(GroupManager())
            .with(TileCleanerSystem())
            .with(TileTypeSystem())
            .with(TileSystem())
            .build()
        return World(configuration)
    }
}