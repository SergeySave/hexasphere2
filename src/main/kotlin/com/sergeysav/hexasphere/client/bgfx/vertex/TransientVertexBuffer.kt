package com.sergeysav.hexasphere.client.bgfx.vertex

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXTransientVertexBuffer
import org.lwjgl.system.MemoryStack

inline class TransientVertexBuffer(val handle: BGFXTransientVertexBuffer)

inline fun MemoryStack.withTransientVertexBuffer(numVertices: Int, vertexLayout: VertexLayout, inner: (TransientVertexBuffer)->Unit) {
    val tvb = TransientVertexBuffer(BGFXTransientVertexBuffer.mallocStack(this))
    BGFX.bgfx_alloc_transient_vertex_buffer(tvb.handle, numVertices, vertexLayout.layout)
    inner(tvb)
}
