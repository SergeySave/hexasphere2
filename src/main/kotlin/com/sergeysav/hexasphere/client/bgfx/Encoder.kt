package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import java.nio.FloatBuffer

inline class Encoder(val handle: Long) {

    fun setTransform(buffer: FloatBuffer) {
        BGFX.bgfx_encoder_set_transform(handle, buffer)
    }

    fun setVertexBuffer(vertexBuffer: StaticVertexBuffer, vertexLayout: VertexLayout, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setIndexBuffer(indexBuffer: StaticIndexBuffer, numIndices: Int, offset: Int = 0) {
        BGFX.bgfx_encoder_set_index_buffer(handle, indexBuffer.handle, offset, numIndices)
    }

    fun setState(state: Long, rgba: Int) {
        BGFX.bgfx_encoder_set_state(handle, state, rgba)
    }

    fun submit(program: ShaderProgram, id: Int = 0, depth: Int = 0, preserveState: Boolean = false) {
        BGFX.bgfx_encoder_submit(handle, id, program.handle, depth, preserveState)
    }

    companion object {
        inline fun <T> with(forThread: Boolean = false, inner: Encoder.()->T): T {
            val encoder = Encoder(BGFX.bgfx_encoder_begin(forThread))
            try {
                return inner(encoder)
            } finally {
                BGFX.bgfx_encoder_end(encoder.handle)
            }
        }
    }
}