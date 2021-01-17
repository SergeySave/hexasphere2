package com.sergeysav.hexasphere.client.game.font

import com.sergeysav.hexasphere.client.font.manager.FontManager
import com.sergeysav.hexasphere.client.font.manager.impl.FontManagerImpl
import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class FontManagerSystem private constructor(private val fontManager: FontManager = FontManagerImpl()) : NonProcessingSystem(), FontManager by fontManager {

    constructor() : this(FontManagerImpl())

    override fun dispose() {
        fontManager.dispose()
    }
}