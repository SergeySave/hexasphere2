package com.sergeysav.hexasphere.client.font.manager

import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.view.View
import com.sergeysav.hexasphere.client.font.align.HAlign
import com.sergeysav.hexasphere.client.font.align.VAlign
import com.sergeysav.hexasphere.client.font.impl.SDFFont
import org.joml.Vector4f
import org.joml.Vector4fc

interface FontManager {
    val font: SDFFont
    val color: Vector4f
    val outlineColor: Vector4f
    fun updateCamera(width: Double, height: Double, view: View)
    fun Encoder.drawFont(
        text: CharSequence,
        x: Double,
        y: Double,
        view: View,
        scale: Float,
        color: Vector4fc = this@FontManager.color,
        outlineColor: Vector4fc = this@FontManager.outlineColor,
        hAlign: HAlign = HAlign.LEFT,
        vAlign: VAlign = VAlign.CENTER,
        thickness: Float = 0f,
        outlineThickness: Float = 0f,
        multiline: Boolean = false
    )
    fun dispose()
}

inline fun FontManager.render(width: Double, height: Double, view: View, inner: FontManager.(Encoder, SDFFont)->Unit) {
    updateCamera(width, height, view)
    Encoder.with {
        inner(this, font)
    }
}

inline fun FontManager.render(inner: FontManager.(Encoder, SDFFont)->Unit) {
    Encoder.with {
        inner(this, font)
    }
}
