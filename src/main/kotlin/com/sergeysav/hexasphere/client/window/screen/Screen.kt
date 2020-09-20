package com.sergeysav.hexasphere.client.window.screen

import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyAction
import com.sergeysav.hexasphere.client.input.KeyModifiers

interface Screen {

    fun create()

    fun resume()

    fun render(delta: Double, width: Int, height: Int) : ScreenAction

    fun pause()

    fun dispose()

    fun onKey(keyAction: KeyAction, key: Key, keyModifiers: KeyModifiers) { }
}