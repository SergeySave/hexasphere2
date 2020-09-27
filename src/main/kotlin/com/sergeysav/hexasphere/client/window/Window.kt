package com.sergeysav.hexasphere.client.window

import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.input.Action
import com.sergeysav.hexasphere.client.input.Key
import com.sergeysav.hexasphere.client.input.KeyModifiers
import com.sergeysav.hexasphere.client.input.MouseButton
import com.sergeysav.hexasphere.client.window.screen.Screen
import com.sergeysav.hexasphere.client.window.screen.ScreenAction
import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.bgfx.BGFXInit
import org.lwjgl.bgfx.BGFXPlatform
import org.lwjgl.bgfx.BGFXPlatformData
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetTimerFrequency
import org.lwjgl.glfw.GLFW.glfwGetTimerValue
import org.lwjgl.glfw.GLFWNativeCocoa
import org.lwjgl.glfw.GLFWNativeWin32
import org.lwjgl.glfw.GLFWNativeX11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.Platform


class Window(
    private var width: Int,
    private var height: Int,
    private val title: String,
    private val windowOptions: WindowOptions,
    private val initialScreen: Screen
) {

    private val logger = KotlinLogging.logger {  }

    private var glfwInitialized = false
    private var bgfxInitialized = false
    private var window = 0L
    private val reset = BGFX.BGFX_RESET_VSYNC
    private var format = 0
    private var renderer = WindowOptions.DEFAULT_RENDERER
    private val screenStack = ArrayList<Screen>(16)
    private var lastRenderedScreen: Screen? = null
    private val modifiers = KeyModifiers.Mutable()

    private fun initAndRun() {
        MemoryStack.stackPush().use { stack ->
            logger.trace { "Initalizing GLFW" }
            if (!GLFW.glfwInit()) throw WindowException("Failed to initialize GLFW")
            glfwInitialized = true

            logger.trace { "Setting GLFW Window Parameters" }
            // Renderer API will be managed by bgfx
            GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)

            logger.trace { "Creating GLFW Window" }
            window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
            if (window == 0L) throw WindowException("Failed to create GLFW window")

            logger.trace { "Setting GLFW Callbacks" }
            GLFW.glfwSetFramebufferSizeCallback(window, this::onResize)
            GLFW.glfwSetKeyCallback(window, this::onKey)
            GLFW.glfwSetMouseButtonCallback(window, this::onMouseButton)
            GLFW.glfwSetCursorPosCallback(window, this::onMouseMove)

            logger.trace { "Setting BGFX Platform Data" }
            val bgfxPlatformData = BGFXPlatformData.callocStack(stack)
            when (Platform.get()) {
                Platform.LINUX -> {
                    bgfxPlatformData.ndt(GLFWNativeX11.glfwGetX11Display())
                    bgfxPlatformData.nwh(GLFWNativeX11.glfwGetX11Window(window))
                }
                Platform.MACOSX -> {
                    bgfxPlatformData.ndt(MemoryUtil.NULL)
                    bgfxPlatformData.nwh(GLFWNativeCocoa.glfwGetCocoaWindow(window))
                }
                Platform.WINDOWS -> {
                    bgfxPlatformData.ndt(MemoryUtil.NULL)
                    bgfxPlatformData.nwh(GLFWNativeWin32.glfwGetWin32Window(window))
                }
                else -> throw WindowException("Failed to initialize BGFX Platform Data")
            }
            bgfxPlatformData.context(MemoryUtil.NULL)
            bgfxPlatformData.backBuffer(MemoryUtil.NULL)
            bgfxPlatformData.backBufferDS(MemoryUtil.NULL)
            BGFXPlatform.bgfx_set_platform_data(bgfxPlatformData)

            val renderers = BGFXUtil.getSupportedRenderers()
            BGFXUtil.printSupportedRenderers(renderers)

            logger.trace { "Setting BGFX Initialization Parameters" }
            val init = BGFXInit.mallocStack(stack)
            BGFX.bgfx_init_ctor(init)
            init.type(windowOptions.renderer ?: WindowOptions.DEFAULT_RENDERER)
                .vendorId(windowOptions.pciId ?: WindowOptions.DEFAULT_PCIID)
                .deviceId(0)
                .callback(null)
                .allocator(null)
                .resolution { res ->
                    res.width(width)
                        .height(height)
                        .reset(reset)
                }

            logger.trace { "Initializing BGFX" }
            if (!BGFX.bgfx_init(init)) throw WindowException("Could not initialize BGFX")
            bgfxInitialized = true

            format = init.resolution().format()

            val bgfxCaps = BGFX.bgfx_get_caps() ?: throw WindowException("Could not get BGFX capabilities")

            this.renderer = bgfxCaps.rendererType()
            val renderer = BGFX.bgfx_get_renderer_name(this.renderer)
            if (renderer == "NULL" || renderer == null) throw WindowException("Could not initialize BGFX renderer")
            logger.info { "Using renderer $renderer" }
            BGFXUtil.renderer = this.renderer

            logger.trace { "Applying renderer settings" }
            BGFXUtil.zZeroToOne = !bgfxCaps.homogeneousDepth()
            BGFXUtil.texelHalf = if (this.renderer == BGFX.BGFX_RENDERER_TYPE_DIRECT3D9) 0.5f else 0f
            BGFXUtil.reset = reset

            logger.trace { "Settings debug mode" }
            BGFX.bgfx_set_debug(windowOptions.debug ?: WindowOptions.DEFAULT_DEBUG)

            logger.trace { "Settings view clear" }
            BGFX.bgfx_set_view_clear(
                0,
                BGFX.BGFX_CLEAR_COLOR or BGFX.BGFX_CLEAR_DEPTH or BGFX.BGFX_CLEAR_STENCIL,
                0x00000000, 1.0f, 0
            )

            logger.trace { "Adding Screen" }
            pushScreen(initialScreen)

            logger.info { "Starting Render Loop" }
            var lastTime = glfwGetTimerValue()
//            val startTime = glfwGetTimerValue().also { lastTime = it }

            while (!GLFW.glfwWindowShouldClose(window)) {
                GLFW.glfwPollEvents()

                val now = glfwGetTimerValue()
                val frameTime = now - lastTime
                lastTime = now

                val freq = glfwGetTimerFrequency().toDouble()
                val toMs = 1000.0 / freq

                //val time = (now - startTime) / freq

                BGFX.bgfx_set_view_rect(0, 0, 0, width, height)
                BGFX.bgfx_dbg_text_clear(0, false)

                val toRenderScreen = peekScreen()
                if (toRenderScreen != lastRenderedScreen) {
                    lastRenderedScreen?.pause()
                    MemoryStack.stackPush().use {
                        val x = it.callocDouble(1)
                        val y = it.callocDouble(1)
                        GLFW.glfwGetCursorPos(window, x, y)
                        toRenderScreen.onMouseMove(x[0], y[0], true)
                    }
                    toRenderScreen.resume()
                }
                lastRenderedScreen = toRenderScreen
                when (val action = toRenderScreen.render(frameTime * toMs, width, height)) {
                    ScreenAction.NoOp -> {
                    }
                    ScreenAction.Pop -> popScreen()
                    is ScreenAction.Push -> pushScreen(action.newScreen)
                    is ScreenAction.Replace -> {
                        popScreen()
                        pushScreen(action.newScreen)
                    }
                }

                BGFX.bgfx_frame(false)
            }
        }
    }

    private fun cleanup() {
        logger.trace { "Cleaning up screens" }
        while (screenStack.isNotEmpty()) {
            popScreen()
        }
        logger.trace { "Cleaning up BGFX" }
        if (bgfxInitialized) {
            BGFX.bgfx_shutdown()
        }
        BGFXUtil.dispose()

        logger.trace { "Cleaning up window" }
        if (glfwInitialized) {
            Callbacks.glfwFreeCallbacks(window)
            GLFW.glfwDestroyWindow(window)
            GLFW.glfwTerminate()
        }
    }

    fun run() {
        try {
            initAndRun()
        } catch (ex: Throwable) {
            logger.error(ex) { "An error occurred which could not be resolved. Cleaning up." }
            throw ex
        } finally {
            cleanup()
        }
    }

    private fun onResize(@Suppress("UNUSED_PARAMETER") window: Long, width: Int, height: Int) {
        this.width = width
        this.height = height

        BGFX.bgfx_reset(width, height, reset, format)
    }

    private fun setModifiers(modifiers: Int) {
        this.modifiers.shift = modifiers and GLFW.GLFW_MOD_SHIFT != 0
        this.modifiers.control = modifiers and GLFW.GLFW_MOD_CONTROL != 0
        this.modifiers.alt = modifiers and GLFW.GLFW_MOD_ALT != 0
        this.modifiers.`super` = modifiers and GLFW.GLFW_MOD_SUPER != 0
        this.modifiers.capsLock = modifiers and GLFW.GLFW_MOD_CAPS_LOCK != 0
        this.modifiers.numLock = modifiers and GLFW.GLFW_MOD_NUM_LOCK != 0
    }

    private fun onKey(
        @Suppress("UNUSED_PARAMETER") window: Long,
        key: Int,
        @Suppress("UNUSED_PARAMETER") code: Int,
        action: Int,
        modifiers: Int
    ) {
        setModifiers(modifiers)
        peekScreen().onKey(
            when (action) {
                GLFW.GLFW_PRESS -> Action.PRESS
                GLFW.GLFW_RELEASE -> Action.RELEASE
                GLFW.GLFW_REPEAT -> Action.REPEAT
                else -> throw WindowException("Unknown Key Action")
            }, Key.fromGLFWCode(key), this.modifiers
        )
    }

    private fun onMouseButton(
        @Suppress("UNUSED_PARAMETER") window: Long,
        button: Int,
        action: Int,
        modifiers: Int
    ) {
        setModifiers(modifiers)
        peekScreen().onMouseButton(
            when (action) {
                GLFW.GLFW_PRESS -> Action.PRESS
                GLFW.GLFW_RELEASE -> Action.RELEASE
                GLFW.GLFW_REPEAT -> Action.REPEAT
                else -> throw WindowException("Unknown Mouse Action")
            }, MouseButton.fromGLFWCode(button), this.modifiers
        )
    }

    private fun onMouseMove(
        @Suppress("UNUSED_PARAMETER") window: Long,
        x: Double,
        y: Double
    ) {
        peekScreen().onMouseMove(x, y, false)
    }

    private fun pushScreen(screen: Screen) {
        screen.create()
        screenStack.add(screen)
    }

    private fun popScreen() {
        val last = screenStack.removeLast()
        if (last == lastRenderedScreen) {
            last.pause()
            lastRenderedScreen = null
        }
        last.dispose()
    }

    private fun peekScreen() = screenStack.last()
}