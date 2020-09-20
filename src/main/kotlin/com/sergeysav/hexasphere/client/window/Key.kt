package com.sergeysav.hexasphere.client.window

import org.lwjgl.glfw.GLFW

enum class Key {
    UNKNOWN,
    SPACE,
    APOSTROPHE,
    COMMA,
    MINUS,
    PERIOD,
    SLASH,
    KEY_0,
    KEY_1,
    KEY_2,
    KEY_3,
    KEY_4,
    KEY_5,
    KEY_6,
    KEY_7,
    KEY_8,
    KEY_9,
    SEMICOLON,
    EQUAL,
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    LEFT_BRACKET,
    BACKSLASH,
    RIGHT_BRACKET,
    GRAVE_ACCENT,
    WORLD_1,
    WORLD_2,
    ESCAPE,
    ENTER,
    TAB,
    BACKSPACE,
    INSERT,
    DELETE,
    RIGHT,
    LEFT,
    DOWN,
    UP,
    PAGE_UP,
    PAGE_DOWN,
    HOME,
    END,
    CAPS_LOCK,
    SCROLL_LOCK,
    NUM_LOCK,
    PRINT_SCREEN,
    PAUSE,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    F13,
    F14,
    F15,
    F16,
    F17,
    F18,
    F19,
    F20,
    F21,
    F22,
    F23,
    F24,
    F25,
    KP_0,
    KP_1,
    KP_2,
    KP_3,
    KP_4,
    KP_5,
    KP_6,
    KP_7,
    KP_8,
    KP_9,
    KP_DECIMAL,
    KP_DIVIDE,
    KP_MULTIPLY,
    KP_SUBTRACT,
    KP_ADD,
    KP_ENTER,
    KP_EQUAL,
    LEFT_SHIFT,
    LEFT_CONTROL,
    LEFT_ALT,
    LEFT_SUPER,
    RIGHT_SHIFT,
    RIGHT_CONTROL,
    RIGHT_ALT,
    RIGHT_SUPER,
    MENU;

    companion object {
        fun fromGLFWCode(code: Int) = when (code) {
            GLFW.GLFW_KEY_SPACE -> SPACE
            GLFW.GLFW_KEY_APOSTROPHE -> APOSTROPHE
            GLFW.GLFW_KEY_COMMA -> COMMA
            GLFW.GLFW_KEY_MINUS -> MINUS
            GLFW.GLFW_KEY_PERIOD -> PERIOD
            GLFW.GLFW_KEY_SLASH -> SLASH
            GLFW.GLFW_KEY_0 -> KEY_0
            GLFW.GLFW_KEY_1 -> KEY_1
            GLFW.GLFW_KEY_2 -> KEY_2
            GLFW.GLFW_KEY_3 -> KEY_3
            GLFW.GLFW_KEY_4 -> KEY_4
            GLFW.GLFW_KEY_5 -> KEY_5
            GLFW.GLFW_KEY_6 -> KEY_6
            GLFW.GLFW_KEY_7 -> KEY_7
            GLFW.GLFW_KEY_8 -> KEY_8
            GLFW.GLFW_KEY_9 -> KEY_9
            GLFW.GLFW_KEY_SEMICOLON -> SEMICOLON
            GLFW.GLFW_KEY_EQUAL -> EQUAL
            GLFW.GLFW_KEY_A -> A
            GLFW.GLFW_KEY_B -> B
            GLFW.GLFW_KEY_C -> C
            GLFW.GLFW_KEY_D -> D
            GLFW.GLFW_KEY_E -> E
            GLFW.GLFW_KEY_F -> F
            GLFW.GLFW_KEY_G -> G
            GLFW.GLFW_KEY_H -> H
            GLFW.GLFW_KEY_I -> I
            GLFW.GLFW_KEY_J -> J
            GLFW.GLFW_KEY_K -> K
            GLFW.GLFW_KEY_L -> L
            GLFW.GLFW_KEY_M -> M
            GLFW.GLFW_KEY_N -> N
            GLFW.GLFW_KEY_O -> O
            GLFW.GLFW_KEY_P -> P
            GLFW.GLFW_KEY_Q -> Q
            GLFW.GLFW_KEY_R -> R
            GLFW.GLFW_KEY_S -> S
            GLFW.GLFW_KEY_T -> T
            GLFW.GLFW_KEY_U -> U
            GLFW.GLFW_KEY_V -> V
            GLFW.GLFW_KEY_W -> W
            GLFW.GLFW_KEY_X -> X
            GLFW.GLFW_KEY_Y -> Y
            GLFW.GLFW_KEY_Z -> Z
            GLFW.GLFW_KEY_LEFT_BRACKET -> LEFT_BRACKET
            GLFW.GLFW_KEY_BACKSLASH -> BACKSLASH
            GLFW.GLFW_KEY_RIGHT_BRACKET -> RIGHT_BRACKET
            GLFW.GLFW_KEY_GRAVE_ACCENT -> GRAVE_ACCENT
            GLFW.GLFW_KEY_WORLD_1 -> WORLD_1
            GLFW.GLFW_KEY_WORLD_2 -> WORLD_2
            GLFW.GLFW_KEY_ESCAPE -> ESCAPE
            GLFW.GLFW_KEY_ENTER -> ENTER
            GLFW.GLFW_KEY_TAB -> TAB
            GLFW.GLFW_KEY_BACKSPACE -> BACKSPACE
            GLFW.GLFW_KEY_INSERT -> INSERT
            GLFW.GLFW_KEY_DELETE -> DELETE
            GLFW.GLFW_KEY_RIGHT -> RIGHT
            GLFW.GLFW_KEY_LEFT -> LEFT
            GLFW.GLFW_KEY_DOWN -> DOWN
            GLFW.GLFW_KEY_UP -> UP
            GLFW.GLFW_KEY_PAGE_UP -> PAGE_UP
            GLFW.GLFW_KEY_PAGE_DOWN -> PAGE_DOWN
            GLFW.GLFW_KEY_HOME -> HOME
            GLFW.GLFW_KEY_END -> END
            GLFW.GLFW_KEY_CAPS_LOCK -> CAPS_LOCK
            GLFW.GLFW_KEY_SCROLL_LOCK -> SCROLL_LOCK
            GLFW.GLFW_KEY_NUM_LOCK -> NUM_LOCK
            GLFW.GLFW_KEY_PRINT_SCREEN -> PRINT_SCREEN
            GLFW.GLFW_KEY_PAUSE -> PAUSE
            GLFW.GLFW_KEY_F1 -> F1
            GLFW.GLFW_KEY_F2 -> F2
            GLFW.GLFW_KEY_F3 -> F3
            GLFW.GLFW_KEY_F4 -> F4
            GLFW.GLFW_KEY_F5 -> F5
            GLFW.GLFW_KEY_F6 -> F6
            GLFW.GLFW_KEY_F7 -> F7
            GLFW.GLFW_KEY_F8 -> F8
            GLFW.GLFW_KEY_F9 -> F9
            GLFW.GLFW_KEY_F10 -> F10
            GLFW.GLFW_KEY_F11 -> F11
            GLFW.GLFW_KEY_F12 -> F12
            GLFW.GLFW_KEY_F13 -> F13
            GLFW.GLFW_KEY_F14 -> F14
            GLFW.GLFW_KEY_F15 -> F15
            GLFW.GLFW_KEY_F16 -> F16
            GLFW.GLFW_KEY_F17 -> F17
            GLFW.GLFW_KEY_F18 -> F18
            GLFW.GLFW_KEY_F19 -> F19
            GLFW.GLFW_KEY_F20 -> F20
            GLFW.GLFW_KEY_F21 -> F21
            GLFW.GLFW_KEY_F22 -> F22
            GLFW.GLFW_KEY_F23 -> F23
            GLFW.GLFW_KEY_F24 -> F24
            GLFW.GLFW_KEY_F25 -> F25
            GLFW.GLFW_KEY_KP_0 -> KP_0
            GLFW.GLFW_KEY_KP_1 -> KP_1
            GLFW.GLFW_KEY_KP_2 -> KP_2
            GLFW.GLFW_KEY_KP_3 -> KP_3
            GLFW.GLFW_KEY_KP_4 -> KP_4
            GLFW.GLFW_KEY_KP_5 -> KP_5
            GLFW.GLFW_KEY_KP_6 -> KP_6
            GLFW.GLFW_KEY_KP_7 -> KP_7
            GLFW.GLFW_KEY_KP_8 -> KP_8
            GLFW.GLFW_KEY_KP_9 -> KP_9
            GLFW.GLFW_KEY_KP_DECIMAL -> KP_DECIMAL
            GLFW.GLFW_KEY_KP_DIVIDE -> KP_DIVIDE
            GLFW.GLFW_KEY_KP_MULTIPLY -> KP_MULTIPLY
            GLFW.GLFW_KEY_KP_SUBTRACT -> KP_SUBTRACT
            GLFW.GLFW_KEY_KP_ADD -> KP_ADD
            GLFW.GLFW_KEY_KP_ENTER -> KP_ENTER
            GLFW.GLFW_KEY_KP_EQUAL -> KP_EQUAL
            GLFW.GLFW_KEY_LEFT_SHIFT -> LEFT_SHIFT
            GLFW.GLFW_KEY_LEFT_CONTROL -> LEFT_CONTROL
            GLFW.GLFW_KEY_LEFT_ALT -> LEFT_ALT
            GLFW.GLFW_KEY_LEFT_SUPER -> LEFT_SUPER
            GLFW.GLFW_KEY_RIGHT_SHIFT -> RIGHT_SHIFT
            GLFW.GLFW_KEY_RIGHT_CONTROL -> RIGHT_CONTROL
            GLFW.GLFW_KEY_RIGHT_ALT -> RIGHT_ALT
            GLFW.GLFW_KEY_RIGHT_SUPER -> RIGHT_SUPER
            GLFW.GLFW_KEY_MENU -> MENU
            else -> UNKNOWN
        }
    }
}