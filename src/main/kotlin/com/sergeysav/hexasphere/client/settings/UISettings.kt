package com.sergeysav.hexasphere.client.settings

import com.sergeysav.hexasphere.client.input.MouseButton

interface UISettings {

    val showDebugInfo: Boolean

    val uiScale: Double

    val mouseHoldCutoffTime: Double

    val selectTileMouseButton: MouseButton
}