package com.sergeysav.hexasphere.client.settings

import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.MouseButton

class MutableUISettings : UISettings {
    override val showDebugInfo: Boolean = true
    override val uiScale: Double = 2.0
    override val mouseHoldCutoffTime: Double = 0.15

    override val selectTileMouseButton: MouseButton = MouseButton.LEFT
    override val switchRenderModeKey: Key = Key.R
    override val outKey: Key = Key.SPACE
    override val inKey: Key = Key.LEFT_SHIFT
    override val upKey: Key = Key.W
    override val downKey: Key = Key.S
    override val rightKey: Key = Key.D
    override val leftKey: Key = Key.A
    override val clockwiseKey: Key = Key.E
    override val counterclockwiseKey: Key = Key.Q
    override val dragMouseButton: MouseButton = MouseButton.LEFT
    override val cameraSpeedMultiplier: Double = 1.0
    override val cameraDragMultiplier: Double = 1.0
}