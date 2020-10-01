package com.sergeysav.hexasphere.common.game.tile.feature

import com.artemis.BaseSystem
import com.artemis.ComponentMapper

class TileFeatureSystem : BaseSystem() {

    private lateinit var cityMapper: ComponentMapper<CityFeatureComponent>

    fun getFeatureCount(tileEntity: Int): Int {
        return if (cityMapper[tileEntity] != null) 0 else 1
    }

    override fun processSystem() {
        isEnabled = false
    }
}