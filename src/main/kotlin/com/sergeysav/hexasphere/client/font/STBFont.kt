package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.client.bgfx.Texture
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

abstract class STBFont(ttf: ByteBuffer) : Font {

    protected val fontInfo: STBTTFontinfo = STBTTFontinfo.malloc()
    protected var scale = 0f
    protected var descent = 0f
    protected var texture = Texture(0)

    init {
        if (!STBTruetype.stbtt_InitFont(fontInfo, ttf)) error("Failed to initialize STB TTF")
    }

    override fun computeWidth(text: CharSequence): Double {
        return MemoryStack.stackPush().use { stack ->
            var width = 0.0
            val advance = stack.mallocInt(1)

            for (char in text) {
                STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, char.toInt(), advance, null)
                width += advance.get(0) * scale.toDouble()
            }

            width
        }
    }

    override fun dispose() {
        fontInfo.free()
        texture.dispose()
    }
}