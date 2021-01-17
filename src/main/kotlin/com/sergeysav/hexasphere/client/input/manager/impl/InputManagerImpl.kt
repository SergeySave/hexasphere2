package com.sergeysav.hexasphere.client.input.manager.impl

import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.input.manager.InputManager
import java.util.EnumSet

class InputManagerImpl : InputManager {

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

    override fun isKeyDown(key: Key) = keysDown.contains(key)
    override fun isKeyJustDown(key: Key) = keysJustDown.contains(key)
    override fun isKeyJustUp(key: Key) = keysJustUp.contains(key)
    override fun isKeyUp(key: Key) = !isKeyDown(key)
    override fun getKeyDownInt(key: Key) = if (isKeyDown(key)) 1 else 0

    override fun isMouseButtonDown(button: MouseButton) = mouseButtonsDown[button.ordinal]
    override fun isMouseButtonJustDown(button: MouseButton) = mouseButtonsJustDown.contains(button)
    override fun isMouseButtonJustUp(button: MouseButton) = mouseButtonsJustUp.contains(button)
    override fun isMouseButtonUp(button: MouseButton) = !isMouseButtonDown(button)
    override fun getMouseButtonDownInt(button: MouseButton) = if (isMouseButtonDown(button)) 1 else 0
    override fun getMouseButtonDownTime(button: MouseButton) = mouseButtonDownTime[button.ordinal]

    override fun getMouseX() = mouseX
    override fun getMouseY() = mouseY
    override fun getMouseDX() = mouseX - lastMouseX
    override fun getMouseDY() = mouseY - lastMouseY

    override fun getScrollX() = scrollX
    override fun getScrollY() = scrollY

    override fun update(delta: Double) {
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

    override fun handleOnKey(action: Action, key: Key, keyModifiers: KeyModifiers) {
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

    override fun handleOnMouseButton(action: Action, button: MouseButton, keyModifiers: KeyModifiers) {
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

    override fun onMouseMove(x: Double, y: Double, reset: Boolean) {
        mouseX = x
        mouseY = y
        if (reset) {
            lastMouseX = x
            lastMouseY = y
        }
    }

    override fun onScroll(x: Double, y: Double) {
        scrollX = x
        scrollY = y
    }
}