package com.sergeysav.hexasphere.common.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.ecs.SystemPriority
import com.sergeysav.hexasphere.common.game.tile.TileCleanerSystem
import com.sergeysav.hexasphere.common.game.tile.TileOwnerSystem
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeatureSystem
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem

object Game {

    inline fun create(inner: WorldConfigurationBuilder.()->Unit): World {
        val configuration = WorldConfigurationBuilder()
            .apply(inner)
            .with(SystemPriority.NON_PROCESSING, GroupManager())
            .with(SystemPriority.NON_PROCESSING, TileTypeSystem())
            .with(SystemPriority.NON_PROCESSING, TileSystem())
            .with(SystemPriority.NON_PROCESSING, TileFeatureSystem())
            .with(SystemPriority.NON_PROCESSING, TileOwnerSystem())
            .with(SystemPriority.CLEANUP, TileCleanerSystem())
            .build()
        return World(configuration)
    }
}