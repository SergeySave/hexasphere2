package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import java.nio.ByteBuffer

inline class StaticIndexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_index_buffer(handle)
    }

    companion object {
        fun new(vertices: ByteBuffer) : StaticIndexBuffer {
            val bgfxMemory = BGFX.bgfx_make_ref(vertices) ?:
                throw BGFXException("Could not create BGFX memory reference for static index buffer")
            return StaticIndexBuffer(BGFX.bgfx_create_index_buffer(bgfxMemory, BGFX.BGFX_BUFFER_NONE))
        }

        fun new32Bit(indices: ByteBuffer) : StaticIndexBuffer {
            val bgfxMemory = BGFX.bgfx_make_ref(indices) ?:
                throw BGFXException("Could not create BGFX memory reference for static index buffer")
            return StaticIndexBuffer(BGFX.bgfx_create_index_buffer(bgfxMemory, BGFX.BGFX_BUFFER_INDEX32))
        }
    }
}