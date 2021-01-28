package com.sergeysav.hexasphere.client.ui

import com.sergeysav.hexasphere.client.bgfx.view.View
import com.sergeysav.hexasphere.client.font.manager.FontManager
import com.sergeysav.hexasphere.client.input.manager.InputManager
import com.sergeysav.hexasphere.client.settings.UISettings

object UIManager {

    val uiWindow = UIWindow(this)
    val uiVerticalStack = UIVerticalStack(this)

    var width: Float = 0f
    var height: Float = 0f
    lateinit var inputManager: InputManager
    var view: View = View(0)
    lateinit var fontManager: FontManager
    lateinit var uiSettings: UISettings
    var fontScale = 1.0

    inline fun start(
        screenWidth: Double,
        screenHeight: Double,
        inputManager: InputManager,
        fontManager: FontManager,
        uiSettings: UISettings,
        view: View,
        inner: UIManager.()->Unit
    ) {
        width = screenWidth.toFloat()
        height = screenHeight.toFloat()
        this.inputManager = inputManager
        this.view = view
        this.fontManager = fontManager
        this.uiSettings = uiSettings
        this.fontScale = 1.0
        this.inner()
    }

    inline fun window(inner: UIWindow.()->Unit) = uiWindow.with(inner)

    inline fun vertStack(inner: UIVerticalStack.()->Unit) = uiVerticalStack.with(uiWindow, inner)
}