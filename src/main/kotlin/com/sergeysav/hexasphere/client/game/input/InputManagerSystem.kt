package com.sergeysav.hexasphere.client.game.input

import com.sergeysav.hexasphere.client.input.manager.InputManager
import com.sergeysav.hexasphere.client.input.manager.impl.InputManagerImpl
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class InputManagerSystem : NonProcessingSystem(), InputManager by InputManagerImpl()
