package com.sergeysav.hexasphere.client.window.screen

sealed class ScreenAction {
    object NoOp : ScreenAction()
    object Pop : ScreenAction()
    data class Push(var newScreen: Screen) : ScreenAction()
    data class Replace(var newScreen: Screen) : ScreenAction()

    companion object {
        private val push = Push(NullScreen)
        private val replace = Replace(NullScreen)

        fun noop(): ScreenAction = NoOp
        fun pop(): ScreenAction = Pop
        fun push(screen: Screen): ScreenAction {
            this.push.newScreen = screen
            return this.push
        }
        fun replace(screen: Screen): ScreenAction {
            this.replace.newScreen = screen
            return this.replace
        }
    }
}