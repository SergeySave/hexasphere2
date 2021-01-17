package com.sergeysav.hexasphere.client.font.impl

import com.sergeysav.hexasphere.client.bgfx.texture.Texture
import com.sergeysav.hexasphere.client.font.Font
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

abstract class STBFont(ttf: ByteBuffer) : Font {

    protected val fontInfo: STBTTFontinfo = STBTTFontinfo.malloc()
    protected var scale = 0f
    protected var descent = 0f
    protected var texture = Texture(0)
    protected var lineGap = 0f
    protected var ascent = 0f

    init {
        if (!STBTruetype.stbtt_InitFont(fontInfo, ttf)) error("Failed to initialize STB TTF")
    }

    override fun computeWidth(text: CharSequence): Double {
        if (text.isEmpty()) return 0.0
        return MemoryStack.stackPush().use { stack ->
            var width = 0
            val advance = stack.mallocInt(1)

            for (char in text) {
                STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, char.toInt(), advance, null)
                width += advance.get(0)
            }

            width * scale.toDouble()
        }
    }

    override fun getLineStep(): Double = ascent.toDouble() - descent.toDouble() + lineGap.toDouble()

    override fun dispose() {
        fontInfo.free()
        texture.dispose()
    }
}