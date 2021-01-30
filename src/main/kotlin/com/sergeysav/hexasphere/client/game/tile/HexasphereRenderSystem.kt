package com.sergeysav.hexasphere.client.game.tile

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.encoder.Encoder
import com.sergeysav.hexasphere.client.bgfx.index.StaticIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.uniform.Uniform
import com.sergeysav.hexasphere.client.bgfx.vertex.DynamicVertexBuffer
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.vertex.VertexLayoutHandle
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.game.selection.SelectionSystem
import com.sergeysav.hexasphere.common.color.Color
import com.sergeysav.hexasphere.common.color.putRGB
import com.sergeysav.hexasphere.common.color.putRGBA
import com.sergeysav.hexasphere.common.game.Groups
import com.sergeysav.hexasphere.common.game.tile.TileComponent
import com.sergeysav.hexasphere.common.game.tile.TileOwnerSystem
import com.sergeysav.hexasphere.common.game.tile.type.TileTypeSystem
import com.sergeysav.hexasphere.common.setColor
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
        VertexAttribute(VertexAttribute.Attribute.COLOR2, 4, VertexAttribute.Type.UINT8, true),
        VertexAttribute(VertexAttribute.Attribute.COLOR3, 2, VertexAttribute.Type.FLOAT),
        VertexAttribute(VertexAttribute.Attribute.TEXCOORD1, 1, VertexAttribute.Type.UINT8, true)
    )
    private val vertLayoutHandle = VertexLayoutHandle.new(vertLayout)
    private val bytesPerVert = (3) * Float.SIZE_BYTES +
            (4) * Byte.SIZE_BYTES +
            (4) * Byte.SIZE_BYTES +
            (4) * Byte.SIZE_BYTES +
            (2) * Float.SIZE_BYTES +
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
    private val outlineSettingsAUniform = Uniform.new("u_outlineSettingsA", Uniform.Type.VEC4)
    private val outlineSettingsBUniform = Uniform.new("u_outlineSettingsB", Uniform.Type.VEC4)

    private lateinit var geomMapper: ComponentMapper<TileGeometryComponent>
    private lateinit var tileMapper: ComponentMapper<TileComponent>
    private lateinit var groupManager: GroupManager
    private lateinit var tileTypeSystem: TileTypeSystem
    private lateinit var selectionSystem: SelectionSystem
    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var tileOwnerSystem: TileOwnerSystem

    override fun dispose() {
        super.dispose()
        vertLayout.dispose()
        vertLayoutHandle.dispose()
        MemoryUtil.memFree(hexVerts)
        vertBuffer.dispose()
        indxBuffer?.dispose()
        MemoryUtil.memFree(modelBuffer)
        MemoryUtil.memFree(vec4Buffer)
        outlineSettingsAUniform.dispose()
        outlineSettingsBUniform.dispose()
    }

    private fun setTileVerts(geometry: TileGeometryComponent, tile: Int): Int {
        val numVerts = if (geometry.vertices[5].lengthSquared() >= 1e-4) 6 else 5
        val color: Color = (tileTypeSystem.getTileType(tile)?.color ?: Color.BLACK)

        vec3a.zero()
        for (j in 0 until numVerts) {
            val vert = geometry.vertices[j]
            vec3a.add(vert)

            var outer = Color.ZERO
            var outerThickness = 0f
            var inner = Color.ZERO
            var innerThickness = 0f

            if (tileOwnerSystem.getTileOwner(tile) != tileOwnerSystem.getTileOwner(tileMapper[tile].adjacent[j])) {
                outer = (tileOwnerSystem.getTileOwner(tile)?.primaryColor?.alpha(1f) ?: Color.ZERO)
                outerThickness = if (outer != Color.ZERO) 0.02f else 0f
                inner = (tileOwnerSystem.getTileOwner(tile)?.secondaryColor?.alpha(0.5f) ?: Color.ZERO)
                innerThickness = if (inner != Color.ZERO) 0.02f else 0f
            }

            if (selectionSystem.selectedTile == tile) {
                outer = Color.WHITE.alpha(1f)
                outerThickness = 0.02f
            }

            if (selectionSystem.mouseoverTile == tile) {
                inner = (Color.WHITE xor color).alpha(1f)
                innerThickness = 0.02f
            }

            hexVerts.putFloat(vert.x().toFloat())
                .putFloat(vert.y().toFloat())
                .putFloat(vert.z().toFloat())
                .putRGB(color).put(0.toByte())
                .putRGBA(outer)
                .putRGBA(inner)
                .putFloat(outerThickness)
                .putFloat(innerThickness)
                .put((-1).toByte())
        }
        vec3a.div(numVerts.toDouble())
        hexVerts.putFloat(vec3a.x().toFloat())
            .putFloat(vec3a.y().toFloat())
            .putFloat(vec3a.z().toFloat())
            .putRGB(color).put(0.toByte())
            .putRGBA(Color.ZERO) // These values are unused because of flat interpolation
            .putRGBA(Color.ZERO)
            .putFloat(0f)
            .putFloat(0f)
            .put(0.toByte())

        return numVerts
    }

    override fun processSystem() {
        val dirtyTiles = groupManager.getEntityIds(Groups.DIRTY_TILE)

        if (indxBuffer == null) {
            val indices = MemoryUtil.memAlloc((pentagons * 5 + hexagons * 6) * 3 * Int.SIZE_BYTES)

            for (i in 0 until dirtyTiles.size()) {
                val tile = dirtyTiles[i]

                val geometry = geomMapper.get(tile).apply {
                    this.vBufferPos = hexVerts.position()
                    this.iBufferPos = indices.position()
                }

                val v1 = geometry.vBufferPos / bytesPerVert
                val numVerts = setTileVerts(geometry, tile)
                for (j in 0 until numVerts) {
                    // Note: Getting a newer version of BGFX will remove the need for this
                    if (BGFXUtil.firstProvokingVertex) {
                        indices.putInt(v1 + j).putInt(v1 + ((j + 1) % numVerts)).putInt(v1 + numVerts)
                    } else {
                        indices.putInt(v1 + ((j + 1) % numVerts)).putInt(v1 + numVerts).putInt(v1 + j)
                    }
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
               val v1 = geometry.vBufferPos / bytesPerVert
                hexVerts.position(geometry.vBufferPos)
                setTileVerts(geometry, tile)
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
            setGlobalOutlineData(Color.BLACK.alpha(0.1f), 0.02f, 0f, 0.01f)
            submit(renderDataSystem.hexasphereShader, renderDataSystem.hexasphereView)
        }
    }

    private fun Encoder.setGlobalOutlineData(color: Color, thickness: Float, borderBaseSmoothing: Float, borderBorderSmoothing: Float) {
        setUniform(outlineSettingsAUniform, vec4a.setColor(color).get(vec4Buffer))
        setUniform(outlineSettingsBUniform, vec4a.set(thickness, borderBaseSmoothing, borderBorderSmoothing, 0f).get(vec4Buffer))
    }
}