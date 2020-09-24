package com.sergeysav.hexasphere.client.ui

import com.sergeysav.hexasphere.client.bgfx.Framebuffer
import org.lwjgl.bgfx.BGFX

class ManagedViewFramebuffer(
    private val name: String,
    private val clearColor: Int = 0x00000000,
    private val useDefaultFramebuffer: Boolean = true
) {
    private var framebuffer: Framebuffer = Framebuffer()
    private var lastWidth: Int = 0
    private var lastHeight: Int = 0

    fun setView(width: Int, height: Int, viewId: Int) {
        if (!useDefaultFramebuffer && (!framebuffer.isValid() || width != lastWidth || height != lastHeight)) {
            lastWidth = width
            lastHeight = height
            if (framebuffer.isValid()) {
                framebuffer.dispose()
            }
            framebuffer = Framebuffer.createScreenFramebuffer(width, height)
        }

        BGFX.bgfx_set_view_name(viewId, name)
        if (!useDefaultFramebuffer) {
            BGFX.bgfx_set_view_clear(viewId, BGFX.BGFX_CLEAR_COLOR or BGFX.BGFX_CLEAR_DEPTH, clearColor, 1.0f, 0)
        }
        BGFX.bgfx_set_view_rect_ratio(viewId, 0, 0, BGFX.BGFX_BACKBUFFER_RATIO_EQUAL)
        BGFX.bgfx_set_view_frame_buffer(viewId, framebuffer.handle)
    }

    fun dispose() {
        if (framebuffer.isValid()) {
            framebuffer.dispose()
            framebuffer = Framebuffer()
        }
    }
}