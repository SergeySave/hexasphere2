package com.sergeysav.hexasphere.client

import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer

/**
 * @author sergeys
 *
 * @constructor Creates a new Image
 */
class Image private constructor(
    val data: ByteBuffer,
    val width: Int,
    val height: Int,
    val channels: Int,
    private val stbFree: Boolean
) {

    fun free() {
        if (stbFree) {
            STBImage.stbi_image_free(data)
        }
    }
    
    companion object {
        fun load(bytes: ByteBuffer, flipY: Boolean = true): Image {
            val w = IntArray(1)
            val h = IntArray(1)
            val c = IntArray(1)
            STBImage.stbi_set_flip_vertically_on_load(flipY)
            val data = STBImage.stbi_load_from_memory(bytes, w, h, c, 0)!!
            val width = w[0]
            val height = h[0]
            val channels = c[0]
            return Image(data, width, height, channels, true)
        }

        fun createDirect(data: ByteBuffer, width: Int, height: Int, channels: Int) = Image(data, width, height, channels, false)
    }
}