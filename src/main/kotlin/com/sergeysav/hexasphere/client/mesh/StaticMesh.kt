package com.sergeysav.hexasphere.client.mesh

import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.StaticVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.VertexLayoutHandle
import org.joml.Matrix4fc
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class StaticMesh(
    private val shader: ShaderProgram,
    private val vertexLayout: VertexLayout,
    private val vertexLayoutHandle: VertexLayoutHandle = VertexLayoutHandle.new(vertexLayout),
    private val numVertices: Int,
    vertices: ByteBuffer,
    private val numIndices: Int,
    indices: ByteBuffer,
    bit32: Boolean = false,
    private val freeShader: Boolean = false,
    private val freeLayout: Boolean = false,
    private val freeLayoutHandle: Boolean = true,
    freeVertices: Boolean = false,
    freeIndices: Boolean = false
) {
    private val modelBuffer = MemoryUtil.memAllocFloat(16)
    private val vertexBuffer: StaticVertexBuffer = StaticVertexBuffer.new(vertices, vertexLayout, freeVertices)
    private val indexBuffer: StaticIndexBuffer = if (bit32) StaticIndexBuffer.new32Bit(indices, freeIndices) else StaticIndexBuffer.new(
        indices, freeIndices
    )

    fun render(encoder: Encoder, transform: Matrix4fc, id: Int = 0) = encoder.run {
        setTransform(transform.get(modelBuffer))
        setVertexBuffer(vertexBuffer, vertexLayoutHandle, numVertices)
        setIndexBuffer(indexBuffer, numIndices)
        submit(shader, id)
        Unit
    }

    fun dispose() {
        if (freeShader) shader.dispose()
        if (freeLayout) vertexLayout.dispose()
        if (freeLayoutHandle) vertexLayoutHandle.dispose()
        MemoryUtil.memFree(modelBuffer)
        vertexBuffer.dispose()
        indexBuffer.dispose()
    }
}