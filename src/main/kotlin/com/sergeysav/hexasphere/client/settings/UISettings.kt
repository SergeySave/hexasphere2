package com.sergeysav.hexasphere.client.settings

import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.MouseButton

interface UISettings {

    val showDebugInfo: Boolean

    val uiScale: Double

    val mouseHoldCutoffTime: Double

    val selectTileMouseButton: MouseButton

    val switchRenderModeKey: Key
    val outKey: Key
    val inKey: Key
    val upKey: Key
    val downKey: Key
    val rightKey: Key
    val leftKey: Key
    val clockwiseKey: Key
    val counterclockwiseKey: Key
    val dragMouseButton: MouseButton
    val cameraSpeedMultiplier: Double
    val cameraDragMultiplier: Double
}