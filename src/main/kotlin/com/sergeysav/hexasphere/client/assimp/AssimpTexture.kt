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

    enum class Type(val typeName: String, val assimpType: Int) {
        DIFFUSE("texture_diffuse", Assimp.aiTextureType_DIFFUSE),
        SPECULAR("texture_specular", Assimp.aiTextureType_SPECULAR),
        NORMAL("texture_normal", Assimp.aiTextureType_HEIGHT)
    }

    companion object {
        val diffuseUniforms = Array(4) { Uniform.new("s_diffuse${it + 1}", Uniform.Type.SAMPLER) }
        val specularUniforms = Array(4) { Uniform.new("s_specular${it + 1}", Uniform.Type.SAMPLER) }
        val normalUniforms = Array(4) { Uniform.new("s_normal${it + 1}", Uniform.Type.SAMPLER) }
    }
}