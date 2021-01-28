package com.sergeysav.hexasphere.client.game.ui

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.font.align.HAlign
import com.sergeysav.hexasphere.client.font.align.VAlign
import com.sergeysav.hexasphere.client.font.manager.render
import com.sergeysav.hexasphere.client.game.font.FontManagerSystem
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.localization.L10n
import com.sergeysav.hexasphere.client.render.DebugRender
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.common.color.Color
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeature
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeatureSystem
import com.sergeysav.hexasphere.common.game.tile.type.CoastTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.GrasslandTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.OceanTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import com.sergeysav.hexasphere.common.setColor
import org.joml.Vector3f

class SelectionUIRenderSystem : BaseSystem() {

    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var selectionSystem: SelectionSystem
    private lateinit var inputManager: InputManagerSystem
    private lateinit var fontManagerSystem: FontManagerSystem
    private lateinit var settingsSystem: SettingsSystem
    private lateinit var tileTypeSystem: TileTypeSystem
    private lateinit var tileFeatureSystem: TileFeatureSystem

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val vec3c = Vector3f()
    private val vec3d = Vector3f()
    private val features = Array<TileFeature?>(TileFeature.MAX_FEATURES_PER_TILE) { null }

    private var lastPosition = 0f
    private var displayTile = -1

    override fun processSystem() {
        val width = renderDataSystem.width.toFloat()
        val height = renderDataSystem.height.toFloat()

        val mouseY = height - inputManager.getMouseY()

        val selectedTile = selectionSystem.selectedTile
        val position = if (selectedTile != -1) {
            displayTile = selectedTile
            minOf(lastPosition + ANIMATION_SPEED, 1f)
        } else {
            maxOf(lastPosition - ANIMATION_SPEED, 0f)
        }

        val x = width - position * width / 4

        Encoder.with {
            setState(Encoder.UI)
            DebugRender.fillQuad(
                this,
                renderDataSystem.uiView,
                vec3a.set(x, 0f, 0f),
                vec3b.set(x, height, 0f),
                vec3c.set(width, height, 0f),
                vec3d.set(width, 0f, 0f),
                Color.WHITE.alpha(0.25f)
            )
        }

        if (displayTile != -1) {
            val tileType = tileTypeSystem.getTileType(displayTile)
            val featureCount = tileFeatureSystem.getTileFeatures(displayTile, features)

            fontManagerSystem.render { encoder, _ ->
                val fontScale = (0.3 * settingsSystem.uiSettings.uiScale).toFloat()
                var y = height.toDouble()

                if (tileType != null) {
                    y -= font.getLineStep() * fontScale * 1.2f
                    encoder.drawFont(
                        String.format(L10n["ui.selection.selected_tile.format"], L10n[tileType.unlocalizedName]),
                        x + font.getSpaceWidth() * fontScale, y,
                        renderDataSystem.uiView,
                        fontScale * 1.2f,
                        color.setColor(255, 255, 255),
                        outlineColor.setColor(0, 0, 0),
                        HAlign.LEFT,
                        VAlign.TOP,
                        outlineThickness = 0.2f,
                        thickness = 0.1f
                    )
                }

                if (featureCount == 0) {
                    y -= font.getLineStep() * fontScale
                    encoder.drawFont(
                        L10n["ui.selection.no_features.text"],
                        x + font.getSpaceWidth() * fontScale, y,
                        renderDataSystem.uiView,
                        fontScale,
                        color.setColor(255, 255, 255),
                        outlineColor.setColor(0, 0, 0),
                        HAlign.LEFT,
                        VAlign.TOP,
                        outlineThickness = 0.2f,
                        thickness = 0.1f
                    )
                }
                for (f in 0 until featureCount) {
                    y -= font.getLineStep() * fontScale
                    encoder.drawFont(
                        String.format(L10n["ui.selection.feature_present.format"], L10n[features[f]!!.unlocalizedName]),
                        x + font.getSpaceWidth() * fontScale, y,
                        renderDataSystem.uiView,
                        fontScale,
                        color.setColor(255, 255, 255),
                        outlineColor.setColor(0, 0, 0),
                        HAlign.LEFT,
                        VAlign.TOP,
                        outlineThickness = 0.2f,
                        thickness = 0.1f
                    )
                }

                y -= font.getLineStep() * fontScale * 4
                for (type in arrayOf(GrasslandTileTypeComponent(), CoastTileTypeComponent(), OceanTileTypeComponent())) {
                    y -= font.getLineStep() * fontScale

                    val isMouseover = inputManager.getMouseX() > position && mouseY > y && mouseY < y + font.getLineStep() * fontScale

                    encoder.drawFont(
                        String.format(L10n["ui.selection.set_type.format"], L10n[type.unlocalizedName]),
                        x + font.getSpaceWidth() * fontScale, y,
                        renderDataSystem.uiView,
                        fontScale,
                        if (isMouseover) {
                            color.setColor(180, 180, 180)
                        } else {
                            color.setColor(255, 255, 255)
                        },
                        outlineColor.setColor(0, 0, 0),
                        HAlign.LEFT,
                        VAlign.TOP,
                        outlineThickness = 0.2f,
                        thickness = 0.1f
                    )

                    if (isMouseover && inputManager.isMouseButtonJustUp(MouseButton.LEFT)) {
                        tileTypeSystem.setTileType(displayTile, type.javaClass)
                    }
                }
            }
        }

        // Prevent other mouse events from firing when this is happening
        if (inputManager.getMouseX() > x) {
            inputManager.consumeMouseEvents()
        }

        lastPosition = position
    }

    companion object {
        private const val ANIMATION_SPEED = 0.05f
    }
}