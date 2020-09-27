package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.BaseSystem
import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.EntityTransmuter
import com.artemis.EntityTransmuterFactory
import com.artemis.managers.GroupManager
import com.artemis.utils.Bag
import com.sergeysav.hexasphere.common.game.Groups

class TileTypeSystem : BaseSystem() {

    private lateinit var groupManager: GroupManager
    private lateinit var oceanMapper: ComponentMapper<OceanTileTypeComponent>
    private lateinit var coastMapper: ComponentMapper<CoastTileTypeComponent>

    private lateinit var mappers: Bag<ComponentMapper<out Component>>
    private lateinit var transmuters: Map<Class<out Component>, EntityTransmuter>

    private fun createBaseFactory() = EntityTransmuterFactory(world)
        .remove(OceanTileTypeComponent::class.java)
        .remove(CoastTileTypeComponent::class.java)

    override fun initialize() {
        super.initialize()

        this.mappers = Bag<ComponentMapper<out Component>>().apply {
            add(oceanMapper)
            add(coastMapper)
        }

        this.transmuters = mutableMapOf<Class<out Component>, EntityTransmuter>().apply {
            this[OceanTileTypeComponent::class.java] = createBaseFactory().add(OceanTileTypeComponent::class.java).build()
            this[CoastTileTypeComponent::class.java] = createBaseFactory().add(CoastTileTypeComponent::class.java).build()
        }
    }

    fun getTileType(tileEntity: Int): TileType? {
        for (mapper in mappers) {
            val component = mapper.get(tileEntity)
            if (component != null) {
                return component as TileType
            }
        }
        return null
    }

    fun setTileType(tileEntity: Int, type: Class<out Component>) {
        transmuters[type]?.transmute(tileEntity)
        groupManager.add(tileEntity, Groups.DIRTY_TILE)
    }

    override fun processSystem() {
        isEnabled = false
    }
}