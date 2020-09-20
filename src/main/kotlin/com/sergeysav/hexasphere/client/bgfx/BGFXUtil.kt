package com.sergeysav.hexasphere.client.bgfx

import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXReleaseFunctionCallback
import org.lwjgl.system.MemoryUtil.nmemFree


object BGFXUtil {

    private val logger = KotlinLogging.logger {  }
    var renderer: Int = 0
    var zZeroToOne = false
    val releaseMemoryCb = BGFXReleaseFunctionCallback.create { pointer: Long, _: Long ->
        nmemFree(pointer)
    }

    fun dispose() {
        releaseMemoryCb.free()
    }

    fun getSupportedRenderers(): IntArray {
        val rendererTypes = IntArray(BGFX.BGFX_RENDERER_TYPE_COUNT)
        val count = BGFX.bgfx_get_supported_renderers(rendererTypes)

        return IntArray(count.toInt()) { rendererTypes[it] }
    }

    fun printSupportedRenderers(renderers: IntArray) {
        for (renderer in renderers) {
            logger.info { "BGFX: Renderer Supported: ${BGFX.bgfx_get_renderer_name(renderer)}" }
        }
    }
}