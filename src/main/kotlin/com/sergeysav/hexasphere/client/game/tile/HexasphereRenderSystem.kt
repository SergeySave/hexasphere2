package com.sergeysav.hexasphere.client.game.tile

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.index.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.uniform.Uniform
import com.sergeysav.hexasphere.client.bgfx.vertex.DynamicVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayoutHandle
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.common.blueComponent
import com.sergeysav.hexasphere.common.color
import com.sergeysav.hexasphere.common.game.Groups
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import com.sergeysav.hexasphere.common.greenComponent
import com.sergeysav.hexasphere.common.redComponent
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector4f
import org.lwjgl.system.MemoryUtil

class HexasphereRenderSystem(
    private val pentagons: Int,
    private val hexagons: Int
) : BaseSystem() {

    private val vertLayout = VertexLayout.new(
        VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
        VertexAttribute(VertexAttribute.Attribute.COLOR0, 4, VertexAttribute.Type.UINT8, true),
        VertexAttribute(VertexAttribute.Attribute.COLOR1, 4, VertexAttribute.Type.UINT8, true),
        VertexAttribute(VertexAttribute.Attribute.TEXCOORD1, 1, VertexAttribute.Type.UINT8, true)
    )
    private val vertLayoutHandle = VertexLayoutHandle.new(vertLayout)
    private val bytesPerVert = (3) * Float.SIZE_BYTES +
            (4) * Byte.SIZE_BYTES +
            (4) * Byte.SIZE_BYTES +
            (1) * Byte.SIZE_BYTES
    private val verts = pentagons * 6 + hexagons * 7
    private val hexVerts = MemoryUtil.memAlloc(verts * bytesPerVert)
    private val vertBuffer = DynamicVertexBuffer.new(verts, vertLayout)
    private var indxBuffer: StaticIndexBuffer? = null
    private val modelBuffer = MemoryUtil.memAllocFloat(16)
    private val vec4Buffer = MemoryUtil.memAllocFloat(4)
    private val model = Matrix4f()
    private val vec3a = Vector3d()
    private val vec4a = Vector4f()
    private val outlineSettingsUniform = Uniform.new("u_outlineSettings", Uniform.Type.VEC4, 1)

    private lateinit var geomMapper: ComponentMapper<TileGeometryComponent>
    private lateinit var groupManager: GroupManager
    private lateinit var tileTypeSystem: TileTypeSystem
    private lateinit var selectionSystem: SelectionSystem
    private lateinit var renderDataSystem: RenderDataSystem

    override fun dispose() {
        super.dispose()
        vertLayoutHandle.dispose()
        vertLayout.dispose()
        indxBuffer?.dispose()
        MemoryUtil.memFree(hexVerts)
        MemoryUtil.memFree(modelBuffer)
    }

    private fun setTileVerts(geometry: TileGeometryComponent, color: Int, outlineColor: Int): Int {
        val numVerts = if (geometry.vertices[5].lengthSquared() >= 1e-4) 6 else 5
        vec3a.zero()
        for (j in 0 until numVerts) {
            val vert = geometry.vertices[j]
            vec3a.add(vert)
            hexVerts.putFloat(vert.x().toFloat())
                .putFloat(vert.y().toFloat())
                .putFloat(vert.z().toFloat())
                .putInt(color)
                .putInt(outlineColor)
                .put((-1).toByte())
        }
        vec3a.div(numVerts.toDouble())
        hexVerts.putFloat(vec3a.x().toFloat())
            .putFloat(vec3a.y().toFloat())
            .putFloat(vec3a.z().toFloat())
            .putInt(color)
            .putInt(outlineColor)
            .put(0.toByte())

        return numVerts
    }

    override fun processSystem() {
        val dirtyTiles = groupManager.getEntityIds(Groups.DIRTY_TILE)

        fun getTileColor(tile: Int): Int {
//            return if (selectionSystem.selectedTile == tile) {
//                val baseColor = tileTypeSystem.getTileType(tile)?.color ?: 0
//                color(
//                    0xFF - (0xFF - redComponent(baseColor)) * 3 / 4,
//                    0xFF - (0xFF - greenComponent(baseColor)) * 3 / 4,
//                    blueComponent(baseColor) * 3 / 4,
//                    0xFF
//                )
//            } else {
                return 0xFF000000.toInt() or (tileTypeSystem.getTileType(tile)?.color ?: 0)
//            }
        }

        fun getTileOutlineColor(tile: Int, color: Int): Int {
            return if (selectionSystem.selectedTile == tile) {
                color(0xFF, 0xFF, 0xFF, 0x0F)
            } else if (selectionSystem.mouseoverTile == tile) {
                color(0xFF - redComponent(color), 0xFF - greenComponent(color), 0xFF - blueComponent(color), 0x08)
            } else {
                color(0x00, 0x00, 0x00, 0x00)
            }
        }

        if (indxBuffer == null) {
            val indices = MemoryUtil.memAlloc((pentagons * 5 + hexagons * 6) * 3 * Int.SIZE_BYTES)

            for (i in 0 until dirtyTiles.size()) {
                val tile = dirtyTiles[i]

                val geometry = geomMapper.get(tile).apply {
                    this.vBufferPos = hexVerts.position()
                    this.iBufferPos = indices.position()
                }

                val color: Int = getTileColor(tile)
                val outline: Int = getTileOutlineColor(tile, color)
                val v1 = geometry.vBufferPos / bytesPerVert
                val numVerts = setTileVerts(geometry, color, outline)
                for (j in 0 until numVerts) {
                    indices.putInt(v1 + j).putInt(v1 + ((j + 1) % numVerts)).putInt(v1 + numVerts)
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
                val color: Int = getTileColor(tile)
                val outline: Int = getTileOutlineColor(tile, color)
                val v1 = geometry.vBufferPos / bytesPerVert
                hexVerts.position(geometry.vBufferPos)
                setTileVerts(geometry, color, outline)
                hexVerts.limit(hexVerts.position())
                hexVerts.position(geometry.vBufferPos)
                vertBuffer.update(hexVerts, autoFree = false, startVertex = v1)
                hexVerts.clear()
            }
        }

        Encoder.with {
            setState(Encoder.DEFAULT)
            setVertexBuffer(vertBuffer, vertLayoutHandle, verts)
            setIndexBuffer(indxBuffer!!, (pentagons * 5 + hexagons * 6) * 3)
            setTransform(model.get(modelBuffer))
            setUniform(outlineSettingsUniform, vec4a.set(0.0f, 0.0f, 0f, 0f).get(vec4Buffer))
            submit(renderDataSystem.hexasphereShader, renderDataSystem.hexasphereView)
        }
    }
}