package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

inline class DynamicVertexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_vertex_buffer(handle)
    }

    fun update(vertices: ByteBuffer, autoFree: Boolean = false, startVertex: Int = 0) {
        val bgfxMemory = (if (autoFree) BGFX.bgfx_make_ref_release(vertices, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) else BGFX.bgfx_make_ref(vertices)) ?:
            throw BGFXException("Could not create BGFX memory reference for dynamic vertex buffer")
        BGFX.bgfx_update_dynamic_vertex_buffer(handle, startVertex, bgfxMemory)
    }

    companion object {
        fun new(numVertices: Int, layout: VertexLayout) : DynamicVertexBuffer {
            return DynamicVertexBuffer(BGFX.bgfx_create_dynamic_vertex_buffer(numVertices, layout.layout, BGFX.BGFX_BUFFER_NONE))
        }
    }
}