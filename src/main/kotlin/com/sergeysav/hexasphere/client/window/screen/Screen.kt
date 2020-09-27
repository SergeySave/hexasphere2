package com.sergeysav.hexasphere.client.window.screen

import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton

interface Screen {

    fun create()

    fun resume()

    fun render(delta: Double, width: Int, height: Int) : ScreenAction

    fun pause()

    fun dispose()

    fun onKey(action: Action, key: Key, keyModifiers: KeyModifiers) { }

    fun onMouseButton(action: Action, mouseButton: MouseButton, keyModifiers: KeyModifiers) { }

    fun onMouseMove(x: Double, y: Double, reset: Boolean) { }
}