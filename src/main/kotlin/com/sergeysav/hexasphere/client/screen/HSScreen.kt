package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.game.ClientGameManager
import com.sergeysav.hexasphere.client.game.skybox.SkyboxRenderSystem
import com.sergeysav.hexasphere.client.game.tile.HexasphereRenderSystem
import com.sergeysav.hexasphere.client.game.ui.DebugUIRenderSystem
import com.sergeysav.hexasphere.client.hexasphere.clientAddToWorld
import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.localization.L10n
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import com.sergeysav.hexasphere.common.ecs.SystemPriority
import com.sergeysav.hexasphere.common.ecs.flipEnabled
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.withSubdivisionLevel
import mu.KotlinLogging

class HSScreen : Screen {

    private val logger = KotlinLogging.logger {  }
    private lateinit var clientGameManager: ClientGameManager

    private var time: Double = 0.0

    override fun create() {
        val hex = Hexasphere.withSubdivisionLevel(5)

        clientGameManager = ClientGameManager {
            with(SystemPriority.RENDER,
                HexasphereRenderSystem(
                    hex.pentagons, hex.hexagons
                )
            )
            with(SystemPriority.RENDER, SkyboxRenderSystem("/skybox/nasa2k.ktx"))
        }

        hex.clientAddToWorld(clientGameManager.world)

        L10n.load("en_US")
        logger.trace { "Finished Create" }
    }

    override fun resume() {
        logger.trace { "Resume" }
        time = 0.0
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += delta

//        val viewDirection = camera!!.projectToWorld(vec2.set(
//            (2 * clientGameManager.inputManager.getMouseX() / width - 1).toFloat(),
//            (2 * clientGameManager.inputManager.getMouseY() / height - 1).toFloat()
//        ), vec3a)
//        if (clientGameManager.inputManager.isMouseButtonJustUp(MouseButton.RIGHT)) {
//            TileSelectionHelper.ifCouldSelectTile(clientGameManager, viewDirection, camera!!.position) { bestTile ->
//                clientGameManager.tileType.setType<CoastTileTypeComponent>(bestTile)
//            }
//        }
//        if (clientGameManager.inputManager.isMouseButtonJustUp(MouseButton.LEFT) && clientGameManager.inputManager.getMouseButtonDownTime(MouseButton.LEFT) < mouseHoldDragTime) {
//            TileSelectionHelper.selectBestTileOrNone(clientGameManager, viewDirection, camera!!.position) { bestTile ->
//                clientGameManager.selection.selectedTile = bestTile
//            }
//        }

        if (clientGameManager.inputManager.isKeyJustUp(Key.F3)) {
            clientGameManager.world.getSystem(DebugUIRenderSystem::class.java).flipEnabled()
        }

        clientGameManager.process(delta, width.toDouble(), height.toDouble())

        return ScreenAction.noop()
    }

    override fun pause() {
        logger.trace { "Pause" }
    }

    override fun dispose() {
        logger.trace { "Dispose" }
        if (::clientGameManager.isInitialized) clientGameManager.dispose()
    }

    override fun onKey(action: Action, key: Key, keyModifiers: KeyModifiers) {
        clientGameManager.inputManager.handleOnKey(action, key, keyModifiers)
    }

    override fun onMouseButton(action: Action, mouseButton: MouseButton, keyModifiers: KeyModifiers) {
        clientGameManager.inputManager.handleOnMouseButton(action, mouseButton, keyModifiers)
    }

    override fun onMouseMove(x: Double, y: Double, reset: Boolean) {
        clientGameManager.inputManager.onMouseMove(x, y, reset)
    }

    override fun onScroll(x: Double, y: Double) {
        clientGameManager.inputManager.onScroll(x, y)
    }
}