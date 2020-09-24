package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.PerspectiveCamera
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.Texture
import com.sergeysav.hexasphere.client.bgfx.Uniform
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.camera.Camera3d
import com.sergeysav.hexasphere.client.input.InputManager
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyAction
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.ui.ManagedViewFramebuffer
import com.sergeysav.hexasphere.client.ui.StaticMesh
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.icosahedron.Icosahedron
import com.sergeysav.hexasphere.common.icosahedron.subdivide
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.math.pow
import kotlin.random.Random

class HSScreen : Screen {

    private val model = Matrix4f()
    private var hexasphereMesh: StaticMesh? = null

    private var skyboxTexture: Texture? = null
    private var texCubeSampler: Uniform? = null
    private var skyboxMesh: StaticMesh? = null

    private var camera: Camera3d? = null
    private var time: Double = 0.0

    private val hex = Hexasphere.fromDualOf(Icosahedron.subdivide(10))
    private val inputManager = InputManager()
    private val vec3 = Vector3f()
    private val buffer = ManagedViewFramebuffer("Skybox", useDefaultFramebuffer = true)

    override fun create() {
        val hexVerts = MemoryUtil.memAlloc((hex.pentagons * 5 + hex.hexagons * 6) * ((3 + 3) * Float.SIZE_BYTES + (4) * Byte.SIZE_BYTES))
        val hexInds = MemoryUtil.memAlloc((hex.pentagons * 3 + hex.hexagons * 4) * 3 * Int.SIZE_BYTES)
        for (tile in hex.tiles) {
            val color: Int = 0xff000000.toInt() or Random.nextInt()
            val v1 = hexVerts.position() / ((3 + 3 + 1) * 4)
            for (vert in tile.vertices) {
                hexVerts.putFloat(vert.x().toFloat())
                        .putFloat(vert.y().toFloat())
                        .putFloat(vert.z().toFloat())
                        .putFloat(tile.centroid.x().toFloat())
                        .putFloat(tile.centroid.y().toFloat())
                        .putFloat(tile.centroid.z().toFloat())
                        .putInt(color)
            }
            for (i in 2 until tile.vertices.size) {
                hexInds.putInt(v1).putInt(v1 + i - 1).putInt(v1 + i)
            }
        }
        hexasphereMesh = StaticMesh(
            ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes"),
            VertexLayout.new(
                VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
                VertexAttribute(VertexAttribute.Attribute.COLOR1, 3, VertexAttribute.Type.FLOAT, true),
                VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, true),
            ),
            numVertices = hex.pentagons * 5 + hex.hexagons * 6, vertices = hexVerts.flip(),
            numIndices = (hex.pentagons * 3 + hex.hexagons * 4) * 3, indices = hexInds.flip(), bit32 = true,
            freeShader = true, freeLayout = true, freeLayoutHandle = true, freeVertices = true, freeIndices = true
        )

        skyboxTexture = Texture.newCubeMap("/skybox/nasa2k.ktx")
        texCubeSampler = Uniform.new("s_texCube", Uniform.Type.SAMPLER)
        skyboxMesh = StaticMesh(
            ShaderProgram.loadFromFiles("skybox/vs", "skybox/fs"),
            VertexLayout.new(VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT)),
            numVertices = 8, vertices = MemoryUtil.memAlloc(8 * ((3) * Float.SIZE_BYTES)).apply {
                putFloat(-1f).putFloat(+1f).putFloat(+1f)
                putFloat(+1f).putFloat(+1f).putFloat(+1f)
                putFloat(-1f).putFloat(-1f).putFloat(+1f)
                putFloat(+1f).putFloat(-1f).putFloat(+1f)
                putFloat(-1f).putFloat(+1f).putFloat(-1f)
                putFloat(+1f).putFloat(+1f).putFloat(-1f)
                putFloat(-1f).putFloat(-1f).putFloat(-1f)
                putFloat(+1f).putFloat(-1f).putFloat(-1f)
            }.flip(), numIndices = 3 * 12, indices = MemoryUtil.memAlloc(3 * 12 * 2).apply {
                putShort(0).putShort(1).putShort(2)
                putShort(1).putShort(3).putShort(2)
                putShort(4).putShort(6).putShort(5)
                putShort(5).putShort(6).putShort(7)
                putShort(0).putShort(2).putShort(4)
                putShort(4).putShort(2).putShort(6)
                putShort(1).putShort(5).putShort(3)
                putShort(5).putShort(7).putShort(3)
                putShort(0).putShort(4).putShort(1)
                putShort(4).putShort(5).putShort(1)
                putShort(2).putShort(3).putShort(6)
                putShort(6).putShort(3).putShort(7)
            }.flip(), bit32 = false, freeShader = true, freeLayout = true, freeLayoutHandle = true,
            freeVertices = true, freeIndices = true
        )

        camera = PerspectiveCamera(zNear = 0.1f, zFar = 100f).apply {
            setFovDeg(45f)
            setPosition(vec3.set(2f, 0f, 0f))
            lookAt(vec3.set(0f, 0f, 0f))
        }
    }

    override fun resume() {
        time = 0.0
        println("Vertices:  ${hex.pentagons * 5 + hex.hexagons * 6}")
        println("Tiles:     ${hex.hexagons + hex.pentagons}")
        println("Triangles: ${hex.pentagons * 3 + hex.hexagons * 4}")
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += (delta / 1000) * 0.1

        BGFX.bgfx_touch(0)
        buffer.setView(width, height, 1)
        MemoryStack.stackPush().use { stack ->
            BGFX.bgfx_set_view_order(0, 2, stack.shorts(0, 1))
        }

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

            update(0)
            update(1, true)
        }

        Encoder.with { // This should be rendered last in the background
            setState(Encoder.SKYBOX)
            setTexture(0, texCubeSampler!!, skyboxTexture!!)
            skyboxMesh!!.render(this, model.identity().scale(100_000f), 1)
        }

        Encoder.with {
            setState(Encoder.DEFAULT)
            hexasphereMesh!!.render(this, model.identity(), 0)
        }

        inputManager.update()
        return ScreenAction.noop()
    }

    override fun pause() {
    }

    override fun dispose() {
        hexasphereMesh?.dispose()
        hexasphereMesh = null
        camera?.dispose()
        camera = null
        skyboxTexture?.dispose()
        skyboxTexture = null
        texCubeSampler?.dispose()
        texCubeSampler = null
        skyboxMesh?.dispose()
        skyboxMesh = null
        buffer.dispose()
    }

    override fun onKey(keyAction: KeyAction, key: Key, keyModifiers: KeyModifiers) {
        inputManager.handleOnKey(keyAction, key, keyModifiers)
    }
}