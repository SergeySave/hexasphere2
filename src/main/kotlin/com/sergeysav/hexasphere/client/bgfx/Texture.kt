package com.sergeysav.hexasphere.client.bgfx

import com.sergeysav.hexasphere.client.IOUtil
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil


inline class Texture(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_texture(handle)
    }

    companion object {
        fun newCubeMap(fileName: String): Texture {
            val textureMemory = BGFX.bgfx_make_ref_release(IOUtil.loadResource(fileName), BGFXUtil.releaseMemoryCb, MemoryUtil.NULL) ?:
                throw BGFXException("Could not create BGFX memory reference for texture")
            return Texture(BGFX.bgfx_create_texture(textureMemory, BGFX.BGFX_TEXTURE_NONE, 0, null))
        }
    }
}