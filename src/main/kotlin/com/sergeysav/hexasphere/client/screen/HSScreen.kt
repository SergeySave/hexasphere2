package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.PerspectiveCamera
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.StaticVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.camera.Camera3d
import com.sergeysav.hexasphere.client.input.InputManager
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyAction
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import com.sergeysav.hexasphere.common.hexasphere.HexasphereTile
import com.sergeysav.hexasphere.common.icosahedron.Icosahedron
import com.sergeysav.hexasphere.common.icosahedron.subdivide
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3f
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.math.pow
import kotlin.random.Random


class HSScreen : Screen {

    private var cubes: ShaderProgram? = null
    private var vertexLayout: VertexLayout? = null
    private var vertices: ByteBuffer? = null
    private var vertexBuffer: StaticVertexBuffer? = null
    private var indices: ByteBuffer? = null
    private var indexBuffer: StaticIndexBuffer? = null
    private val model = Matrix4f()
    private var modelBuf: FloatBuffer? = null
    private var time: Double = 0.0
    private val hex = Hexasphere.fromDualOf(Icosahedron.subdivide(10))
    private var camera: Camera3d? = null
    private val inputManager = InputManager()
    private val vec3 = Vector3f()

    override fun create() {
        cubes = ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes")

        vertexLayout = VertexLayout.new(
                VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
                VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, true)
        )
        val vertices = MemoryUtil.memAlloc((12 * 5 + hex.hexagons * 6) * ((3 + 4) * 4))
        val indices = MemoryUtil.memAlloc((12 * 3 + hex.hexagons * 4) * 3*2)
        for (tile in hex.tiles) {
            val color: Int = 0xff000000.toInt() or Random.nextInt()
            val v1 = vertices.position() / (3 * 4 + 1 * 4)
            for (vert in tile.vertices) {
                vertices.putFloat(vert.x().toFloat())
                        .putFloat(vert.y().toFloat())
                        .putFloat(vert.z().toFloat())
                        .putInt(color)
            }
            for (i in 2 until tile.vertices.size) {
                indices.putShort(v1.toShort()).putShort((v1 + i - 1).toShort()).putShort((v1 + i).toShort())
            }
        }
        this.vertices = vertices.flip()
        this.indices = indices.flip()
        vertexBuffer = StaticVertexBuffer.new(vertices, vertexLayout!!)
        indexBuffer = StaticIndexBuffer.new(indices)

        modelBuf = MemoryUtil.memAllocFloat(16)

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
        time += (delta / 1000) * 0.1
        val cubes = cubes!!
        val vertexBuffer = vertexBuffer!!
        val indexBuffer = indexBuffer!!
        val vertexLayout = vertexLayout!!

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

            update()
        }

        Encoder.with {
            setTransform(model.identity().get(modelBuf))
            setVertexBuffer(vertexBuffer, vertexLayout, 12 * 5 + hex.hexagons * 6)
            setIndexBuffer(indexBuffer, (12 * 3 + hex.hexagons * 4) * 3)
            setState(BGFX.BGFX_STATE_DEFAULT, 0)
            submit(cubes)
        }

        inputManager.update()

        return ScreenAction.noop()
    }

    override fun pause() {
    }

    override fun dispose() {
        cubes?.dispose()
        cubes = null
        vertexLayout?.dispose()
        vertexLayout = null
        MemoryUtil.memFree(vertices)
        vertexBuffer?.dispose()
        vertexBuffer = null
        MemoryUtil.memFree(indices)
        indexBuffer?.dispose()
        indexBuffer = null
        camera?.dispose()
        camera = null
        MemoryUtil.memFree(modelBuf)
    }

    override fun onKey(keyAction: KeyAction, key: Key, keyModifiers: KeyModifiers) {
        inputManager.handleOnKey(keyAction, key, keyModifiers)
    }
}