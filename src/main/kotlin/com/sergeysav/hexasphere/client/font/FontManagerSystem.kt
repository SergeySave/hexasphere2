package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.common.ecs.NonProcessingSystem

class FontManagerSystem private constructor(private val fontManager: FontManager = FontManager()) : NonProcessingSystem(), IFontManager by fontManager {

    constructor() : this(FontManager())

    override fun dispose() {
        fontManager.dispose()
    }
}