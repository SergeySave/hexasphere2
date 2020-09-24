package com.sergeysav.hexasphere.client.bgfx

import org.lwjgl.bgfx.BGFX

inline class Uniform(val handle: Short) {

    fun dispose() {
        BGFX.bgfx_destroy_uniform(handle)
    }

    companion object {
        fun new(uniformName: CharSequence, type: Type, count: Int = 1): Uniform {
            return Uniform(BGFX.bgfx_create_uniform(uniformName, type.handle, count))
        }
    }

    enum class Type(val handle: Int) {
        SAMPLER(BGFX.BGFX_UNIFORM_TYPE_SAMPLER),
        VEC4(BGFX.BGFX_UNIFORM_TYPE_VEC4),
        MAT3(BGFX.BGFX_UNIFORM_TYPE_MAT3),
        MAT4(BGFX.BGFX_UNIFORM_TYPE_MAT4)
    }
}