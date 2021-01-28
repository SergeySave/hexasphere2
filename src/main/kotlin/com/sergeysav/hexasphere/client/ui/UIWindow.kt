package com.sergeysav.hexasphere.client.ui

import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.render.DebugRender
import com.sergeysav.hexasphere.common.color.Color
import org.joml.Vector3f

class UIWindow(private val uiManager: UIManager) {

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val vec3c = Vector3f()
    private val vec3d = Vector3f()

    var x = 0.0
    var y = 0.0
    var width = 0.0
    var height = 0.0
    var xFull = 0.0
    var yFull = 0.0
    var widthFull = 0.0
    var heightFull = 0.0
    var consumeMouseEvents = false

    inline fun with(inner: UIWindow.()->Unit) {
        reset()
        inner()
        finalize()
    }

    fun reset() {
        x = 0.0
        y = 0.0
        width = 0.0
        height = 0.0
        xFull = 0.0
        yFull = 0.0
        widthFull = 0.0
        heightFull = 0.0

        consumeMouseEvents = false
    }

    fun finalize() {
        if (consumeMouseEvents && uiManager.inputManager.getMouseX() in xFull..(xFull+widthFull) &&
            uiManager.height - uiManager.inputManager.getMouseY() in yFull..(yFull+heightFull)) {
            uiManager.inputManager.consumeMouseEvents()
        }
    }

    fun setBox(x: Double, y: Double, w: Double, h: Double) {
        this.x = x
        this.y = y
        this.width = w
        this.height = h
        this.xFull = x
        this.yFull = y
        this.widthFull = w
        this.heightFull = h
    }

    fun fill(color: Color) {
        Encoder.with {
            setState(Encoder.UI)
            DebugRender.fillQuad(
                this,
                uiManager.view,
                vec3a.set(x.toFloat(), y.toFloat(), 0f),
                vec3b.set(x.toFloat(), (y + height).toFloat(), 0f),
                vec3c.set((x + width).toFloat(), (y + height).toFloat(), 0f),
                vec3d.set((x + width).toFloat(), y.toFloat(), 0f),
                color
            )
        }
    }

    fun addBorder(top: Double, bottom: Double = top, right: Double = top, left: Double = right) {
        x += left
        y += bottom
        width -= left + right
        height -= bottom + top
    }
}