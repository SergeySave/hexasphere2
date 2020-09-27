package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

inline class StaticIndexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_index_buffer(handle)
    }

    companion object {
        fun new(indices: ByteBuffer, autoFree: Boolean = false) : StaticIndexBuffer {
            val bgfxMemory = (if (autoFree) BGFX.bgfx_make_ref_release(indices, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) else BGFX.bgfx_make_ref(indices)) ?:
                throw BGFXException("Could not create BGFX memory reference for static index buffer")
            return StaticIndexBuffer(BGFX.bgfx_create_index_buffer(bgfxMemory, BGFX.BGFX_BUFFER_NONE))
        }

        fun new32Bit(indices: ByteBuffer, autoFree: Boolean = false) : StaticIndexBuffer {
            val bgfxMemory = (if (autoFree) BGFX.bgfx_make_ref_release(indices, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) else BGFX.bgfx_make_ref(indices)) ?:
                throw BGFXException("Could not create BGFX memory reference for static index buffer")
            return StaticIndexBuffer(BGFX.bgfx_create_index_buffer(bgfxMemory, BGFX.BGFX_BUFFER_INDEX32))
        }
    }
}