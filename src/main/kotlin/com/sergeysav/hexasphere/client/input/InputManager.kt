package com.sergeysav.hexasphere.client.input

import java.util.EnumSet

class InputManager {

    private val keysDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustUp: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val mouseButtonsDown: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val mouseButtonsJustDown: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val mouseButtonsJustUp: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private var mouseX: Double = 0.0
    private var mouseY: Double = 0.0
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0

    fun isKeyDown(key: Key) = keysDown.contains(key)
    fun isKeyJustDown(key: Key) = keysJustDown.contains(key)
    fun isKeyJustUp(key: Key) = keysJustUp.contains(key)
    fun isKeyUp(key: Key) = !isKeyDown(key)
    fun getKeyDownInt(key: Key) = if (isKeyDown(key)) 1 else 0

    fun isMouseButtonDown(button: MouseButton) = mouseButtonsDown.contains(button)
    fun isMouseButtonJustDown(button: MouseButton) = mouseButtonsJustDown.contains(button)
    fun isMouseButtonJustUp(button: MouseButton) = mouseButtonsJustUp.contains(button)
    fun isMouseButtonUp(button: MouseButton) = !isMouseButtonDown(button)
    fun getMouseButtonDownInt(button: MouseButton) = if (isMouseButtonDown(button)) 1 else 0

    fun getMouseX() = mouseX
    fun getMouseY() = mouseY
    fun getMouseDX() = mouseX - lastMouseX
    fun getMouseDY() = mouseY - lastMouseY

    fun update() {
        keysJustDown.clear()
        keysJustUp.clear()

        mouseButtonsJustDown.clear()
        mouseButtonsJustUp.clear()

        lastMouseX = mouseX
        lastMouseY = mouseY
    }

    fun handleOnKey(action: Action, key: Key, keyModifiers: KeyModifiers) {
        when (action) {
            Action.RELEASE -> {
                keysDown.remove(key)
                keysJustUp.add(key)
            }
            Action.PRESS -> {
                keysDown.add(key)
                keysJustDown.add(key)
            }
            Action.REPEAT -> { }
        }
    }

    fun handleOnMouseButton(action: Action, button: MouseButton, keyModifiers: KeyModifiers) {
        when (action) {
            Action.RELEASE -> {
                mouseButtonsDown.remove(button)
                mouseButtonsJustUp.add(button)
            }
            Action.PRESS -> {
                mouseButtonsDown.add(button)
                mouseButtonsJustDown.add(button)
            }
            Action.REPEAT -> { }
        }
    }

    fun onMouseMove(x: Double, y: Double, reset: Boolean) {
        mouseX = x
        mouseY = y
        if (reset) {
            lastMouseX = x
            lastMouseY = y
        }
    }
}