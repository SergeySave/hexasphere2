package com.sergeysav.hexasphere.client.screen

import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import mu.KotlinLogging

class LoadingScreen : Screen {

    private val logger = KotlinLogging.logger {  }

    override fun create() {
        logger.info { "Create" }
    }

    override fun resume() {
        logger.info { "Resume" }
    }

    override fun render(delta: Double, width: Int, height: Int): ScreenAction {
        return ScreenAction.replace(HSScreen())
    }

    override fun pause() {
        logger.info { "Pause" }
    }

    override fun dispose() {
        logger.info { "Dispose" }
    }
}