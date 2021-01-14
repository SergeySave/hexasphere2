package com.sergeysav.hexasphere.client.settings

import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class SettingsSystem : NonProcessingSystem() {

    val uiSettings: UISettings = MutableUISettings()
}