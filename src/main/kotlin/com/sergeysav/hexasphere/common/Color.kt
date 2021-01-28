package com.sergeysav.hexasphere.common

import com.sergeysav.hexasphere.common.color.Color
import org.joml.Vector3f
import org.joml.Vector4f

fun Vector3f.setColor(r: Int, g: Int, b: Int) = this.set(r / 255f, g / 255f, b / 255f)

fun Vector4f.setColor(r: Int, g: Int, b: Int, a: Int = 255) = this.set(r / 255f, g / 255f, b / 255f, a / 255f)

fun Vector3f.setColor(color: Color) = this.setColor(color.red, color.green, color.blue)

fun Vector4f.setColor(color: Color) = this.setColor(color.red, color.green, color.blue, color.alpha)
