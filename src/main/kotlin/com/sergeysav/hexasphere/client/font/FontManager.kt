package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.client.IOUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.OrthographicCamera
import com.sergeysav.hexasphere.client.bgfx.View
import org.joml.Vector4f
import org.joml.Vector4fc

class FontManager : IFontManager {

    override val font = SDFFont(IOUtil.loadResource("/font/OpenSans/OpenSans-Regular.ttf"), pixelHeight = 60f, bitmapSize = 512)
    private val camera = OrthographicCamera(0.0, 0.0,  0.0, 0.0, -1f, 1f)
    override val color = Vector4f(1f, 1f, 1f, 1f)
    override val outlineColor = Vector4f(1f, 1f, 1f, 1f)

    override fun updateCamera(width: Double, height: Double, view: View) {
        camera.width = width
        camera.height = height
        camera.update(view)
    }

    override fun Encoder.drawFont(
        text: CharSequence,
        x: Double,
        y: Double,
        view: View,
        scale: Float,
        color: Vector4fc,
        outlineColor: Vector4fc,
        hAlign: HAlign,
        vAlign: VAlign,
        thickness: Float,
        outlineThickness: Float,
        multiline: Boolean
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

    override fun dispose() {
        font.dispose()
        camera.dispose()
    }
}