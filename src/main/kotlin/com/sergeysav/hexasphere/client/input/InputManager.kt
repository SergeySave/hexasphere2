package com.sergeysav.hexasphere.client.input

interface InputManager {
    fun isKeyDown(key: Key): Boolean
    fun isKeyJustDown(key: Key): Boolean
    fun isKeyJustUp(key: Key): Boolean
    fun isKeyUp(key: Key): Boolean
    fun getKeyDownInt(key: Key): Int
    fun isMouseButtonDown(button: MouseButton): Boolean
    fun isMouseButtonJustDown(button: MouseButton): Boolean
    fun isMouseButtonJustUp(button: MouseButton): Boolean
    fun isMouseButtonUp(button: MouseButton): Boolean
    fun getMouseButtonDownInt(button: MouseButton): Int
    fun getMouseButtonDownTime(button: MouseButton): Double
    fun getMouseX(): Double
    fun getMouseY(): Double
    fun getMouseDX(): Double
    fun getMouseDY(): Double
    fun getScrollX(): Double
    fun getScrollY(): Double
    fun update(delta: Double)
    fun handleOnKey(action: Action, key: Key, keyModifiers: KeyModifiers)
    fun handleOnMouseButton(action: Action, button: MouseButton, keyModifiers: KeyModifiers)
    fun onMouseMove(x: Double, y: Double, reset: Boolean)
    fun onScroll(x: Double, y: Double)
}
