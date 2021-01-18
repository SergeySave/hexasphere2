package com.sergeysav.hexasphere.client.stb

import com.sergeysav.hexasphere.client.IOUtil
import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.texture.Texture
import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class Image(bytes: ByteBuffer, flipY: Boolean = true) {
    val data: ByteBuffer
    val width: Int
    val height: Int
    val channels: Int

    init {
        val w = IntArray(1)
        val h = IntArray(1)
        val c = IntArray(1)
        STBImage.stbi_set_flip_vertically_on_load(flipY)
        data = STBImage.stbi_load_from_memory(bytes, w, h, c, 0)!!
        width = w[0]
        height = h[0]
        channels = c[0]
    }

    fun free() {
        STBImage.stbi_image_free(data)
    }

    companion object {
        val logger = KotlinLogging.logger {  }
    }
}

fun Image.Companion.createTexture(resourceName: String, flipY: Boolean = true): Texture {
    val image = Image(IOUtil.loadResource(resourceName), flipY)

    val format = when (image.channels) {
        1 -> BGFX.BGFX_TEXTURE_FORMAT_R8
        2 -> BGFX.BGFX_TEXTURE_FORMAT_RG8
        3 -> BGFX.BGFX_TEXTURE_FORMAT_RGB8
        4 -> BGFX.BGFX_TEXTURE_FORMAT_RGBA8
        else -> {
            image.free()
            error("Invalid Image Format")
        }
    }

    val bgfxMemory = BGFX.bgfx_make_ref_release(image.data, BGFXUtil.stbImageReleaseMemoryCb, MemoryUtil.NULL)

    val flags = BGFX.BGFX_TEXTURE_NONE
    return Texture(BGFX.bgfx_create_texture_2d(image.width, image.height, false, 1, format, flags, bgfxMemory))
}
