package com.sergeysav.hexasphere

import com.sergeysav.hexasphere.client.screen.LoadingScreen
import com.sergeysav.hexasphere.client.window.Window
import com.sergeysav.hexasphere.client.window.WindowOptions

fun main(args: Array<String>) {
    System.setProperty("org.lwjgl.util.Debug","true")
    Window(1920, 1080, "Hexasphere 2", WindowOptions.parseFromArgs(args), LoadingScreen()).run()
}
