package com.sergeysav.hexasphere.client.input

import java.util.*

class InputManager {

    private val keysDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustDown: EnumSet<Key> = EnumSet.noneOf(Key::class.java)
    private val keysJustUp: EnumSet<Key> = EnumSet.noneOf(Key::class.java)

    fun isKeyDown(key: Key) = keysDown.contains(key)
    fun isKeyJustDown(key: Key) = keysJustDown.contains(key)
    fun isKeyJustUp(key: Key) = keysJustUp.contains(key)
    fun isKeyUp(key: Key) = !isKeyDown(key)
    fun getKeyDownInt(key: Key) = if (isKeyDown(key)) 1 else 0

    fun update() {
        keysJustDown.clear()
        keysJustUp.clear()
    }

    fun handleOnKey(keyAction: KeyAction, key: Key, keyModifiers: KeyModifiers) {
        when (keyAction) {
            KeyAction.RELEASE -> {
                keysDown.remove(key)
                keysJustUp.add(key)
            }
            KeyAction.PRESS -> {
                keysDown.add(key)
                keysJustDown.add(key)
            }
            KeyAction.REPEAT -> { }
        }
    }
}