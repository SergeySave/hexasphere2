package com.sergeysav.hexasphere.common.game.tile.feature

import com.artemis.ComponentMapper
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class TileFeatureSystem : NonProcessingSystem() {

    private lateinit var cityMapper: ComponentMapper<CityFeatureComponent>

    fun getFeatureCount(tileEntity: Int): Int {
        return if (cityMapper[tileEntity] != null) 0 else 1
    }
}