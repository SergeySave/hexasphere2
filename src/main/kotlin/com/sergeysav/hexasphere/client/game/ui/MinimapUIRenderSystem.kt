package com.sergeysav.hexasphere.client.game.ui

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.render.DebugRender
import com.sergeysav.hexasphere.common.color
import org.joml.Vector3f

class MinimapUIRenderSystem : BaseSystem() {

    private lateinit var renderDataSystem: RenderDataSystem

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val vec3c = Vector3f()
    private val vec3d = Vector3f()

    override fun processSystem() {
        val width = renderDataSystem.width.toFloat() / 4
        val height = renderDataSystem.height.toFloat() / 3

        Encoder.with {
            setState(Encoder.UI)
            DebugRender.fillQuad(
                this,
                renderDataSystem.uiView,
                vec3a.set(0f, 0f, 0f),
                vec3b.set(0f, height, 0f),
                vec3c.set(width, height, 0f),
                vec3d.set(width, 0f, 0f),
                color(0x55, 0x55, 0x55, 0xFF)
            )
        }
    }
}