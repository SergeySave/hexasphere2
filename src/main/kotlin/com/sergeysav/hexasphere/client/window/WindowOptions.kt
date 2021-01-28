package com.sergeysav.hexasphere.client.window

import mu.KotlinLogging
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.Platform

data class WindowOptions(
    val renderer: Int? = null,
    val pciId: Short? = null,
    val debug: Int? = null
) {
    companion object {
        private val logger = KotlinLogging.logger {  }
        
        val DEFAULT_RENDERER: Int
            get() = when (Platform.get()) {
                Platform.LINUX -> BGFX.BGFX_RENDERER_TYPE_VULKAN
                Platform.MACOSX -> BGFX.BGFX_RENDERER_TYPE_OPENGL
                Platform.WINDOWS -> BGFX.BGFX_RENDERER_TYPE_DIRECT3D11
                else -> BGFX.BGFX_RENDERER_TYPE_COUNT
            }
        val DEFAULT_PCIID: Short
            get() = BGFX.BGFX_PCI_ID_NONE
        val DEFAULT_DEBUG: Int
            get() = BGFX.BGFX_DEBUG_NONE

        fun parseFromArgs(args: Array<String>): WindowOptions {
            var renderer: Int? = null
            var pciId: Short? = null
            var debug: Int? = null

            if (args.contains("--gl")) {
                renderer = BGFX.BGFX_RENDERER_TYPE_OPENGL
            } else if (args.contains("--vk")) {
                renderer = BGFX.BGFX_RENDERER_TYPE_VULKAN
            } else if (args.contains("--noop")) {
                renderer = BGFX.BGFX_RENDERER_TYPE_NOOP
            } else if (Platform.get() == Platform.WINDOWS) {
                when {
                    args.contains("--d9") -> renderer = BGFX.BGFX_RENDERER_TYPE_DIRECT3D9
                    args.contains("--d11") -> renderer = BGFX.BGFX_RENDERER_TYPE_DIRECT3D11
                    args.contains("--d12") -> renderer = BGFX.BGFX_RENDERER_TYPE_DIRECT3D12
                }
            } else if (Platform.get() == Platform.MACOSX) {
                if (args.contains("--mtl")) {
                    renderer = BGFX.BGFX_RENDERER_TYPE_METAL
                    logger.warn { "macOS Metal Backend not fully supported. On newer version of macOS this may crash." }
                }
            }

            when {
                args.contains("--amd") -> pciId = BGFX.BGFX_PCI_ID_AMD
                args.contains("--nvidia") -> pciId = BGFX.BGFX_PCI_ID_NVIDIA
                args.contains("--intel") -> pciId = BGFX.BGFX_PCI_ID_INTEL
                args.contains("--sw") -> pciId = BGFX.BGFX_PCI_ID_SOFTWARE_RASTERIZER
            }

            when (args.mapNotNull { Regex("^--debug=(.+)$").matchEntire(it)?.groups?.get(1)?.value?.toLowerCase() }.firstOrNull()) {
                "none" -> debug = BGFX.BGFX_DEBUG_NONE
                "wire" -> debug = BGFX.BGFX_DEBUG_WIREFRAME
                "ifh" -> debug = BGFX.BGFX_DEBUG_IFH
                "stats" -> debug = BGFX.BGFX_DEBUG_STATS
                "text" -> debug = BGFX.BGFX_DEBUG_TEXT
                "profiler" -> debug = BGFX.BGFX_DEBUG_PROFILER
            }

            return WindowOptions(renderer, pciId, debug)
        }
    }
}
