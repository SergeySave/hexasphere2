package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX


inline class Framebuffer(val handle: Short = BGFX.BGFX_INVALID_HANDLE) {

    fun isValid() = handle != BGFX.BGFX_INVALID_HANDLE

    fun dispose() {
        BGFX.bgfx_destroy_frame_buffer(handle)
    }

    companion object {
        val DEFAULT = Framebuffer()

        fun createScreenFramebuffer(width: Int, height: Int): Framebuffer {
            val msaa = (BGFXUtil.reset and BGFX.BGFX_RESET_MSAA_MASK) ushr BGFX.BGFX_RESET_MSAA_SHIFT
            val reset = ((msaa + 1).toLong() shl BGFX.BGFX_TEXTURE_RT_MSAA_SHIFT) or BGFX.BGFX_SAMPLER_U_CLAMP.toLong() or BGFX.BGFX_SAMPLER_V_CLAMP.toLong()
            val textureFlags = BGFX.BGFX_TEXTURE_RT_WRITE_ONLY or ((msaa + 1).toLong() shl BGFX.BGFX_TEXTURE_RT_MSAA_SHIFT)

            val depthFormat = if (BGFX.bgfx_is_texture_valid(0, false, 1, BGFX.BGFX_TEXTURE_FORMAT_D16, textureFlags)) {
                BGFX.BGFX_TEXTURE_FORMAT_D16
            } else if (BGFX.bgfx_is_texture_valid(0, false, 1, BGFX.BGFX_TEXTURE_FORMAT_D24S8, textureFlags)) {
                BGFX.BGFX_TEXTURE_FORMAT_D24S8
            } else {
                BGFX.BGFX_TEXTURE_FORMAT_D32
            }

            val colorBuffer = BGFX.bgfx_create_texture_2d(
                width,
                height,
                false,
                1,
                BGFX.BGFX_TEXTURE_FORMAT_RGBA8,
                reset,
                null
            )
            val depthBuffer = BGFX.bgfx_create_texture_2d(
                width,
                height,
                false,
                1,
                depthFormat,
                textureFlags,
                null
            )
            return Framebuffer(BGFX.bgfx_create_frame_buffer_from_handles(shortArrayOf(colorBuffer, depthBuffer), true))
//            MemoryStack.stackPush().use { stack ->
//                return Framebuffer(BGFX.bgfx_create_frame_buffer_from_handles(stack.shorts(colorBuffer, depthBuffer), true))
//            }
        }
    }
}