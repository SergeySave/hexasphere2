package com.sergeysav.hexasphere.common.ecs

object SystemPriority {
    const val SETUP = Int.MAX_VALUE

    const val INPUTS = 100

    const val DEFAULT = 0
    const val NON_PROCESSING = DEFAULT

    const val RENDER = -100
    const val UI = RENDER

    const val CLEANUP = Int.MIN_VALUE
}