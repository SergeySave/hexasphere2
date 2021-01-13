package com.sergeysav.hexasphere.common

import org.joml.Vector3f
import org.joml.Vector4f

fun color(r: Int, g: Int, b: Int): Int {
    return (r and 0xFF) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 16)
}

fun color(r: Int, g: Int, b: Int, a: Int): Int {
    return (r and 0xFF) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 16) or ((a and 0xFF) shl 24)
}

fun redComponent(color: Int): Int = color and 0xFF
fun greenComponent(color: Int): Int = (color ushr 8) and 0xFF
fun blueComponent(color: Int): Int = (color ushr 16) and 0xFF
fun alphaComponent(color: Int): Int = (color ushr 24) and 0xFF

fun Vector3f.setColor(r: Int, g: Int, b: Int) = this.set(r / 255f, g / 255f, b / 255f)

fun Vector4f.setColor(r: Int, g: Int, b: Int, a: Int = 255) = this.set(r / 255f, g / 255f, b / 255f, a / 255f)
