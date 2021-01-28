package com.sergeysav.hexasphere.common.game.tile.feature

import com.artemis.ComponentMapper
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class TileFeatureSystem : NonProcessingSystem() {

    private lateinit var cityMapper: ComponentMapper<CityFeatureComponent>

    fun getFeatureCount(tileEntity: Int): Int {
        return if (cityMapper[tileEntity] != null) 0 else 1
    }

    fun getTileFeatures(tileEntity: Int, featureOutput: Array<TileFeature?>): Int {
        var amount = 0

        featureOutput.fill(null, 0, TileFeature.MAX_FEATURES_PER_TILE)

        cityMapper[tileEntity]?.also {
            featureOutput[it.featureNumber] = it
            amount++
        }

        return amount
    }
}