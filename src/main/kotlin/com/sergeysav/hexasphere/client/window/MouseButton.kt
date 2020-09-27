package com.sergeysav.hexasphere.client.window

import com.sergeysav.hexasphere.client.input.MouseButton
import org.lwjgl.glfw.GLFW

fun MouseButton.Companion.fromGLFWCode(button: Int) = when (button) {
    GLFW.GLFW_MOUSE_BUTTON_LEFT -> MouseButton.LEFT
    GLFW.GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.RIGHT
    GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.MIDDLE
    else -> MouseButton.UNKNOWN
}
