package com.sergeysav.hexasphere.common

import org.joml.Vector3f
import org.joml.Vector4f

fun Vector3f.setColor(r: Int, g: Int, b: Int) = this.set(r / 255f, g / 255f, b / 255f)

fun Vector4f.setColor(r: Int, g: Int, b: Int, a: Int = 255) = this.set(r / 255f, g / 255f, b / 255f, a / 255f)
