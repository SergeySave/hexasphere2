package com.sergeysav.hexasphere.client.settings

import com.sergeysav.hexasphere.client.input.MouseButton

class MutableUISettings : UISettings {
    override val showDebugInfo: Boolean = true
    override val uiScale: Double = 2.0
    override val mouseHoldCutoffTime: Double = 0.1

    override val selectTileMouseButton: MouseButton = MouseButton.LEFT
}