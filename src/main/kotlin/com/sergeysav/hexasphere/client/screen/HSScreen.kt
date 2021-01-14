package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.bgfx.PerspectiveCamera
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.game.ClientGameManager
import com.sergeysav.hexasphere.client.game.TileSelectionHelper
import com.sergeysav.hexasphere.client.game.skybox.SkyboxRenderSystem
import com.sergeysav.hexasphere.client.game.tile.HexasphereRenderSystem
import com.sergeysav.hexasphere.client.hexasphere.clientAddToWorld
import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.localization.L10n
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import com.sergeysav.hexasphere.common.game.tile.type.CoastTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.setType
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.withSubdivisionLevel
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.pow

class HSScreen : Screen {

    private lateinit var clientGameManager: ClientGameManager
    private var camera: PerspectiveCamera? = null
    private var time: Double = 0.0
    private val vec2 = Vector2f()
    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val mouseHoldDragTime = 0.1

    override fun create() {
        val hex = Hexasphere.withSubdivisionLevel(5)

        clientGameManager = ClientGameManager {
            with(
                HexasphereRenderSystem(
                    hex.pentagons, hex.hexagons,
                    ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes")
                )
            )
            with(SkyboxRenderSystem("/skybox/nasa2k.ktx"))
        }

        hex.clientAddToWorld(clientGameManager.world)

        camera = PerspectiveCamera(zNear = 0.1f, zFar = 100f).apply {
            setFovDeg(45f)
            setPosition(vec3a.set(2f, 0f, 0f))
            lookAt(vec3a.set(0f, 0f, 0f))
        }

        L10n.load("en_US")
    }

    override fun resume() {
        time = 0.0
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += delta

        camera?.run {
            val speed = (60 * delta * 0.1f * (position.length().toDouble() / 5).pow(1.5)).toFloat()

            setAspect(width, height)
            translateIn(forward, speed * (clientGameManager.inputManager.getKeyDownInt(Key.SPACE) - clientGameManager.inputManager.getKeyDownInt(Key.LEFT_SHIFT)))
            rotateAround(vec3a.zero(), right, -speed * (clientGameManager.inputManager.getKeyDownInt(Key.W) - clientGameManager.inputManager.getKeyDownInt(Key.S)))
            rotateAround(vec3a.zero(), up, speed * (clientGameManager.inputManager.getKeyDownInt(Key.D) - clientGameManager.inputManager.getKeyDownInt(Key.A)))
            rotate(forward, (5 * delta).toFloat() * (clientGameManager.inputManager.getKeyDownInt(Key.Q) - clientGameManager.inputManager.getKeyDownInt(Key.E)))

            translateIn(forward, 3f * speed * clientGameManager.inputManager.getScrollY().toFloat())

            if (clientGameManager.inputManager.isMouseButtonDown(MouseButton.LEFT) && !clientGameManager.inputManager.isMouseButtonJustDown(MouseButton.LEFT)) {
                up.mul(clientGameManager.inputManager.getMouseDX().toFloat(), vec3a)
                right.mul(clientGameManager.inputManager.getMouseDY().toFloat(), vec3b)
                vec3a.add(vec3b) // vec3a should be the rotation axis
                if (vec3a.lengthSquared() > 1e-4) {
                    rotateAround(vec3b.zero(), vec3a, -speed * 0.075f)
                }
            }

            val minLen = 1.1f
            if (position.lengthSquared() < minLen * minLen) {
                position.normalize(minLen)
            }
            if (position.lengthSquared() > 8 * 8) {
                position.normalize(8f)
            }

            update(clientGameManager.renderData.hexasphereView)
            update(clientGameManager.renderData.skyboxView, true)
            update(clientGameManager.renderData.featuresView)
        }

        val viewDirection = camera!!.projectToWorld(vec2.set(
            (2 * clientGameManager.inputManager.getMouseX() / width - 1).toFloat(),
            (2 * clientGameManager.inputManager.getMouseY() / height - 1).toFloat()
        ), vec3a)
        if (clientGameManager.inputManager.isMouseButtonJustUp(MouseButton.RIGHT)) {
            TileSelectionHelper.ifCouldSelectTile(clientGameManager, viewDirection, camera!!.position) { bestTile ->
                clientGameManager.tileType.setType<CoastTileTypeComponent>(bestTile)
            }
        }
        if (clientGameManager.inputManager.isMouseButtonJustUp(MouseButton.LEFT) && clientGameManager.inputManager.getMouseButtonDownTime(MouseButton.LEFT) < mouseHoldDragTime) {
            TileSelectionHelper.selectBestTileOrNone(clientGameManager, viewDirection, camera!!.position) { bestTile ->
                clientGameManager.selection.selectedTile = bestTile
            }
        }

        clientGameManager.process(delta, width.toDouble(), height.toDouble())

        return ScreenAction.noop()
    }

    override fun pause() {
    }

    override fun dispose() {
        camera?.dispose()
        camera = null
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