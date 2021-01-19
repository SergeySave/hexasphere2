package com.sergeysav.hexasphere.client.game.font

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.font.manager.FontManager
import com.sergeysav.hexasphere.client.font.manager.impl.FontManagerImpl
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem

class FontManagerSystem private constructor(private val fontManager: FontManager = FontManagerImpl()) : BaseSystem(), FontManager by fontManager {

    constructor() : this(FontManagerImpl())

    private lateinit var renderDataSystem: RenderDataSystem

    override fun processSystem() {
        updateCamera(renderDataSystem.width, renderDataSystem.height, renderDataSystem.uiView)
    }

    override fun dispose() {
        fontManager.dispose()
    }
}