package com.sergeysav.hexasphere.client.window.screen

object NullScreen : Screen {
    override fun create() { error("Cannot Create Null Screen") }

    override fun resume() { }

    override fun render(delta: Double, width: Int, height: Int) = ScreenAction.pop()

    override fun pause() { }

    override fun dispose() { }
}