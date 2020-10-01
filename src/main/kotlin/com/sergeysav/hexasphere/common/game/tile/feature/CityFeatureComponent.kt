package com.sergeysav.hexasphere.common.game.tile.feature

import com.artemis.PooledComponent

class CityFeatureComponent : PooledComponent(), TileFeature {

    var featureNumber: Int = 0

    override fun reset() {
        featureNumber = 0
    }
}