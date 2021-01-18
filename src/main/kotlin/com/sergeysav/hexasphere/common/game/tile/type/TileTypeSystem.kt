package com.sergeysav.hexasphere.common.game.tile.type

import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.EntityTransmuter
import com.artemis.EntityTransmuterFactory
import com.artemis.managers.GroupManager
import com.artemis.utils.Bag
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem
import com.sergeysav.hexasphere.common.game.Groups

class TileTypeSystem : NonProcessingSystem() {

    private lateinit var groupManager: GroupManager
    private lateinit var mappers: Bag<ComponentMapper<out Component>>
    private lateinit var transmuters: Map<Class<out Component>, EntityTransmuter>

    // ADD NEW TILE TYPES HERE
    private fun createBaseFactory() = EntityTransmuterFactory(world)
        .remove(OceanTileTypeComponent::class.java)
        .remove(CoastTileTypeComponent::class.java)
        .remove(GrasslandTileTypeComponent::class.java)

    override fun initialize() {
        super.initialize()
        this.mappers = Bag<ComponentMapper<out Component>>().also { bag ->
            this.transmuters = mutableMapOf<Class<out Component>, EntityTransmuter>().also { map ->
                // ADD NEW TILE TYPES HERE
                add<OceanTileTypeComponent>(bag, map)
                add<CoastTileTypeComponent>(bag, map)
                add<GrasslandTileTypeComponent>(bag, map)
            }
        }
    }

    private inline fun <reified T : Component> MutableMap<Class<out Component>, EntityTransmuter>.addTransmuter() {
        this[T::class.java] = createBaseFactory().add(T::class.java).build()
    }

    private inline fun <reified T : Component> Bag<ComponentMapper<out Component>>.addMapper() {
        add(world.getMapper(T::class.java))
    }

    private inline fun <reified T : Component> add(bag: Bag<ComponentMapper<out Component>>, map: MutableMap<Class<out Component>, EntityTransmuter>) {
        bag.addMapper<T>()
        map.addTransmuter<T>()
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
}

inline fun <reified T : Component> TileTypeSystem.setType(tileEntity: Int) = setTileType(tileEntity, T::class.java)
