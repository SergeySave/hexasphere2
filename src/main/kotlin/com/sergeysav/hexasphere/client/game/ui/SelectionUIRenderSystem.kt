package com.sergeysav.hexasphere.client.game.ui

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.game.font.FontManagerSystem
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.client.localization.L10n
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.client.ui.UIManager
import com.sergeysav.hexasphere.common.color.Color
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeature
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeatureSystem
import com.sergeysav.hexasphere.common.game.tile.type.CoastTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.GrasslandTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.OceanTileTypeComponent
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem

class SelectionUIRenderSystem : BaseSystem() {

    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var selectionSystem: SelectionSystem
    private lateinit var inputManager: InputManagerSystem
    private lateinit var fontManagerSystem: FontManagerSystem
    private lateinit var settingsSystem: SettingsSystem
    private lateinit var tileTypeSystem: TileTypeSystem
    private lateinit var tileFeatureSystem: TileFeatureSystem

    private val features = Array<TileFeature?>(TileFeature.MAX_FEATURES_PER_TILE) { null }

    private var lastPosition = 0f
    private var displayTile = -1

    override fun processSystem() {
        val selectedTile = selectionSystem.selectedTile
        val position = if (selectedTile != -1) {
            displayTile = selectedTile
            minOf(lastPosition + ANIMATION_SPEED, 1f)
        } else {
            maxOf(lastPosition - ANIMATION_SPEED, 0f)
        }

        UIManager.start(
            renderDataSystem.width,
            renderDataSystem.height,
            inputManager,
            fontManagerSystem,
            settingsSystem.uiSettings,
            renderDataSystem.uiView
        ) {
            fontScale = 0.3
            window {
                setBox(renderDataSystem.width - position * renderDataSystem.width / 4.0, 0.0, renderDataSystem.width / 4.0, renderDataSystem.height)
                consumeMouseEvents = true

                fill(Color.WHITE.alpha(0.25f))
                addBorder(fontManager.font.getSpaceWidth())

                if (displayTile != -1) {
                    val tileType = tileTypeSystem.getTileType(displayTile)
                    val featureCount = tileFeatureSystem.getTileFeatures(displayTile, features)

                    vertStack {
                        if (tileType != null) {
                            label(
                                String.format(L10n["ui.selection.selected_tile.format"], L10n[tileType.unlocalizedName]),
                                Color.WHITE, Color.BLACK, 0.2, 0.1, 1.2
                            )
                        }
                        if (featureCount == 0) {
                            label(L10n["ui.selection.no_features.text"], Color.WHITE, Color.BLACK, 0.2, 0.1)
                        }
                        for (f in 0 until featureCount) {
                            label(
                                String.format(L10n["ui.selection.feature_present.format"], L10n[features[f]!!.unlocalizedName]),
                                Color.WHITE, Color.BLACK, 0.2, 0.1
                            )
                        }
                        skip(4.0)
                        for (type in arrayOf(GrasslandTileTypeComponent(), CoastTileTypeComponent(), OceanTileTypeComponent())) {
                            if (labelButton(
                                String.format(L10n["ui.selection.set_type.format"], L10n[type.unlocalizedName]),
                                Color.WHITE, (Color.WHITE * 0.7f).alpha(255), Color.BLACK, 0.2, 0.1
                            )) {
                                tileTypeSystem.setTileType(displayTile, type.javaClass)
                            }
                        }
                    }
                }
            }
        }

        lastPosition = position
    }

    companion object {
        private const val ANIMATION_SPEED = 0.05f
    }
}