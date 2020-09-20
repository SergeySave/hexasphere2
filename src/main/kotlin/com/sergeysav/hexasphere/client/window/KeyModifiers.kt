package com.sergeysav.hexasphere.client.window

interface KeyModifiers {
    val shift: Boolean
    val control: Boolean
    val alt: Boolean
    val `super`: Boolean
    val capsLock: Boolean
    val numLock: Boolean

    data class Mutable(
        override var shift: Boolean = false,
        override var control: Boolean = false,
        override var alt: Boolean = false,
        override var `super`: Boolean = false,
        override var capsLock: Boolean = false,
        override var numLock: Boolean = false
    ) : KeyModifiers
}
