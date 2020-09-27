package com.sergeysav.hexasphere.client.screen

import com.artemis.World
import com.sergeysav.hexasphere.client.bgfx.Framebuffer
import com.sergeysav.hexasphere.client.bgfx.PerspectiveCamera
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.client.bgfx.ViewArray
import com.sergeysav.hexasphere.client.bgfx.set
import com.sergeysav.hexasphere.client.camera.Camera3d
import com.sergeysav.hexasphere.client.game.skybox.SkyboxRenderSystem
import com.sergeysav.hexasphere.client.game.tile.HexasphereRenderSystem
import com.sergeysav.hexasphere.client.hexasphere.clientAddToWorld
import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.InputManager
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.math.SphereRayIntersect
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import com.sergeysav.hexasphere.common.Vectors
import com.sergeysav.hexasphere.common.game.Game
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import com.sergeysav.hexasphere.common.game.tile.type.CoastTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.withSubdivisionLevel
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.pow

class HSScreen : Screen {

    private lateinit var world: World
    private lateinit var tileSystem: TileSystem
    private lateinit var tileTypeSystem: TileTypeSystem
    private var camera: Camera3d? = null
    private var time: Double = 0.0
    private val inputManager = InputManager()
    private val vec2 = Vector2f()
    private val vec3 = Vector3f()
    private val vec3b = Vector3f()
    private val hexasphereView = View(0)
    private val skyboxView = View(1)
    private val views: ViewArray = intArrayOf(hexasphereView.id, skyboxView.id)

    override fun create() {
        val hex = Hexasphere.withSubdivisionLevel(10)

        world = Game.create {
            with(HexasphereRenderSystem(hex.pentagons, hex.hexagons, hexasphereView,
                ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes")))
            with(SkyboxRenderSystem("/skybox/nasa2k.ktx", skyboxView))
        }
        tileSystem = world.getSystem(TileSystem::class.java)
        tileTypeSystem = world.getSystem(TileTypeSystem::class.java)
//        coastificationTransformer = EntityTransmuterFactory(world)
//            .remove(CoastTileTypeComponent::class.java)
//            .remove(OceanTileTypeComponent::class.java)
//            .add(CoastTileTypeComponent::class.java)
//            .build()

        hex.clientAddToWorld(world)

        camera = PerspectiveCamera(zNear = 0.1f, zFar = 100f).apply {
            setFovDeg(45f)
            setPosition(vec3.set(2f, 0f, 0f))
            lookAt(vec3.set(0f, 0f, 0f))
        }
    }

    override fun resume() {
        time = 0.0
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += delta / 1000

        View.touch() // Touch view 0 to make sure everything renders

        skyboxView.set("Skybox", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT)

        View.setViewOrder(views)

        camera?.run {
            val speed = (60 * delta / 1000 * 0.1f * (position.length().toDouble() / 5).pow(1.5)).toFloat()

            setAspect(width, height)
            translateIn(forward, speed * (inputManager.getKeyDownInt(Key.SPACE) - inputManager.getKeyDownInt(Key.LEFT_SHIFT)))
            rotateAround(vec3.zero(), right, -speed * (inputManager.getKeyDownInt(Key.W) - inputManager.getKeyDownInt(Key.S)))
            rotateAround(vec3.zero(), up, speed * (inputManager.getKeyDownInt(Key.D) - inputManager.getKeyDownInt(Key.A)))
            rotate(forward, (5 * delta / 1000).toFloat() * (inputManager.getKeyDownInt(Key.Q) - inputManager.getKeyDownInt(Key.E)))

            val minLen = 1.1f
            if (position.lengthSquared() < minLen * minLen) {
                position.normalize(minLen)
            }
            if (position.lengthSquared() > 8 * 8) {
                position.normalize(8f)
            }

            update(hexasphereView)
            update(skyboxView, true)
        }

        if (inputManager.isMouseButtonJustUp(MouseButton.RIGHT)) {
            val direction = camera!!.projectToWorld(vec2.set(
                (2 * inputManager.getMouseX() / width - 1).toFloat(),
                (2 * inputManager.getMouseY() / height - 1).toFloat()
            ), vec3)
            if (SphereRayIntersect.computeIntersection(direction, camera!!.position, Vectors.ZERO3, 1.0, vec3b) != null) {
                val bestTile = tileSystem.getClosestTile(vec3b)
                if (bestTile != -1) {
                    tileTypeSystem.setTileType(bestTile, CoastTileTypeComponent::class.java)
                }
            }
        }

        world.delta = delta.toFloat()
        world.process()

        inputManager.update()
        return ScreenAction.noop()
    }

    override fun pause() {
    }

    override fun dispose() {
        camera?.dispose()
        camera = null
        world.dispose()
    }

    override fun onKey(action: Action, key: Key, keyModifiers: KeyModifiers) {
        inputManager.handleOnKey(action, key, keyModifiers)
    }

    override fun onMouseButton(action: Action, mouseButton: MouseButton, keyModifiers: KeyModifiers) {
        inputManager.handleOnMouseButton(action, mouseButton, keyModifiers)
    }

    override fun onMouseMove(x: Double, y: Double, reset: Boolean) {
        inputManager.onMouseMove(x, y, reset)
    }
}