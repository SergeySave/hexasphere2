package com.sergeysav.hexasphere.client.game.camera

import com.sergeysav.hexasphere.client.bgfx.camera.PerspectiveCamera
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem
import org.joml.Vector3f

class CameraSystem : NonProcessingSystem() {

    private val vec3a = Vector3f()
    val camera: PerspectiveCamera = PerspectiveCamera(zNear = 0.01f, zFar = 10f).apply {
        setFovDeg(45f)
        setPosition(vec3a.set(2f, 0f, 0f))
        lookAt(vec3a.set(0f, 0f, 0f))
    }

    override fun dispose() {
        camera.dispose()
    }
}