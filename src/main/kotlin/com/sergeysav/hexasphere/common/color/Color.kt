package com.sergeysav.hexasphere.common.color

import kotlin.math.roundToInt

inline class Color(val value: Int) {

    constructor(r: Int, g: Int, b: Int) : this(r, g, b, 0xFF)
    constructor(r: Int, g: Int, b: Int, a: Int) : this((r and 0xFF) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 16) or ((a and 0xFF) shl 24) )
    constructor(r: Float, g: Float, b: Float) : this(r, g, b, 1f)
    constructor(r: Float, g: Float, b: Float, a: Float) : this((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt(), (a * 255).roundToInt())

    val red: Int get() = value and 0xFF
    val green: Int get() = (value ushr 8) and 0xFF
    val blue: Int get() = (value ushr 16) and 0xFF
    val alpha: Int get() = (value ushr 24) and 0xFF
    val redFloat: Float get() = red / 255f
    val greenFloat: Float get() = green / 255f
    val blueFloat: Float get() = blue / 255f
    val alphaFloat: Float get() = alpha / 255f

    operator fun component0() = red
    operator fun component1() = green
    operator fun component2() = blue
    operator fun component3() = alpha

    fun red(r: Int) = Color((value and 0xFF.inv()) or (r and 0xFF) )
    fun green(g: Int) = Color((value and 0xFF00.inv()) or ((g and 0xFF) shl 8) )
    fun blue(b: Int) = Color((value and 0xFF0000.inv()) or ((b and 0xFF) shl 16) )
    fun alpha(a: Int) = Color((value and 0x00FFFFFF) or ((a and 0xFF) shl 24) )
    fun red(r: Float) = red((r * 255).roundToInt())
    fun green(g: Float) = green((g * 255).roundToInt())
    fun blue(b: Float) = blue((b * 255).roundToInt())
    fun alpha(a: Float) = alpha((a * 255).roundToInt())

    operator fun plus(that: Color) = Color(this.red + that.red, this.green + that.green, this.blue + that.blue, this.alpha + that.alpha)
    operator fun minus(that: Color) = Color(this.red - that.red, this.green - that.green, this.blue - that.blue, this.alpha - that.alpha)
    infix fun xor(that: Color) = Color(this.red xor that.red, this.green xor that.green, this.blue xor that.blue, this.alpha xor that.alpha)
    operator fun times(scalar: Float) = Color(this.redFloat * scalar, this.greenFloat * scalar, this.blueFloat * scalar, this.alphaFloat * scalar)

    companion object {
        val BLACK = Color(0x00,0x00,0x00)
        val WHITE = Color(0xFF,0xFF,0xFF)
    }
}