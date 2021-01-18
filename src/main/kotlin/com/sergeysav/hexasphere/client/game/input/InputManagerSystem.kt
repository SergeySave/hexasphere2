package com.sergeysav.hexasphere.client.game.input

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.input.manager.InputManager
import com.sergeysav.hexasphere.client.input.manager.impl.InputManagerImpl

class InputManagerSystem : BaseSystem(), InputManager by InputManagerImpl() {
    override fun processSystem() {
        this.update(world.delta.toDouble())
    }
}
