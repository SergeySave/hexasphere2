package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.client.IOUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.OrthographicCamera
import com.sergeysav.hexasphere.client.bgfx.View
import org.joml.Vector4f

class FontManager {

    val font = SDFFont(IOUtil.loadResource("/font/OpenSans/OpenSans-Regular.ttf"), pixelHeight = 60f, bitmapSize = 512)
    private val camera = OrthographicCamera(0.0, 0.0,  0.0, 0.0, -1f, 1f)
    val color = Vector4f(1f, 1f, 1f, 1f)
    val outlineColor = Vector4f(1f, 1f, 1f, 1f)

    fun updateCamera(width: Double, height: Double, view: View) {
        camera.width = width
        camera.height = height
        camera.update(view)
    }

    inline fun render(width: Double, height: Double, view: View, inner: FontManager.(Encoder, SDFFont)->Unit) {
        updateCamera(width, height, view)
        Encoder.with {
            inner(this, font)
        }
    }

    fun Encoder.drawFont(
        text: CharSequence,
        x: Double,
        y: Double,
        view: View,
        scale: Float,
        hAlign: HAlign = HAlign.LEFT,
        vAlign: VAlign = VAlign.CENTER,
        thickness: Float = 0f,
        outlineThickness: Float = 0f,
        multiline: Boolean = false
    ) {
        if (!multiline) {
            val fixedX = when (hAlign) {
                HAlign.LEFT -> x
                HAlign.CENTER -> x - scale * font.computeWidth(text) / 2
                HAlign.RIGHT -> x - scale * font.computeWidth(text)
            }
            val fixedY = when (vAlign) {
                VAlign.TOP -> y
                VAlign.CENTER -> y - scale * font.computeHeight(text) / 2
                VAlign.BOTTOM -> y - scale * font.computeHeight(text)
            }
            font.render(
                this, view, fixedX, fixedY, text,
                color = color,
                drawScale = scale,
                extraThickness = thickness,
                outlineColor = if (outlineThickness > 0) outlineColor else color,
                outlineThickness = outlineThickness
            )
        } else {
            val parts = text.split('\n')
            var partY = when (vAlign) {
                VAlign.TOP -> y + scale * (parts.size - 1) * font.getLineStep()
                VAlign.CENTER -> y + scale * (parts.size - 1) * font.getLineStep() / 2 - scale * font.computeHeight(parts[0]) / 2
                VAlign.BOTTOM -> y - scale * font.computeHeight(parts[0])
            }
            for (part in parts) {
                val fixedX = when (hAlign) {
                    HAlign.LEFT -> x
                    HAlign.CENTER -> x - scale * font.computeWidth(part) / 2
                    HAlign.RIGHT -> x - scale * font.computeWidth(part)
                }
                font.render(
                    this, view, fixedX, partY, part,
                    color = color,
                    drawScale = scale,
                    extraThickness = thickness,
                    outlineColor = if (outlineThickness > 0) outlineColor else color,
                    outlineThickness = outlineThickness
                )
                partY -= font.getLineStep() * scale
            }
        }
    }

    fun dispose() {
        font.dispose()
        camera.dispose()
    }
}