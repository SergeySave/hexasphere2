package com.sergeysav.hexasphere.client.game.camera

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.bgfx.camera.PerspectiveCamera
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import org.joml.Vector3f
import kotlin.math.pow

class CameraSystem : BaseSystem() {

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    val camera: PerspectiveCamera = PerspectiveCamera(zNear = 0.1f, zFar = 100f).apply {
        setFovDeg(45f)
        setPosition(vec3a.set(2f, 0f, 0f))
        lookAt(vec3a.set(0f, 0f, 0f))
    }

    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var inputManager: InputManagerSystem
    private lateinit var settingsSystem: SettingsSystem

    override fun processSystem() {
        val delta = world.delta
        camera.run {
            val speed = (settingsSystem.uiSettings.cameraSpeedMultiplier * 60 * delta * 0.1f * (position.length().toDouble() / 5).pow(1.5)).toFloat()

            setAspect(renderDataSystem.width.toFloat(), renderDataSystem.height.toFloat())
            translateIn(forward, speed * (inputManager.getKeyDownInt(settingsSystem.uiSettings.outKey) -
                    inputManager.getKeyDownInt(settingsSystem.uiSettings.inKey)))
            rotateAround(vec3a.zero(), right, -speed * (inputManager.getKeyDownInt(settingsSystem.uiSettings.upKey) -
                    inputManager.getKeyDownInt(settingsSystem.uiSettings.downKey)))
            rotateAround(vec3a.zero(), up, speed * (inputManager.getKeyDownInt(settingsSystem.uiSettings.rightKey) -
                    inputManager.getKeyDownInt(settingsSystem.uiSettings.leftKey)))
            rotate(forward, (5 * delta) * (inputManager.getKeyDownInt(settingsSystem.uiSettings.clockwiseKey) -
                    inputManager.getKeyDownInt(settingsSystem.uiSettings.counterclockwiseKey)))

            translateIn(forward, 3f * speed * inputManager.getScrollY().toFloat())

            if (inputManager.isMouseButtonDown(settingsSystem.uiSettings.dragMouseButton) &&
                !inputManager.isMouseButtonJustDown(settingsSystem.uiSettings.dragMouseButton)) {
                up.mul(inputManager.getMouseDX().toFloat(), vec3a)
                right.mul(inputManager.getMouseDY().toFloat(), vec3b)
                vec3a.add(vec3b) // vec3a should be the rotation axis
                if (vec3a.lengthSquared() > 1e-4) {
                    rotateAround(vec3b.zero(), vec3a, -speed * 0.075f * settingsSystem.uiSettings.cameraDragMultiplier.toFloat())
                }
            }

            val minLen = 1.1f
            if (position.lengthSquared() < minLen * minLen) {
                position.normalize(minLen)
            }
            if (position.lengthSquared() > 8 * 8) {
                position.normalize(8f)
            }

            update(renderDataSystem.hexasphereView)
            update(renderDataSystem.skyboxView, true)
            update(renderDataSystem.featuresView)
        }
    }

    override fun dispose() {
        camera.dispose()
    }
}