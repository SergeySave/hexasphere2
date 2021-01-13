package com.sergeysav.hexasphere.common.game.tile

import com.artemis.BaseSystem
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.game.Groups

class TileCleanerSystem : BaseSystem() {

    override fun processSystem() {
        val groupManager = world.getSystem(GroupManager::class.java)
        val entities = groupManager.getEntityIds(Groups.DIRTY_TILE)
        for (i in entities.size() - 1 downTo 0) {
            groupManager.remove(entities[i], Groups.DIRTY_TILE)
        }
    }
}