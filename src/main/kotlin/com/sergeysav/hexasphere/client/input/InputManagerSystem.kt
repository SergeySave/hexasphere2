package com.sergeysav.hexasphere.client.input

import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class InputManagerSystem : NonProcessingSystem(), InputManager by InputManagerImpl()
