package com.sergeysav.hexasphere.common.ecs

import com.artemis.BaseSystem

abstract class NonProcessingSystem : BaseSystem() {

    override fun begin() {
        isEnabled = false
    }

    override fun processSystem() {}
}