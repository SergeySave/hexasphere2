package com.sergeysav.hexasphere.client.game.tile

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.sergeysav.hexasphere.client.assimp.AssimpModel
import com.sergeysav.hexasphere.client.assimp.AssimpUtils
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.instance.withInstanceBuffer
import com.sergeysav.hexasphere.client.bgfx.shader.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.view.View
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.hexasphere.computeTileRotation
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.system.MemoryStack

class TestFeaturePositionRenderSystem(private val view: View) : BaseEntitySystem(Aspect.all(TileFeaturePositionComponent::class.java, TileComponent::class.java)) {

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val mat3 = Matrix3f()
    private val model = Matrix4f()
    private val rotation = Matrix4f()
    private val instanceStride = 64

    private lateinit var featMapper: ComponentMapper<TileFeaturePositionComponent>
    private lateinit var tileMapper: ComponentMapper<TileComponent>
    private lateinit var renderModel: AssimpModel
    private var shader = ShaderProgram(0)

    override fun initialize() {
        super.initialize()
        shader = ShaderProgram.loadFromFiles("feat/vs", "feat/fs")
        renderModel = AssimpUtils.loadModel("/house/House.obj")
    }

    override fun processSystem() {
        val entities = subscription.entities
        var parts = 0

        for (i in 0 until entities.size()) {
            val tileEntity = entities[i]
            parts += Math.floorMod(tileEntity, 3) + 1
        }

        MemoryStack.stackPush().use { stack ->
            val buffer = stack.malloc(64)
            Encoder.with {
                stack.withInstanceBuffer(parts, instanceStride) { instance ->
                    val data = instance.handle.data()

                    for (i in 0 until entities.size()) {
                        val tileEntity = entities[i]
                        val featurePositionComponent = featMapper[tileEntity]
                        val tileComponent = tileMapper[tileEntity]

                        computeTileRotation(tileComponent.centroid, vec3a, vec3b, mat3).invert()
                        rotation.set(mat3).translateLocal(
                            tileComponent.centroid.x().toFloat(),
                            tileComponent.centroid.y().toFloat(),
                            tileComponent.centroid.z().toFloat()
                        )

                        val part = featurePositionComponent.positions[Math.floorMod(tileEntity, 3)]
                        for (j in part.indices) {
                            data.put(model.set(rotation)
                                .translate(part[j].x().toFloat(), part[j].y().toFloat(), 0f)
                                .scale(featurePositionComponent.radius.toFloat())
                                .get(buffer.rewind()))
                        }
                    }
                    data.rewind()

                    for (mesh in renderModel.meshes) {
                        setState(Encoder.DEFAULT_CW)
                        setTransform(model.identity().get(buffer))
                        setInstanceBuffer(instance, parts)
                        mesh.set(this)
                        submit(shader, view.id)
                    }
                }
            }
        }
    }

    override fun dispose() {
        renderModel.dispose()
        shader.dispose()
    }
}