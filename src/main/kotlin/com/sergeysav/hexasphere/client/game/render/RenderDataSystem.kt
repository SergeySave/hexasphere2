package com.sergeysav.hexasphere.client.game.render

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.bgfx.frame.Framebuffer
import com.sergeysav.hexasphere.client.bgfx.shader.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.view.View
import com.sergeysav.hexasphere.client.bgfx.view.ViewArray
import com.sergeysav.hexasphere.client.bgfx.view.set
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.settings.SettingsSystem

class RenderDataSystem : BaseSystem() {

    private lateinit var inputManager: InputManagerSystem
    private lateinit var settingsSystem: SettingsSystem
    private val hexDefaultShader = ShaderProgram.loadFromFiles("hexasphere/vs_default", "hexasphere/fs_default")
    private val hexStereoShader = ShaderProgram.loadFromFiles("hexasphere/vs_stereographic", "hexasphere/fs_default")
    private val featDefaultShader = ShaderProgram.loadFromFiles("feature/vs_default", "feature/fs_default")
    private val featStereoShader = ShaderProgram.loadFromFiles("feature/vs_stereographic", "feature/fs_default")

    val hexasphereView = View(0)
    val skyboxView = View(1)
    val featuresView = View(2)
    val uiView = View(3)
    val views: ViewArray = intArrayOf(hexasphereView.id, featuresView.id, skyboxView.id, uiView.id)

    var width: Double = 0.0
        private set
    var height: Double = 0.0
        private set
    var hexasphereShader = hexDefaultShader
        private set
    var featureShader = featDefaultShader
        private set
    var mode = RenderMode.DEFAULT
        private set

    fun initializeFrame(width: Double, height: Double) {
        this.width = width
        this.height = height

        View.touch() // Touch view 0 to make sure everything renders
        hexasphereView.set("Hexasphere", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT, View.ViewMode.ASCENDING)
        skyboxView.set("Skybox", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT, View.ViewMode.ASCENDING)
        featuresView.set("Features", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT, View.ViewMode.ASCENDING)
        uiView.set("UI", View.BackbufferRatio.EQUAL, Framebuffer.DEFAULT, View.ViewMode.SEQUENTIAL)
        View.setViewOrder(views)
    }

    override fun processSystem() {
        if (inputManager.isKeyJustUp(settingsSystem.uiSettings.switchRenderModeKey)) {
            mode = RenderMode.VALUES[(mode.ordinal + 1) % RenderMode.VALUES.size]
            if (mode == RenderMode.DEFAULT) {
                hexasphereShader = hexDefaultShader
                featureShader = featDefaultShader
            } else {
                hexasphereShader = hexStereoShader
                featureShader = featStereoShader
            }
        }
    }

    override fun dispose() {
        hexDefaultShader.dispose()
        hexStereoShader.dispose()
        featDefaultShader.dispose()
        featStereoShader.dispose()
    }

    enum class RenderMode {
        DEFAULT,
        STEREOGRAPHIC;

        companion object {
            val VALUES = values().toList()
        }
    }
}