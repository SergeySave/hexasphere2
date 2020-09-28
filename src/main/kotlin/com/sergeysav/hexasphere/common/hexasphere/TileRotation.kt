package com.sergeysav.hexasphere.common.hexasphere

import org.joml.Matrix3d
import org.joml.Matrix3f
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import kotlin.math.acos

fun HexasphereTile.getFlatteningMatrix(): Matrix3d {
    return computeTileRotation(centroid, Matrix3d())
}

fun computeTileRotation(centroid: Vector3dc, output: Matrix3d): Matrix3d {
    val desiredNormal = Vector3d(0.0, 0.0, 1.0).normalize()
    val currentNormal = Vector3d(centroid).normalize()

    val angle = acos(desiredNormal.dot(currentNormal))

    return output.identity().rotate(angle, currentNormal.cross(desiredNormal).normalize())
}

fun computeTileRotation(centroid: Vector3dc, work1: Vector3f, work2: Vector3f, output: Matrix3f): Matrix3f {
    val desiredNormal = work1.set(0.0, 0.0, 1.0).normalize()
    val currentNormal = work2.set(centroid).normalize()

    val angle = acos(desiredNormal.dot(currentNormal))

    return output.identity().rotate(angle, currentNormal.cross(desiredNormal).normalize())
}
