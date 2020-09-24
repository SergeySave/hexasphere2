package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import java.nio.ByteBuffer

inline class StaticVertexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_vertex_buffer(handle)
    }

    companion object {
        fun new(vertices: ByteBuffer, layout: VertexLayout) : StaticVertexBuffer {
            val bgfxMemory = BGFX.bgfx_make_ref(vertices) ?:
                throw BGFXException("Could not create BGFX memory reference for static vertex buffer")
            return StaticVertexBuffer(BGFX.bgfx_create_vertex_buffer(bgfxMemory, layout.layout, BGFX.BGFX_BUFFER_NONE))
        }
    }
}