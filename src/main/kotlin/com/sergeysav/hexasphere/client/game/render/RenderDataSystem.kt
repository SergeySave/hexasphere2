package com.sergeysav.hexasphere.client.game.render

import com.sergeysav.hexasphere.client.bgfx.frame.Framebuffer
import com.sergeysav.hexasphere.client.bgfx.view.View
import com.sergeysav.hexasphere.client.bgfx.view.ViewArray
import com.sergeysav.hexasphere.client.bgfx.view.set
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class RenderDataSystem : NonProcessingSystem() {

    val hexasphereView = View(0)
    val skyboxView = View(1)
    val featuresView = View(2)
    val uiView = View(3)
    val views: ViewArray = intArrayOf(hexasphereView.id, featuresView.id, skyboxView.id, uiView.id)

    var width: Double = 0.0
        private set
    var height: Double = 0.0
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
}