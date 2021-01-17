package com.sergeysav.hexasphere.client.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.sergeysav.hexasphere.client.game.font.FontManagerSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.client.game.tile.feature.CityRenderSystem
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.client.game.ui.DebugUIRenderSystem
import com.sergeysav.hexasphere.common.game.Game
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem

class ClientGameManager(renderSystemInit: WorldConfigurationBuilder.()->Unit) {

    val world: World = Game.create {
        renderSystemInit()
        with(SelectionSystem())
        with(RenderDataSystem())
        with(SettingsSystem())
        with(CityRenderSystem())
        with(FontManagerSystem())
        with(DebugUIRenderSystem())
        with(InputManagerSystem())
    }
    val tile = world.getSystem(TileSystem::class.java)!!
    val tileType = world.getSystem(TileTypeSystem::class.java)!!
    val selection = world.getSystem(SelectionSystem::class.java)!!
    val renderData = world.getSystem(RenderDataSystem::class.java)!!
    val inputManager = world.getSystem(InputManagerSystem::class.java)!!

    init {
        world.getSystem(DebugUIRenderSystem::class.java).isEnabled = false
    }

    fun process(delta: Double, width: Double, height: Double) {
        renderData.initializeFrame(width, height)

        world.delta = delta.toFloat()
        world.process()

        // Update should happen after everything else updates
        inputManager.update(delta)
    }

    fun dispose() {
        world.dispose()
    }
}