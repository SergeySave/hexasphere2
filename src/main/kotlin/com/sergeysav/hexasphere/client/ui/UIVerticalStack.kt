package com.sergeysav.hexasphere.client.ui

import com.sergeysav.hexasphere.client.font.align.HAlign
import com.sergeysav.hexasphere.client.font.align.VAlign
import com.sergeysav.hexasphere.client.font.manager.render
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.common.color.Color
import com.sergeysav.hexasphere.common.setColor

class UIVerticalStack(private val uiManager: UIManager) {

    lateinit var window: UIWindow

    inline fun with(window: UIWindow, inner: UIVerticalStack.()->Unit) {
        this.window = window
        inner()
    }

    fun label(text: CharSequence, color: Color, outlineColor: Color = color, outline: Double = 0.0, thickness: Double = 0.0, scale: Double = 1.0) {
        val fontScale = (uiManager.fontScale * scale * uiManager.uiSettings.uiScale).toFloat()

        uiManager.fontManager.render { encoder, _ ->
            window.height -= font.getLineStep() * fontScale
            encoder.drawFont(
                text,
                window.x, window.y + window.height,
                uiManager.view,
                fontScale,
                this.color.setColor(color),
                this.outlineColor.setColor(outlineColor),
                HAlign.LEFT,
                VAlign.TOP,
                outlineThickness = outline.toFloat(),
                thickness = thickness.toFloat()
            )
        }
    }

    fun labelButton(text: CharSequence, color: Color, mouseoverColor: Color, outlineColor: Color = color, outline: Double = 0.0, thickness: Double = 0.0, scale: Double = 1.0): Boolean {
        val fontScale = (uiManager.fontScale * scale * uiManager.uiSettings.uiScale).toFloat()
        val isMouseover = uiManager.inputManager.getMouseX() in window.x..(window.x+window.width) && uiManager.height - uiManager.inputManager.getMouseY() in (window.y + window.height - uiManager.fontManager.font.getLineStep() * fontScale)..(window.y + window.height)

        label(text, if (isMouseover) mouseoverColor else color, outlineColor, outline, thickness, scale)

        return isMouseover && uiManager.inputManager.isMouseButtonJustUp(MouseButton.LEFT)
    }

    fun skip(lines: Double, scale: Double = 1.0) {
        val fontScale = (uiManager.fontScale * scale * uiManager.uiSettings.uiScale).toFloat()
        window.height -= uiManager.fontManager.font.getLineStep() * fontScale * lines
    }
}