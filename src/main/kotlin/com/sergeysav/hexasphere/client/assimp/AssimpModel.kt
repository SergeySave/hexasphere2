package com.sergeysav.hexasphere.client.assimp

import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIScene
import org.lwjgl.system.MemoryUtil

class AssimpModel(scene: AIScene, basePath: String) {

    val meshes: List<AssimpMesh>

    init {
        val meshes = mutableListOf<AssimpMesh>()
        for (i in 0 until scene.mNumMeshes()) {
            meshes.add(processMesh(AIMesh.create(scene.mMeshes()!![i]), scene, basePath))
        }
        this.meshes = meshes
    }

    fun dispose() {
        for (mesh in meshes) {
            mesh.dispose()
        }
    }

    private fun processMesh(aiMesh: AIMesh, scene: AIScene, basePath: String): AssimpMesh {
        val vert = MemoryUtil.memAlloc(aiMesh.mNumVertices() * AssimpUtils.vertexLayout.layout.stride())
        val idxs = MemoryUtil.memAlloc(aiMesh.mNumFaces() * 3 * 2)
        val textures = mutableListOf<AssimpTexture>()

        for (i in 0 until aiMesh.mNumVertices()) {
            vert.putFloat(aiMesh.mVertices()[i].x())
                .putFloat(aiMesh.mVertices()[i].y())
                .putFloat(aiMesh.mVertices()[i].z())
                .putFloat(aiMesh.mNormals()!![i].x())
                .putFloat(aiMesh.mNormals()!![i].y())
                .putFloat(aiMesh.mNormals()!![i].z())
            if (aiMesh.mTextureCoords()[0] != 0L) {
                val texCoords = aiMesh.mTextureCoords(0)!![i]
                vert.putFloat(texCoords.x())
                    .putFloat(texCoords.y())
            } else {
                vert.putFloat(0f)
                    .putFloat(0f)
            }
            vert.putFloat(aiMesh.mTangents()!![i].x())
                .putFloat(aiMesh.mTangents()!![i].y())
                .putFloat(aiMesh.mTangents()!![i].z())
                .putFloat(aiMesh.mBitangents()!![i].x())
                .putFloat(aiMesh.mBitangents()!![i].y())
                .putFloat(aiMesh.mBitangents()!![i].z())
        }
        vert.flip()

        for (i in 0 until aiMesh.mNumFaces()) {
            val aiFace = aiMesh.mFaces()[i]
            for (j in 0 until aiFace.mNumIndices()) {
                idxs.putShort(aiFace.mIndices()[j].toShort())
            }
        }
        idxs.flip()

        if (aiMesh.mMaterialIndex() >= 0) {
            val aiMaterial = AIMaterial.create(scene.mMaterials()!![aiMesh.mMaterialIndex()])
            textures.addAll(AssimpUtils.loadMaterialTextures(aiMaterial, AssimpTexture.Type.DIFFUSE, basePath))
            textures.addAll(AssimpUtils.loadMaterialTextures(aiMaterial, AssimpTexture.Type.SPECULAR, basePath))
            textures.addAll(AssimpUtils.loadMaterialTextures(aiMaterial, AssimpTexture.Type.NORMAL, basePath))
        }

        return AssimpMesh(vert, aiMesh.mNumVertices(), idxs, aiMesh.mNumFaces() * 3, textures)
    }
}