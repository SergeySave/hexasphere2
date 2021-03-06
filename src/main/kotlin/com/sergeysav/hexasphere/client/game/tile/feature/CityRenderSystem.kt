package com.sergeysav.hexasphere.client.game.tile.feature

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.utils.IntBag
import com.sergeysav.hexasphere.client.assimp.AssimpModel
import com.sergeysav.hexasphere.client.assimp.AssimpUtils
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.instance.withInstanceBuffer
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.tile.TileFeaturePositionComponent
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.game.tile.feature.CityFeatureComponent
import com.sergeysav.hexasphere.common.game.tile.feature.TileFeatureSystem
import com.sergeysav.hexasphere.common.hexasphere.computeTileRotation
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class CityRenderSystem : BaseEntitySystem(Aspect.all(TileFeaturePositionComponent::class.java, TileComponent::class.java, CityFeatureComponent::class.java)) {

    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val mat3 = Matrix3f()
    private val model = Matrix4f()
    private val rotation = Matrix4f()
    private val buffer = MemoryUtil.memAlloc(64)
    private val instanceStride = 64

    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var featureSystem: TileFeatureSystem
    private lateinit var featMapper: ComponentMapper<TileFeaturePositionComponent>
    private lateinit var tileMapper: ComponentMapper<TileComponent>
    private lateinit var cityMapper: ComponentMapper<CityFeatureComponent>
    private lateinit var renderModel: AssimpModel

    private var cities = 0

    override fun initialize() {
        super.initialize()
        renderModel = AssimpUtils.loadModel("/house/House.obj")
        cities = 0
    }

    override fun inserted(entities: IntBag?) {
        cities += entities?.size() ?: 0
    }

    override fun removed(entities: IntBag?) {
        cities -= entities?.size() ?: 0
    }

    override fun processSystem() {
        val entities = subscription.entities
        MemoryStack.stackPush().use { stack ->
            stack.withInstanceBuffer(cities, instanceStride) { instance ->
                val data = instance.handle.data()

                for (i in 0 until entities.size()) {
                    val tileEntity = entities[i]
                    val featurePositionComponent = featMapper[tileEntity]
                    val tileComponent = tileMapper[tileEntity]
                    val cityFeatureComponent = cityMapper[tileEntity]

                    computeTileRotation(tileComponent.centroid, vec3a, vec3b, mat3).invert()
                    rotation.set(mat3).translateLocal(
                        tileComponent.centroid.x().toFloat(),
                        tileComponent.centroid.y().toFloat(),
                        tileComponent.centroid.z().toFloat()
                    )

                    val part = featurePositionComponent.positions[featureSystem.getFeatureCount(tileEntity)]
                    data.put(model.set(rotation)
                        .translate(part[cityFeatureComponent.featureNumber].x().toFloat(), part[cityFeatureComponent.featureNumber].y().toFloat(), 0f)
                        .scale(featurePositionComponent.radius.toFloat())
                        .get(buffer))
                    buffer.rewind()
                }
                data.rewind()

                Encoder.with {
                    for (mesh in renderModel.meshes) {
                        setState(Encoder.DEFAULT_CW)
                        setTransform(model.identity().get(buffer))
                        buffer.rewind()
                        setInstanceBuffer(instance, cities)
                        mesh.set(this)
                        submit(renderDataSystem.featureShader, renderDataSystem.featuresView)
                    }
                }
            }
        }
    }

    override fun dispose() {
        renderModel.dispose()
        renderDataSystem.featureShader.dispose()
        MemoryUtil.memFree(buffer)
    }
}