package com.sergeysav.hexasphere.client.input

import java.util.EnumSet

class InputManager {

    private val keysDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustUp: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val mouseButtonsDown = BooleanArray(MouseButton.values().size) { false }//: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val mouseButtonsJustDown: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val mouseButtonsJustUp: EnumSet<MouseButton> = EnumSet.noneOf(MouseButton::class.java)
    private val mouseButtonDownTime = DoubleArray(MouseButton.values().size) { 0.0 }
    private var mouseX: Double = 0.0
    private var mouseY: Double = 0.0
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0
    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0

    fun isKeyDown(key: Key) = keysDown.contains(key)
    fun isKeyJustDown(key: Key) = keysJustDown.contains(key)
    fun isKeyJustUp(key: Key) = keysJustUp.contains(key)
    fun isKeyUp(key: Key) = !isKeyDown(key)
    fun getKeyDownInt(key: Key) = if (isKeyDown(key)) 1 else 0

    fun isMouseButtonDown(button: MouseButton) = mouseButtonsDown[button.ordinal]
    fun isMouseButtonJustDown(button: MouseButton) = mouseButtonsJustDown.contains(button)
    fun isMouseButtonJustUp(button: MouseButton) = mouseButtonsJustUp.contains(button)
    fun isMouseButtonUp(button: MouseButton) = !isMouseButtonDown(button)
    fun getMouseButtonDownInt(button: MouseButton) = if (isMouseButtonDown(button)) 1 else 0
    fun getMouseButtonDownTime(button: MouseButton) = mouseButtonDownTime[button.ordinal]

    fun getMouseX() = mouseX
    fun getMouseY() = mouseY
    fun getMouseDX() = mouseX - lastMouseX
    fun getMouseDY() = mouseY - lastMouseY

    fun getScrollX() = scrollX
    fun getScrollY() = scrollY

    fun update(delta: Double) {
        keysJustDown.clear()
        keysJustUp.clear()

        mouseButtonsJustDown.clear()
        mouseButtonsJustUp.clear()

        lastMouseX = mouseX
        lastMouseY = mouseY

        scrollX = 0.0
        scrollY = 0.0

        for (i in mouseButtonDownTime.indices) {
            if (mouseButtonsDown[i]) {
                mouseButtonDownTime[i] += delta
            } else {
                mouseButtonDownTime[i] = 0.0
            }
        }
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
//                mouseButtonsDown.remove(button)
                mouseButtonsDown[button.ordinal] = false
                mouseButtonsJustUp.add(button)
            }
            Action.PRESS -> {
//                mouseButtonsDown.add(button)
                mouseButtonsDown[button.ordinal] = true
                mouseButtonsJustDown.add(button)
                mouseButtonDownTime[button.ordinal] = 0.0
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

    fun onScroll(x: Double, y: Double) {
        scrollX = x
        scrollY = y
    }
}