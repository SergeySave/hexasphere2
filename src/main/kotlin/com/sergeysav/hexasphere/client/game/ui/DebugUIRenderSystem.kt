package com.sergeysav.hexasphere.client.game.ui

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.font.align.HAlign
import com.sergeysav.hexasphere.client.font.align.VAlign
import com.sergeysav.hexasphere.client.font.manager.render
import com.sergeysav.hexasphere.client.game.font.FontManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.localization.L10n
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.common.setColor

class DebugUIRenderSystem : BaseSystem() {

    private lateinit var settingsSystem: SettingsSystem
    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var fontManagerSystem: FontManagerSystem

    private val fpsSmoothing = FloatArray(20)
    private var fpsIndex = 0

    override fun processSystem() {
        fpsSmoothing[fpsIndex++] = 1f/ world.delta
        fpsIndex %= fpsSmoothing.size

        if (!settingsSystem.uiSettings.showDebugInfo) return

        val fps = fpsSmoothing.average()
        fontManagerSystem.render(renderDataSystem.width, renderDataSystem.height, renderDataSystem.uiView) { encoder, _ ->
            val fontScale = (0.3 * settingsSystem.uiSettings.uiScale).toFloat()

            encoder.drawFont(
                buildString {
                    append(String.format(L10n("ui.debug.fps.format"), fps))
                }, 0.0, renderDataSystem.height - font.getLineStep() * fontScale * 1, renderDataSystem.uiView, fontScale,
                color.setColor(0xFF, 0xFF, 0xFF),
                outlineColor.setColor(0x00, 0x00, 0x00),
                hAlign = HAlign.LEFT, vAlign = VAlign.TOP, outlineThickness = 0.2f, thickness = 0.75f
            )
        }
    }
}