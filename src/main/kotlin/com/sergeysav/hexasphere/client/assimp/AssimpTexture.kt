package com.sergeysav.hexasphere.client.assimp

import com.sergeysav.hexasphere.client.bgfx.texture.Texture
import com.sergeysav.hexasphere.client.bgfx.uniform.Uniform
import org.lwjgl.assimp.Assimp

/**
 * @author sergeys
 *
 * @constructor Creates a new AssimpTexture
 */
data class AssimpTexture(val texture: Texture, val path: String, val type: Type) {

    fun dispose() {
        AssimpUtils.disposeTexture(this)
    }

    enum class Type(val assimpType: Int) {
        DIFFUSE(Assimp.aiTextureType_DIFFUSE),
        SPECULAR(Assimp.aiTextureType_SPECULAR),
        NORMAL(Assimp.aiTextureType_HEIGHT)
    }

    companion object {
        val diffuseUniforms = Array(4) { Uniform.new("s_diffuse${it}", Uniform.Type.SAMPLER) }
        val specularUniforms = Array(4) { Uniform.new("s_specular${it}", Uniform.Type.SAMPLER) }
        val normalUniforms = Array(4) { Uniform.new("s_normal${it}", Uniform.Type.SAMPLER) }
    }
}