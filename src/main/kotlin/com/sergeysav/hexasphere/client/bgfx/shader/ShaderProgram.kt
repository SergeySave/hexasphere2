package com.sergeysav.hexasphere.client.bgfx.shader

import org.lwjgl.bgfx.BGFX

inline class ShaderProgram(val handle: Short = 0) {

    fun dispose() {
        if (handle != 0.toShort()) {
            BGFX.bgfx_destroy_program(handle)
        }
    }

    companion object {
        fun loadFromFiles(vertexShaderName: String, fragmentShaderName: String): ShaderProgram {
            val vertex = Shader.loadShaderFromFile(vertexShaderName)
            val fragment = Shader.loadShaderFromFile(fragmentShaderName)

            return ShaderProgram(BGFX.bgfx_create_program(vertex.handle, fragment.handle, true))
        }
    }
}
