package com.sergeysav.hexasphere.common.ecs

import com.artemis.BaseSystem

fun BaseSystem.flipEnabled() {
    this.isEnabled = !this.isEnabled
}
