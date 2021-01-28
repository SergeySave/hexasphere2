package com.sergeysav.hexasphere.client.input

enum class MouseButton {
    LEFT,
    RIGHT,
    MIDDLE,
    UNKNOWN;

    companion object {
        val VALUES = values().toList()
    }
}