package com.sergeysav.hexasphere.client.assimp

import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.index.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.StaticVertexBuffer
import java.nio.ByteBuffer

class AssimpMesh(vertexData: ByteBuffer, private val numVerts: Int, indexData: ByteBuffer, private val numIndx: Int, private val textures: List<AssimpTexture>) {

    private val vertexBuffer = StaticVertexBuffer.new(vertexData, AssimpUtils.vertexLayout, true)
    private val indexBuffer = StaticIndexBuffer.new(indexData, true)

    fun set(encoder: Encoder) {
        encoder.apply {
            setVertexBuffer(vertexBuffer, AssimpUtils.vertexLayoutHandle, numVerts)
            setIndexBuffer(indexBuffer, numIndx)
            var diffuseNr = 0
            var specularNr = 0
            var normalNr = 0

            for (i in textures.indices) {
                setTexture(i, when (textures[i].type) {
                    AssimpTexture.Type.DIFFUSE -> AssimpTexture.diffuseUniforms[diffuseNr++]
                    AssimpTexture.Type.SPECULAR -> AssimpTexture.specularUniforms[specularNr++]
                    AssimpTexture.Type.NORMAL -> AssimpTexture.normalUniforms[normalNr++]
                }, textures[i].texture)
            }
        }
    }

    fun dispose() {
        vertexBuffer.dispose()
        indexBuffer.dispose()
        for (texture in textures) {
            texture.dispose()
        }
    }
}