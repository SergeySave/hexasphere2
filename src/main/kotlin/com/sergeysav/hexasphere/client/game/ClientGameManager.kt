package com.sergeysav.hexasphere.client.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.sergeysav.hexasphere.client.game.camera.CameraControlsSystem
import com.sergeysav.hexasphere.client.game.camera.CameraSystem
import com.sergeysav.hexasphere.client.game.font.FontManagerSystem
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.client.game.tile.feature.CityRenderSystem
import com.sergeysav.hexasphere.client.game.ui.DebugUIRenderSystem
import com.sergeysav.hexasphere.client.game.ui.MinimapUIRenderSystem
import com.sergeysav.hexasphere.client.game.ui.SelectionUIRenderSystem
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.common.ecs.SystemPriority
import com.sergeysav.hexasphere.common.game.Game

class ClientGameManager(renderSystemInit: WorldConfigurationBuilder.()->Unit) {

    val world: World = Game.create {
        renderSystemInit()

        with(SystemPriority.NON_PROCESSING, SettingsSystem())
        with(SystemPriority.NON_PROCESSING, CameraSystem())
        
        with(SystemPriority.SETUP, FontManagerSystem())

        with(SystemPriority.UI, SelectionUIRenderSystem())

        with(SystemPriority.CONTROLS, CameraControlsSystem())

        with(SystemPriority.PRE_RENDER, SelectionSystem())
        with(SystemPriority.PRE_RENDER, RenderDataSystem())

        with(SystemPriority.RENDER, CityRenderSystem())

        with(SystemPriority.LAST_UI, DebugUIRenderSystem())
        with(SystemPriority.LAST_UI, MinimapUIRenderSystem())

        with(SystemPriority.CLEANUP, InputManagerSystem())
    }
    private val renderData = world.getSystem(RenderDataSystem::class.java)!!
    val inputManager = world.getSystem(InputManagerSystem::class.java)!!

    init {
        world.getSystem(DebugUIRenderSystem::class.java).isEnabled = false
        world.getSystem(MinimapUIRenderSystem::class.java).isEnabled = false
    }

    fun process(delta: Double, width: Double, height: Double) {
        renderData.initializeFrame(width, height)

        world.delta = delta.toFloat()
        world.process()
    }

    fun dispose() {
        world.dispose()
    }
}