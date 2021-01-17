package com.sergeysav.hexasphere.client.bgfx.vertex

import org.lwjgl.bgfx.BGFX

inline class VertexLayoutHandle(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_vertex_layout(handle)
    }

    companion object {
        fun new(vertexLayout: VertexLayout): VertexLayoutHandle {
            try {
                return VertexLayoutHandle(BGFX.bgfx_create_vertex_layout(vertexLayout.layout))
            } catch (ex : Throwable) {
                throw ex
            }
        }
    }
}