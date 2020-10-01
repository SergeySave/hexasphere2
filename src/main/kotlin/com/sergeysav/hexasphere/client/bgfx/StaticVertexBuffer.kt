package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

inline class StaticVertexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_vertex_buffer(handle)
    }

    companion object {
        fun new(vertices: ByteBuffer, layout: VertexLayout, autoFree: Boolean = false) : StaticVertexBuffer {
            val bgfxMemory = (if (autoFree) BGFX.bgfx_make_ref_release(vertices, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) else BGFX.bgfx_make_ref(vertices)) ?:
                throw BGFXException("Could not create BGFX memory reference for static vertex buffer")
            return StaticVertexBuffer(BGFX.bgfx_create_vertex_buffer(bgfxMemory, layout.layout, BGFX.BGFX_BUFFER_NONE))
        }
    }
}