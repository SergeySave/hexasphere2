package com.sergeysav.hexasphere.common.game.tile

import com.artemis.ComponentMapper
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem
import com.sergeysav.hexasphere.common.game.player.Player
import com.sergeysav.hexasphere.common.game.player.PlayerComponent

class TileOwnerSystem : NonProcessingSystem() {

    private lateinit var tileMapper: ComponentMapper<TileComponent>
    private lateinit var playerMapper: ComponentMapper<PlayerComponent>

    fun getTileOwner(tile: Int): Player? {
        if (!tileMapper.has(tile)) return null
        return playerMapper[tile]?.player
    }
}