package com.sergeysav.hexasphere.common

fun color(r: Int, g: Int, b: Int): Int {
    return (r and 0xFF) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 16)
}
