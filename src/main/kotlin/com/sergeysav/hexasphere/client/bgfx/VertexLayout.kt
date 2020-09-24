package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXVertexLayout

inline class VertexLayout(val layout: BGFXVertexLayout) {

    fun dispose() {
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

                return VertexLayout(layout)
            } catch (ex : Throwable) {
                throw ex
            }
        }
    }
}