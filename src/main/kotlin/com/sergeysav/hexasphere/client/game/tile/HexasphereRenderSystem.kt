package com.sergeysav.hexasphere.client.game.tile

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.client.bgfx.DynamicVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.VertexLayoutHandle
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.common.game.Groups
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import org.joml.Matrix4f
import org.lwjgl.system.MemoryUtil

class HexasphereRenderSystem(
    private val pentagons: Int,
    private val hexagons: Int,
    private val viewId: View,
    private val shader: ShaderProgram
) : BaseSystem() {

    private val vertLayout = VertexLayout.new(
        VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
        VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, true),
    )
    private val vertLayoutHandle = VertexLayoutHandle.new(vertLayout)
    private val bytesPerVert = (3) * Float.SIZE_BYTES + (4) * Byte.SIZE_BYTES
    private val verts = pentagons * 5 + hexagons * 6
    private val hexVerts = MemoryUtil.memAlloc(verts * bytesPerVert)
    private val vertBuffer = DynamicVertexBuffer.new(verts, vertLayout)
    private var indxBuffer: StaticIndexBuffer? = null
    private val modelBuffer = MemoryUtil.memAllocFloat(16)
    private val model = Matrix4f()

    private lateinit var geomMapper: ComponentMapper<TileGeometryComponent>

    override fun dispose() {
        super.dispose()
        vertLayoutHandle.dispose()
        vertLayout.dispose()
        indxBuffer?.dispose()
        MemoryUtil.memFree(hexVerts)
        MemoryUtil.memFree(modelBuffer)
    }

    private fun setTileVerts(geometry: TileGeometryComponent, color: Int): Int {
        val numVerts = if (geometry.vertices[5].lengthSquared() >= 1e-4) 6 else 5
        for (j in 0 until numVerts) {
            val vert = geometry.vertices[j]
            hexVerts.putFloat(vert.x().toFloat())
                .putFloat(vert.y().toFloat())
                .putFloat(vert.z().toFloat())
                .putInt(color)
        }
        return numVerts
    }


    override fun processSystem() {
        val dirtyTiles = world.getSystem(GroupManager::class.java).getEntityIds(Groups.DIRTY_TILE)
        val tileTypeSystem = world.getSystem(TileTypeSystem::class.java)

        if (indxBuffer == null) {
            val indices = MemoryUtil.memAlloc((pentagons * 3 + hexagons * 4) * 3 * Int.SIZE_BYTES)

            for (i in 0 until dirtyTiles.size()) {
                val tile = dirtyTiles[i]

                val geometry = geomMapper.get(tile).apply {
                    this.vBufferPos = hexVerts.position()
                    this.iBufferPos = indices.position()
                }

                val color: Int = 0xff000000.toInt() or (tileTypeSystem.getTileType(tile)?.color ?: 0)
                val v1 = geometry.vBufferPos / bytesPerVert
                val numVerts = setTileVerts(geometry, color)
                for (j in 2 until numVerts) {
                    indices.putInt(v1).putInt(v1 + j - 1).putInt(v1 + j)
                }
            }

            hexVerts.flip()
            indices.flip()
            vertBuffer.update(hexVerts, autoFree = false)
            indxBuffer = StaticIndexBuffer.new32Bit(indices, autoFree = true)
        } else {
            for (i in 0 until dirtyTiles.size()) {
                val tile = dirtyTiles[i]

                val geometry = geomMapper.get(tile)
                val color: Int = 0xff000000.toInt() or (tileTypeSystem.getTileType(tile)?.color ?: 0)
                val v1 = geometry.vBufferPos / bytesPerVert
                hexVerts.position(geometry.vBufferPos)
                setTileVerts(geometry, color)
                hexVerts.limit(hexVerts.position())
                hexVerts.position(geometry.vBufferPos)
                vertBuffer.update(hexVerts, autoFree = false, startVertex = v1)
                hexVerts.clear()
            }
        }

        Encoder.with {
            setState(Encoder.DEFAULT)
            setVertexBuffer(vertBuffer, vertLayoutHandle, verts)
            setIndexBuffer(indxBuffer!!, (pentagons * 3 + hexagons * 4) * 3)
            setTransform(model.get(modelBuffer))
            submit(shader, viewId.id)
        }
    }
}