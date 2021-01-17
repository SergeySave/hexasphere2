package com.sergeysav.hexasphere.client.game

import org.joml.Vector3f
import org.joml.Vector3fc
import kotlin.math.sqrt

object SphereRayIntersect {

    fun computeIntersection(ray: Vector3fc, source: Vector3fc, center: Vector3fc, radius: Double, output: Vector3f): Vector3f? {
        source.sub(center, output) // output = direction towards center of sphere
        val part1 = -(ray.dot(output)).toDouble()
        val det = part1*part1 - output.lengthSquared() + radius*radius

        return if (det >= 0) {
            val part2 = sqrt(det)
            val dist = minOf(part1 - part2, part1 + part2)
            ray.mul(dist.toFloat(), output).add(source)
        } else {
            null
        }
    }
}