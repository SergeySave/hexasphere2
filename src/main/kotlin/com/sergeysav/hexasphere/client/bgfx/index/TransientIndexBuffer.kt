package com.sergeysav.hexasphere.client.bgfx.index

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXTransientIndexBuffer
import org.lwjgl.system.MemoryStack

inline class TransientIndexBuffer(val handle: BGFXTransientIndexBuffer)

inline fun MemoryStack.withTransientIndexBuffer(numIndices: Int, inner: (TransientIndexBuffer)->Unit) {
    val tvb = TransientIndexBuffer(BGFXTransientIndexBuffer.mallocStack(this))
    BGFX.bgfx_alloc_transient_index_buffer(tvb.handle, numIndices)
    inner(tvb)
}
