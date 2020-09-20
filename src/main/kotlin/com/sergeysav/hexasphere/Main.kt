package com.sergeysav.hexasphere

import com.sergeysav.hexasphere.client.screen.LoadingScreen
import com.sergeysav.hexasphere.client.window.Window
import com.sergeysav.hexasphere.client.window.WindowOptions

fun main(args: Array<String>) {
    Window(800, 600, "Hexasphere 2", WindowOptions.parseFromArgs(args), LoadingScreen()).run()
}
