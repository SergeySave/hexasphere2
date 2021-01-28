package com.sergeysav.hexasphere.common.ecs

object SystemPriority {
    const val SETUP = Int.MAX_VALUE

    const val UI = 200

    const val CONTROLS = 100
    
    const val PRE_RENDER = 50

    const val DEFAULT = 0
    const val NON_PROCESSING = DEFAULT

    const val RENDER = -100
    
    const val LAST_UI = RENDER - 1

    const val CLEANUP = Int.MIN_VALUE
}