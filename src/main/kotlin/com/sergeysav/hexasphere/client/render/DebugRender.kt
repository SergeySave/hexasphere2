package com.sergeysav.hexasphere.client.render

import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.shader.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayoutHandle
import com.sergeysav.hexasphere.client.bgfx.vertex.withTransientVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.view.View
import org.joml.Vector3fc
import org.lwjgl.system.MemoryStack

object DebugRender {

    private val shader = ShaderProgram.loadFromFiles("debug/vs", "debug/fs")
    private val vertexLayout: VertexLayout =
        VertexLayout.new(
            VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
            VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, normalized = true)
        )

    private val vertexLayoutHandle: VertexLayoutHandle = VertexLayoutHandle.new(vertexLayout)

    fun fillQuad(encoder: Encoder, view: View, p1: Vector3fc, p2: Vector3fc, p3: Vector3fc, p4: Vector3fc, color: Int, depth: Int = 0) {
        MemoryStack.stackPush().use { stack ->
            stack.withTransientVertexBuffer(6, vertexLayout) {
                it.handle.data()
                    .putFloat(p1.x()).putFloat(p1.y()).putFloat(p1.z()).putInt(color)
                    .putFloat(p2.x()).putFloat(p2.y()).putFloat(p2.z()).putInt(color)
                    .putFloat(p3.x()).putFloat(p3.y()).putFloat(p3.z()).putInt(color)
                    .putFloat(p3.x()).putFloat(p3.y()).putFloat(p3.z()).putInt(color)
                    .putFloat(p4.x()).putFloat(p4.y()).putFloat(p4.z()).putInt(color)
                    .putFloat(p1.x()).putFloat(p1.y()).putFloat(p1.z()).putInt(color)
                    .flip()
                encoder.apply {
                    setVertexBuffer(it, vertexLayoutHandle, 6)
                    submit(shader, view.id, depth = depth)
                }
            }
        }
    }
}