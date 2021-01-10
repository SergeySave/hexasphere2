package com.sergeysav.hexasphere.client.screen

import com.artemis.World
import com.sergeysav.hexasphere.client.IOUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.Framebuffer
import com.sergeysav.hexasphere.client.bgfx.OrthographicCamera
import com.sergeysav.hexasphere.client.bgfx.PerspectiveCamera
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.client.bgfx.ViewArray
import com.sergeysav.hexasphere.client.bgfx.set
import com.sergeysav.hexasphere.client.font.SDFFont
import com.sergeysav.hexasphere.client.game.skybox.SkyboxRenderSystem
import com.sergeysav.hexasphere.client.game.tile.HexasphereRenderSystem
import com.sergeysav.hexasphere.client.game.tile.feature.CityRenderSystem
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
import com.sergeysav.hexasphere.common.game.tile.type.setType
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.withSubdivisionLevel
import com.sergeysav.hexasphere.common.setColor
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import java.util.Random
import kotlin.math.pow

class HSScreen : Screen {

    private lateinit var world: World
    private lateinit var tileSystem: TileSystem
    private lateinit var tileTypeSystem: TileTypeSystem
    private lateinit var font: SDFFont
    private var camera: PerspectiveCamera? = null
    private var fontCamera: OrthographicCamera? = null
    private var time: Double = 0.0
    private val inputManager = InputManager()
    private val vec2 = Vector2f()
    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val vec4 = Vector4f()
    private val vec4b = Vector4f()
    private val hexasphereView = View(0)
    private val skyboxView = View(1)
    private val featuresView = View(2)
    private val uiView = View(3)
    private val views: ViewArray = intArrayOf(hexasphereView.id, featuresView.id, skyboxView.id, uiView.id)
    private val random = Random()

    override fun create() {
        val hex = Hexasphere.withSubdivisionLevel(3)

        world = Game.create {
            with(
                HexasphereRenderSystem(hex.pentagons, hex.hexagons, hexasphereView,
                ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes"))
            )
            with(SkyboxRenderSystem("/skybox/nasa2k.ktx", skyboxView))
            with(CityRenderSystem(featuresView))
        }
        tileSystem = world.getSystem(TileSystem::class.java)
        tileTypeSystem = world.getSystem(TileTypeSystem::class.java)

        hex.clientAddToWorld(world)

        camera = PerspectiveCamera(zNear = 0.1f, zFar = 100f).apply {
            setFovDeg(45f)
            setPosition(vec3a.set(2f, 0f, 0f))
            lookAt(vec3a.set(0f, 0f, 0f))
        }

        font = SDFFont(IOUtil.loadResource("/font/OpenSans/OpenSans-Regular.ttf"), pixelHeight = 60f, bitmapSize = 512)

        fontCamera = OrthographicCamera(0.0, 0.0,  0.0, 0.0, -1f, 1f)
    }

    override fun resume() {
        time = 0.0
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += delta / 1000

        View.touch() // Touch view 0 to make sure everything renders
        skyboxView.set("Skybox", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT)
        featuresView.set("Features", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT)
        uiView.set("UI", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT)
        View.setViewOrder(views)

        camera?.run {
            val speed = (60 * delta / 1000 * 0.1f * (position.length().toDouble() / 5).pow(1.5)).toFloat()

            setAspect(width, height)
            translateIn(forward, speed * (inputManager.getKeyDownInt(Key.SPACE) - inputManager.getKeyDownInt(Key.LEFT_SHIFT)))
            rotateAround(vec3a.zero(), right, -speed * (inputManager.getKeyDownInt(Key.W) - inputManager.getKeyDownInt(Key.S)))
            rotateAround(vec3a.zero(), up, speed * (inputManager.getKeyDownInt(Key.D) - inputManager.getKeyDownInt(Key.A)))
            rotate(forward, (5 * delta / 1000).toFloat() * (inputManager.getKeyDownInt(Key.Q) - inputManager.getKeyDownInt(Key.E)))

            translateIn(forward, 3f * speed * inputManager.getScrollY().toFloat())

            if (inputManager.isMouseButtonDown(MouseButton.LEFT) && !inputManager.isMouseButtonJustDown(MouseButton.LEFT)) {
                up.mul(inputManager.getMouseDX().toFloat(), vec3a)
                right.mul(inputManager.getMouseDY().toFloat(), vec3b)
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

            update(hexasphereView)
            update(skyboxView, true)
            update(featuresView)
        }

        if (inputManager.isMouseButtonJustUp(MouseButton.RIGHT)) {
            val direction = camera!!.projectToWorld(vec2.set(
                (2 * inputManager.getMouseX() / width - 1).toFloat(),
                (2 * inputManager.getMouseY() / height - 1).toFloat()
            ), vec3a)
            if (SphereRayIntersect.computeIntersection(direction, camera!!.position, Vectors.ZERO3f, 1.0, vec3b) != null) {
                val bestTile = tileSystem.getClosestTile(vec3b)
                if (bestTile != -1) {
                    tileTypeSystem.setType<CoastTileTypeComponent>(bestTile)
                }
            }
        }

        world.delta = delta.toFloat()
        world.process()

        fontCamera?.run {
            this.width = width.toDouble()
            this.height = height.toDouble()
            update(uiView)
        }

        Encoder.with {

            val scale = 1 / (font.computeWidth("abcdefghijklmn opqrstuvwxyz") / (width - 100)).toFloat()
            val w = font.computeWidth("abcdefghijklmn opqrstuvwxyz") * scale
            val h = font.computeHeight("abcdefghijklmn opqrstuvwxyz") * scale
            val x = (width - w) / 2.0
            val y = (height - h) / 2.0
            font.render(this, uiView, x, y, "abcdefghijklmn opqrstuvwxyz",
                color = vec4.setColor(r=0xFF, g=0xFF, b=0xFF),
                drawScale = scale,
                extraThickness = 0f,
                outlineColor = vec4b.setColor(r=0x00, g=0x00, b=0x00),
                outlineThickness = 0f
            )
        }

        inputManager.update()
        return ScreenAction.noop()
    }

    override fun pause() {
    }

    override fun dispose() {
        camera?.dispose()
        camera = null
        world.dispose()
        fontCamera?.dispose()
        fontCamera = null
        font.dispose()
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

    override fun onScroll(x: Double, y: Double) {
        inputManager.onScroll(x, y)
    }
}