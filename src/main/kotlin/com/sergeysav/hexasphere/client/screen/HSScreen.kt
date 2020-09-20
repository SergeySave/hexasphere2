package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.StaticVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import org.joml.Matrix4f
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class HSScreen : Screen {

    private var cubes: ShaderProgram? = null
    private var vertexLayout: VertexLayout? = null
    private var vertices: ByteBuffer? = null
    private var vertexBuffer: StaticVertexBuffer? = null
    private var indices: ByteBuffer? = null
    private var indexBuffer: StaticIndexBuffer? = null
    private val view = Matrix4f()
    private val proj = Matrix4f()
    private val model = Matrix4f()
    private var viewBuf: FloatBuffer? = null
    private var projBuf: FloatBuffer? = null
    private var modelBuf: FloatBuffer? = null
    private var time: Double = 0.0

    override fun create() {
        cubes = ShaderProgram.loadFromFiles("cube/vs_cubes", "cube/fs_cubes")

        vertexLayout = VertexLayout.new(
                VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
                VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, true)
        )
        vertices = MemoryUtil.memAlloc(8 * ((3 + 4) * 4)).apply {
            putFloat(-1f).putFloat(+1f).putFloat(+1f).putInt(0xff000000.toInt())
            putFloat(+1f).putFloat(+1f).putFloat(+1f).putInt(0xff0000ff.toInt())
            putFloat(-1f).putFloat(-1f).putFloat(+1f).putInt(0xff00ff00.toInt())
            putFloat(+1f).putFloat(-1f).putFloat(+1f).putInt(0xff00ffff.toInt())
            putFloat(-1f).putFloat(+1f).putFloat(-1f).putInt(0xffff0000.toInt())
            putFloat(+1f).putFloat(+1f).putFloat(-1f).putInt(0xffff00ff.toInt())
            putFloat(-1f).putFloat(-1f).putFloat(-1f).putInt(0xffffff00.toInt())
            putFloat(+1f).putFloat(-1f).putFloat(-1f).putInt(0xffffffff.toInt())
            flip()
        }
        vertexBuffer = StaticVertexBuffer.new(vertices!!, vertexLayout!!)

        indices = MemoryUtil.memAlloc(6 * 2 * 3 * 2).apply {
            putShort(0).putShort(1).putShort(2) // 0
            putShort(1).putShort(3).putShort(2)
            putShort(4).putShort(6).putShort(5) // 2
            putShort(5).putShort(6).putShort(7)
            putShort(0).putShort(2).putShort(4) // 4
            putShort(4).putShort(2).putShort(6)
            putShort(1).putShort(5).putShort(3) // 6
            putShort(5).putShort(7).putShort(3)
            putShort(0).putShort(4).putShort(1) // 8
            putShort(4).putShort(5).putShort(1)
            putShort(2).putShort(3).putShort(6) // 10
            putShort(6).putShort(3).putShort(7)
            flip()
        }
        indexBuffer = StaticIndexBuffer.new(indices!!)

        viewBuf = MemoryUtil.memAllocFloat(16)
        projBuf = MemoryUtil.memAllocFloat(16)
        modelBuf = MemoryUtil.memAllocFloat(16)
    }

    override fun resume() {
        time = 0.0
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        time += delta / 1000
        val cubes = cubes!!
        val vertexBuffer = vertexBuffer!!
        val indexBuffer = indexBuffer!!
        val vertexLayout = vertexLayout!!

        BGFX.bgfx_dbg_text_printf(0, 1, 0x4f, "bgfx/examples/01-cubes")
        BGFX.bgfx_dbg_text_printf(0, 2, 0x6f, "Description: Rendering simple static mesh.")
        BGFX.bgfx_dbg_text_printf(0, 3, 0x0f, String.format("Frame: %7.3f[ms]", delta))

        view.setLookAt(0.0f, 0.0f, -35.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
        proj.setPerspective(Math.toRadians(60.0).toFloat(), width / height.toFloat(), 0.1f, 100f, BGFXUtil.zZeroToOne)

        BGFX.bgfx_set_view_transform(0, view.get(viewBuf), proj.get(projBuf))

        Encoder.with {
            for (yy in 0..10) {
                for (xx in 0..10) {
                    setTransform(model.translation(
                            -15.0f + xx * 3.0f,
                            -15.0f + yy * 3.0f,
                            0.0f)
                            .rotateXYZ(
                                    (time + xx * 0.21).toFloat(),
                                    (time + yy * 0.37).toFloat(),
                                    0.0f).get(modelBuf))
                    setVertexBuffer(vertexBuffer, vertexLayout, 8)
                    setIndexBuffer(indexBuffer, 36)
                    setState(BGFX.BGFX_STATE_DEFAULT, 0)
                    submit(cubes)
                }
            }
        }

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
        MemoryUtil.memFree(viewBuf)
        MemoryUtil.memFree(projBuf)
        MemoryUtil.memFree(modelBuf)
    }
}