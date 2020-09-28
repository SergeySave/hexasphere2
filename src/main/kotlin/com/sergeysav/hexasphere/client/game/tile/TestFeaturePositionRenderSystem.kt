package com.sergeysav.hexasphere.client.game.tile

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.Uniform
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.client.ui.StaticMesh
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.hexasphere.computeTileRotation
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.util.Random
import kotlin.math.sqrt

class TestFeaturePositionRenderSystem(private val view: View) : BaseEntitySystem(Aspect.all(TileFeaturePositionComponent::class.java, TileComponent::class.java)) {

    private var mesh: StaticMesh = StaticMesh(
        ShaderProgram.loadFromFiles("feat/vs", "feat/fs"),
        VertexLayout.new(
            VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT)
        ),
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
    private var colorUniform: Uniform = Uniform.new("u_color", Uniform.Type.VEC4)
    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val mat3 = Matrix3f()
    private val model = Matrix4f()
    private val rotation = Matrix4f()
    private val vec4 = Vector4f()
    private val rand = Random()
    private lateinit var buffer: FloatBuffer
    private lateinit var featMapper: ComponentMapper<TileFeaturePositionComponent>
    private lateinit var tileMapper: ComponentMapper<TileComponent>

    override fun initialize() {
        super.initialize()
        buffer = MemoryUtil.memAllocFloat(4)
    }

    override fun processSystem() {
        Encoder.with {
            val entities = subscription.entities
            for (i in 0 until entities.size()) {
                val tileEntity = entities[i]
                val featurePositionComponent = featMapper[tileEntity]
                val tileComponent = tileMapper[tileEntity]

                computeTileRotation(tileComponent.centroid, vec3a, vec3b, mat3).invert()
                rotation.set(mat3).translateLocal(tileComponent.centroid.x().toFloat(), tileComponent.centroid.y().toFloat(), tileComponent.centroid.z().toFloat())

                rand.setSeed(tileEntity.toLong())

                val part = featurePositionComponent.positions[Math.floorMod(tileEntity, 3)]
                for (j in part.indices) {
                    setState(Encoder.DEFAULT)
                    setUniform(colorUniform, vec4.set(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f).get(buffer))
                    mesh.render(this,
                        model.set(rotation)
                            .translate(part[j].x().toFloat(), part[j].y().toFloat(), part[j].z().toFloat())
                            .scale(featurePositionComponent.radius.toFloat()).scale(1 / sqrt(2f)),
                        view.id)
                }
            }
        }
    }

    override fun dispose() {
        mesh.dispose()
        MemoryUtil.memFree(buffer)
    }
}