package com.sergeysav.hexasphere.common.game.tile.feature

import com.artemis.PooledComponent

class CityFeatureComponent : PooledComponent(), TileFeature {

    override var featureNumber: Int = 0
    override val unlocalizedName: String = "feature.city.name"

    override fun reset() {
        featureNumber = 0
    }
}