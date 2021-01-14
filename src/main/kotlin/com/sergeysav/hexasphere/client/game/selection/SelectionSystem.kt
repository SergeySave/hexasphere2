package com.sergeysav.hexasphere.client.game.selection

import com.artemis.BaseSystem
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.common.game.Groups

class SelectionSystem : BaseSystem() {

    private lateinit var groupManager: GroupManager

    var selectedTile: Int = -1
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != -1) {
                groupManager.add(oldValue, Groups.DIRTY_TILE)
            }
            if (value != -1) {
                groupManager.add(selectedTile, Groups.DIRTY_TILE)
            }
        }

    override fun processSystem() {
    }

    fun clearSelectedTile() {
        selectedTile = -1
    }
}