package com.sergeysav.hexasphere.client.bgfx

import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXReleaseFunctionCallback
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil

object BGFXUtil {

    private val logger = KotlinLogging.logger {  }
    var renderer: Int = 0
    var zZeroToOne = false
    val releaseMemoryCb = BGFXReleaseFunctionCallback.create { pointer: Long, _: Long ->
        MemoryUtil.nmemFree(pointer)
    }
    val stbImageReleaseMemoryCb = BGFXReleaseFunctionCallback.create { pointer: Long, _: Long ->
        STBImage.nstbi_image_free(pointer)
    }
    var texelHalf: Float = 0f
    var reset: Int = 0

    fun dispose() {
        releaseMemoryCb.free()
        stbImageReleaseMemoryCb.free()
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