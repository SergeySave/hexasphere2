package com.sergeysav.hexasphere.common.game.tile

import com.artemis.BaseSystem
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.game.Groups

class TileCleanerSystem : BaseSystem() {

    override fun processSystem() {
        val groupManager = world.getSystem(GroupManager::class.java)
        val entities = groupManager.getEntityIds(Groups.DIRTY_TILE)
        for (i in 0 until entities.size()) {
            groupManager.remove(i, Groups.DIRTY_TILE)
        }
    }
}