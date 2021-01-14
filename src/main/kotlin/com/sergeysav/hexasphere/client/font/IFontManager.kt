package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.View
import org.joml.Vector4f
import org.joml.Vector4fc

interface IFontManager {
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
        color: Vector4fc = this@IFontManager.color,
        outlineColor: Vector4fc = this@IFontManager.outlineColor,
        hAlign: HAlign = HAlign.LEFT,
        vAlign: VAlign = VAlign.CENTER,
        thickness: Float = 0f,
        outlineThickness: Float = 0f,
        multiline: Boolean = false
    )
    fun dispose()
}

inline fun IFontManager.render(width: Double, height: Double, view: View, inner: IFontManager.(Encoder, SDFFont)->Unit) {
    updateCamera(width, height, view)
    Encoder.with {
        inner(this, font)
    }
}
