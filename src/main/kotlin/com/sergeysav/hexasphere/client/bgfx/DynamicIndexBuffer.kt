package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

inline class DynamicIndexBuffer(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_index_buffer(handle)
    }

    fun update(indices: ByteBuffer, startIndex: Int = 0) {
        val bgfxMemory = BGFX.bgfx_make_ref_release(indices, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) ?:
            throw BGFXException("Could not create BGFX memory reference for dynamic index buffer")
        BGFX.bgfx_update_dynamic_index_buffer(handle, startIndex, bgfxMemory)
    }

    companion object {
        fun new(numIndices: Int) : DynamicIndexBuffer {
            return DynamicIndexBuffer(BGFX.bgfx_create_dynamic_index_buffer(numIndices, BGFX.BGFX_BUFFER_NONE))
        }

        fun new32Bit(numIndices: Int) : DynamicIndexBuffer {
            return DynamicIndexBuffer(BGFX.bgfx_create_dynamic_index_buffer(numIndices, BGFX.BGFX_BUFFER_INDEX32))
        }
    }
}