package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import java.nio.FloatBuffer

inline class Encoder(val handle: Long) {

    fun setTransform(buffer: FloatBuffer) {
        BGFX.bgfx_encoder_set_transform(handle, buffer)
    }

    fun setVertexBuffer(vertexBuffer: StaticVertexBuffer, vertexLayout: VertexLayoutHandle, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setVertexBuffer(vertexBuffer: TransientVertexBuffer, vertexLayout: VertexLayoutHandle, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_transient_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setIndexBuffer(indexBuffer: StaticIndexBuffer, numIndices: Int, offset: Int = 0) {
        BGFX.bgfx_encoder_set_index_buffer(handle, indexBuffer.handle, offset, numIndices)
    }

    fun setState(state: Long, rgba: Int = 0) {
        BGFX.bgfx_encoder_set_state(handle, state, rgba)
    }

    fun setTexture(textureUnit: Int, sampler: Uniform, texture: Texture, flags: Int = BGFX.BGFX_SAMPLER_NONE) {
        BGFX.bgfx_encoder_set_texture(handle, textureUnit, sampler.handle, texture.handle, flags)
    }

    fun setUniform(uniform: Uniform, buffer: FloatBuffer, count: Int = -1) {
        BGFX.bgfx_encoder_set_uniform(handle, uniform.handle, buffer, count)
    }

    fun submit(program: ShaderProgram, id: Int = 0, depth: Int = 0, preserveState: Boolean = false) {
        BGFX.bgfx_encoder_submit(handle, id, program.handle, depth, preserveState)
    }

    companion object {

        const val DEFAULT = BGFX.BGFX_STATE_WRITE_MASK or
                BGFX.BGFX_STATE_DEPTH_TEST_LESS or
                BGFX.BGFX_STATE_CULL_CW or
                BGFX.BGFX_STATE_MSAA
        const val SKYBOX = BGFX.BGFX_STATE_WRITE_RGB or BGFX.BGFX_STATE_WRITE_A or
                BGFX.BGFX_STATE_DEPTH_TEST_LEQUAL or
                BGFX.BGFX_STATE_CULL_CW or
                BGFX.BGFX_STATE_MSAA

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