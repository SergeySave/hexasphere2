package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXVertexLayout

class VertexLayout(val handle: Short, val layout: BGFXVertexLayout) {

    fun dispose() {
        BGFX.bgfx_destroy_vertex_layout(handle)
        layout.free()
    }

    companion object {
        fun new(vararg attributes: VertexAttribute): VertexLayout {
            val layout = BGFXVertexLayout.calloc()

            try {
                BGFX.bgfx_vertex_layout_begin(layout, BGFXUtil.renderer)

                for (attribute in attributes) {
                    BGFX.bgfx_vertex_layout_add(layout, attribute.attribute.bgfxValue, attribute.num, attribute.type.bgfxValue, attribute.normalized, attribute.packAsInt)
                }

                BGFX.bgfx_vertex_layout_end(layout)

                return VertexLayout(BGFX.bgfx_create_vertex_layout(layout), layout)
            } catch (ex : Throwable) {
                throw ex
            }
        }
    }
}