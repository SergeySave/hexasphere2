package com.sergeysav.hexasphere.client.bgfx

import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class OrthographicCamera(
        var x: Double,
        var y: Double,
        var width: Double,
        var height: Double,
        var zNear: Float = 0f,
        var zFar: Float = 0f
)  {
    val projMat: Matrix4fc
        get() = proj

    private val proj = Matrix4f()
    private val view = Matrix4f()
    private var viewBuf: FloatBuffer = MemoryUtil.memAllocFloat(16)
    private var projBuf: FloatBuffer = MemoryUtil.memAllocFloat(16)

    // Camera Lifecycle
    fun update(viewId: View) {
        proj.setOrtho(x.toFloat(), (x + width).toFloat(), y.toFloat(), (y + height).toFloat(), zNear, zFar, BGFXUtil.zZeroToOne)
        BGFX.bgfx_set_view_transform(viewId.id, view.get(viewBuf), proj.get(projBuf))
    }

    fun dispose() {
        MemoryUtil.memFree(viewBuf)
        MemoryUtil.memFree(projBuf)
    }
}
