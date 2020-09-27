package com.sergeysav.hexasphere.client.camera

import com.sergeysav.hexasphere.client.bgfx.View
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc

interface Camera3d {

    val position: Vector3f
    val forward: Vector3fc
    val right: Vector3fc
    val up: Vector3fc

    // Camera Settings
    fun setFovDeg(degrees: Float)
    fun setFovRad(radians: Float)
    fun setAspect(width: Int, height: Int)
    var zNear: Float
    var zFar: Float

    // Camera Location
    fun setPosition(position: Vector3fc)
    fun translate(translation: Vector3fc)
    fun translateIn(direction: Vector3fc, amount: Float)

    // Camera Orientation
    fun rotate(axis: Vector3fc, radians: Float)
    fun rotateAround(center: Vector3fc, axis: Vector3fc, radians: Float)
    fun lookAt(target: Vector3fc)

    // Camera Projection
    fun projectToWorld(input: Vector2fc, output: Vector3f): Vector3f

    // Camera Lifecycle
    fun update(viewId: View = View(0), ignoreCameraPosition: Boolean = false)
    fun dispose()
}