package com.sergeysav.hexasphere.client.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.common.game.Game
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem

class ClientGameManager(renderSystemInit: WorldConfigurationBuilder.()->Unit) {

    val world: World = Game.create {
        renderSystemInit()
        with(SelectionSystem())
    }
    val tileSystem = world.getSystem(TileSystem::class.java)!!
    val tileTypeSystem = world.getSystem(TileTypeSystem::class.java)!!
    val selectionSystem = world.getSystem(SelectionSystem::class.java)!!

    fun process(delta: Double) {
        world.delta = delta.toFloat()
        world.process()
    }

    fun dispose() {
        world.dispose()
    }
}