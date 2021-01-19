package com.sergeysav.hexasphere.client.bgfx.encoder

import com.sergeysav.hexasphere.client.bgfx.index.DynamicIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.index.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.index.TransientIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.instance.InstanceBuffer
import com.sergeysav.hexasphere.client.bgfx.shader.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.texture.Texture
import com.sergeysav.hexasphere.client.bgfx.uniform.Uniform
import com.sergeysav.hexasphere.client.bgfx.vertex.DynamicVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.StaticVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.TransientVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayoutHandle
import com.sergeysav.hexasphere.client.bgfx.view.View
import org.lwjgl.bgfx.BGFX
import java.nio.ByteBuffer
import java.nio.FloatBuffer

inline class Encoder(val handle: Long) {

    fun setTransform(buffer: FloatBuffer) {
        BGFX.bgfx_encoder_set_transform(handle, buffer)
    }

    fun setTransform(buffer: ByteBuffer) {
        BGFX.bgfx_encoder_set_transform(handle, buffer)
    }

    fun setVertexBuffer(vertexBuffer: StaticVertexBuffer, vertexLayout: VertexLayoutHandle, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setVertexBuffer(vertexBuffer: DynamicVertexBuffer, vertexLayout: VertexLayoutHandle, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_dynamic_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setVertexBuffer(vertexBuffer: TransientVertexBuffer, vertexLayout: VertexLayoutHandle, numVertices: Int, stream: Int = 0, offset: Int = 0) {
        BGFX.bgfx_encoder_set_transient_vertex_buffer(handle, stream, vertexBuffer.handle, offset, numVertices, vertexLayout.handle)
    }

    fun setIndexBuffer(indexBuffer: StaticIndexBuffer, numIndices: Int, offset: Int = 0) {
        BGFX.bgfx_encoder_set_index_buffer(handle, indexBuffer.handle, offset, numIndices)
    }

    fun setIndexBuffer(indexBuffer: DynamicIndexBuffer, numIndices: Int, offset: Int = 0) {
        BGFX.bgfx_encoder_set_dynamic_index_buffer(handle, indexBuffer.handle, offset, numIndices)
    }

    fun setIndexBuffer(indexBuffer: TransientIndexBuffer, numIndices: Int, offset: Int = 0) {
        BGFX.bgfx_encoder_set_transient_index_buffer(handle, indexBuffer.handle, offset, numIndices)
    }

    fun setState(state: Long, rgba: Int = 0) {
        BGFX.bgfx_encoder_set_state(handle, state, rgba)
    }

    fun setTexture(textureUnit: Int, sampler: Uniform, texture: Texture, flags: Int = -1) {
        BGFX.bgfx_encoder_set_texture(handle, textureUnit, sampler.handle, texture.handle, flags)
    }

    fun setUniform(uniform: Uniform, buffer: FloatBuffer, count: Int = 1) {
        BGFX.bgfx_encoder_set_uniform(handle, uniform.handle, buffer, count)
    }

    fun setInstanceBuffer(instanceBuffer: InstanceBuffer, num: Int, start: Int = 0) {
        BGFX.bgfx_encoder_set_instance_data_buffer(handle, instanceBuffer.handle, start, num)
    }

    fun submit(program: ShaderProgram, id: Int = 0, depth: Int = 0, preserveState: Boolean = false) = submit(program, View(id), depth, preserveState)

    fun submit(program: ShaderProgram, view: View = View(0), depth: Int = 0, preserveState: Boolean = false) {
        BGFX.bgfx_encoder_submit(handle, view.id, program.handle, depth, preserveState)
    }

    companion object {

        const val DEFAULT = BGFX.BGFX_STATE_WRITE_MASK or
                BGFX.BGFX_STATE_DEPTH_TEST_LESS or
                BGFX.BGFX_STATE_CULL_CCW or
                BGFX.BGFX_STATE_MSAA
        const val DEFAULT_CW = BGFX.BGFX_STATE_WRITE_MASK or
                BGFX.BGFX_STATE_DEPTH_TEST_LESS or
                BGFX.BGFX_STATE_CULL_CW or
                BGFX.BGFX_STATE_MSAA
        const val SKYBOX = BGFX.BGFX_STATE_WRITE_RGB or BGFX.BGFX_STATE_WRITE_A or
                BGFX.BGFX_STATE_DEPTH_TEST_LEQUAL or
                BGFX.BGFX_STATE_CULL_CW or
                BGFX.BGFX_STATE_MSAA
        const val DEBUG = BGFX.BGFX_STATE_WRITE_MASK or
                BGFX.BGFX_STATE_DEPTH_TEST_LESS
        val TEXT = BGFX.BGFX_STATE_WRITE_RGB or BGFX.BGFX_STATE_WRITE_A or
                BGFX.BGFX_STATE_MSAA or
                BGFX.BGFX_STATE_CULL_CCW or
                BGFX.BGFX_STATE_BLEND_ALPHA

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