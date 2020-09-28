package com.sergeysav.hexasphere.client.game.skybox

import com.artemis.BaseSystem
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.Texture
import com.sergeysav.hexasphere.client.bgfx.Uniform
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.client.ui.StaticMesh
import org.joml.Matrix4f
import org.lwjgl.system.MemoryUtil

class SkyboxRenderSystem(fileName: String, private val view: View) : BaseSystem() {

    private var skyboxTexture: Texture = Texture.newCubeMap(fileName)
    private var texCubeSampler: Uniform = Uniform.new("s_texCube", Uniform.Type.SAMPLER)
    private var skyboxMesh: StaticMesh = StaticMesh(
        ShaderProgram.loadFromFiles("skybox/vs", "skybox/fs"),
        VertexLayout.new(VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT)),
        numVertices = 8, vertices = MemoryUtil.memAlloc(8 * ((3) * Float.SIZE_BYTES)).apply {
            putFloat(-1f).putFloat(+1f).putFloat(+1f)
            putFloat(+1f).putFloat(+1f).putFloat(+1f)
            putFloat(-1f).putFloat(-1f).putFloat(+1f)
            putFloat(+1f).putFloat(-1f).putFloat(+1f)
            putFloat(-1f).putFloat(+1f).putFloat(-1f)
            putFloat(+1f).putFloat(+1f).putFloat(-1f)
            putFloat(-1f).putFloat(-1f).putFloat(-1f)
            putFloat(+1f).putFloat(-1f).putFloat(-1f)
        }.flip(), numIndices = 3 * 12, indices = MemoryUtil.memAlloc(3 * 12 * 2).apply {
            putShort(0).putShort(1).putShort(2)
            putShort(1).putShort(3).putShort(2)
            putShort(4).putShort(6).putShort(5)
            putShort(5).putShort(6).putShort(7)
            putShort(0).putShort(2).putShort(4)
            putShort(4).putShort(2).putShort(6)
            putShort(1).putShort(5).putShort(3)
            putShort(5).putShort(7).putShort(3)
            putShort(0).putShort(4).putShort(1)
            putShort(4).putShort(5).putShort(1)
            putShort(2).putShort(3).putShort(6)
            putShort(6).putShort(3).putShort(7)
        }.flip(), bit32 = false, freeShader = true, freeLayout = true, freeLayoutHandle = true,
        freeVertices = true, freeIndices = true
    )
    private val model = Matrix4f().scale(100_000f)

    override fun processSystem() {
        Encoder.with { // This should be rendered last in the background
            setState(Encoder.SKYBOX)
            setTexture(0, texCubeSampler, skyboxTexture)
            skyboxMesh.render(this, model, view.id)
        }
    }

    override fun dispose() {
        skyboxTexture.dispose()
        texCubeSampler.dispose()
        skyboxMesh.dispose()
    }
}