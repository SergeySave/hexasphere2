package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXInstanceDataBuffer
import org.lwjgl.system.MemoryStack

inline class InstanceBuffer(val handle: BGFXInstanceDataBuffer)

inline fun MemoryStack.withInstanceBuffer(numInstances: Int, instanceStride: Int, inner: (InstanceBuffer)->Unit) {
    val instanceBuffer = InstanceBuffer(BGFXInstanceDataBuffer.mallocStack(this))
    BGFX.bgfx_alloc_instance_data_buffer(instanceBuffer.handle, numInstances, instanceStride)
    inner(instanceBuffer)
}
