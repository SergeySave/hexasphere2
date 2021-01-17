package com.sergeysav.hexasphere.client.bgfx.view

import com.sergeysav.hexasphere.client.bgfx.frame.Framebuffer
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryStack

inline class View(val id: Int) {
    fun setName(name: String) {
        BGFX.bgfx_set_view_name(id, name)
    }

    fun setRectRatio(backbufferRatio: BackbufferRatio = BackbufferRatio.EQUAL) {
        BGFX.bgfx_set_view_rect_ratio(id, 0, 0, backbufferRatio.id)
    }

    fun setFrameBuffer(framebuffer: Framebuffer = Framebuffer.DEFAULT) {
        BGFX.bgfx_set_view_frame_buffer(id, framebuffer.handle)
    }

    fun setViewMode(mode: ViewMode = ViewMode.DEFAULT) {
        BGFX.bgfx_set_view_mode(id, mode.id)
    }

    enum class BackbufferRatio(val id: Int) {
        EQUAL(BGFX.BGFX_BACKBUFFER_RATIO_EQUAL),
        HALF(BGFX.BGFX_BACKBUFFER_RATIO_HALF),
        QUARTER(BGFX.BGFX_BACKBUFFER_RATIO_QUARTER),
        EIGHTH(BGFX.BGFX_BACKBUFFER_RATIO_EIGHTH),
        SIXTEENTH(BGFX.BGFX_BACKBUFFER_RATIO_SIXTEENTH),
        DOUBLE(BGFX.BGFX_BACKBUFFER_RATIO_DOUBLE)
    }

    enum class ViewMode(val id: Int) {
        DEFAULT(BGFX.BGFX_VIEW_MODE_DEFAULT),
        SEQUENTIAL(BGFX.BGFX_VIEW_MODE_SEQUENTIAL),
        ASCENDING(BGFX.BGFX_VIEW_MODE_DEPTH_ASCENDING),
        DESCENDING(BGFX.BGFX_VIEW_MODE_DEPTH_DESCENDING)
    }

    companion object {
        fun touch() {
            BGFX.bgfx_touch(0)
        }

        fun setViewOrder(views: ViewArray) {
            MemoryStack.stackPush().use { stack ->
                val viewBuffer = stack.mallocShort(views.size)
                for (i in views.indices) {
                    viewBuffer.put(i, views[i].toShort())
                }
                BGFX.bgfx_set_view_order(0, views.size, viewBuffer)
            }
        }
    }
}

fun View.set(name: String, backbufferRatio: View.BackbufferRatio = View.BackbufferRatio.EQUAL, framebuffer: Framebuffer = Framebuffer.DEFAULT, mode: View.ViewMode = View.ViewMode.DEFAULT) {
    setName(name)
    setRectRatio(backbufferRatio)
    setFrameBuffer(framebuffer)
    setViewMode(mode)
}

typealias ViewArray = IntArray
