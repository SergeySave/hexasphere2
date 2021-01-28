package com.sergeysav.hexasphere.client.bgfx.shader

import com.sergeysav.hexasphere.client.IOUtil
import com.sergeysav.hexasphere.client.bgfx.BGFXException
import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.io.IOException

inline class Shader(val handle: Short = 0) {

    fun dispose() {
        if (handle != 0.toShort()) {
            BGFX.bgfx_destroy_shader(handle)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }

        fun loadShaderFromFile(shaderName: String): Shader {
            if (BGFXUtil.renderer == BGFX.BGFX_RENDERER_TYPE_NOOP) {
                logger.trace { "Creating NoOp shader $shaderName" }
                return Shader(0)
            }
            val resourceName = buildString {
                append("/shaders/")
                when (BGFXUtil.renderer) {
                    BGFX.BGFX_RENDERER_TYPE_DIRECT3D9 -> append("dx9/")
                    BGFX.BGFX_RENDERER_TYPE_DIRECT3D11, BGFX.BGFX_RENDERER_TYPE_DIRECT3D12 -> append("dx11/")
                    BGFX.BGFX_RENDERER_TYPE_OPENGL -> append("glsl/")
                    BGFX.BGFX_RENDERER_TYPE_METAL -> append("metal/")
                    BGFX.BGFX_RENDERER_TYPE_VULKAN -> append("spirv/")
                    else -> throw BGFXException("No shaders supported for " + BGFX.bgfx_get_renderer_name(BGFXUtil.renderer) + " renderer")
                }
                append(shaderName)
                append(".bin")
            }
            try {
                val loadResource = IOUtil.loadResource(resourceName)

                val bgfxMemory = BGFX.bgfx_make_ref_release(loadResource, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) ?:
                    throw BGFXException("Could not create BGFX memory reference for shader")
                val shader = BGFX.bgfx_create_shader(bgfxMemory)
                return Shader(shader)
            } catch (ex : IOException) {
                throw BGFXException(ex)
            }
        }
    }
}
