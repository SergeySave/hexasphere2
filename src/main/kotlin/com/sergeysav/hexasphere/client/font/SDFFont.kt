package com.sergeysav.hexasphere.client.font

import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.ShaderProgram
import com.sergeysav.hexasphere.client.bgfx.Texture
import com.sergeysav.hexasphere.client.bgfx.Uniform
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.VertexLayoutHandle
import com.sergeysav.hexasphere.client.bgfx.View
import com.sergeysav.hexasphere.client.bgfx.withTransientIndexBuffer
import com.sergeysav.hexasphere.client.bgfx.withTransientVertexBuffer
import com.sergeysav.hexasphere.common.Vectors
import org.joml.Vector4fc
import org.lwjgl.bgfx.BGFX
import org.lwjgl.stb.STBImageWrite
import org.lwjgl.stb.STBRPContext
import org.lwjgl.stb.STBRPNode
import org.lwjgl.stb.STBRPRect
import org.lwjgl.stb.STBRectPack
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class SDFFont(
    ttf: ByteBuffer,
    private val pixelHeight: Float,
    private val bitmapSize: Int,
    private val padding: Int = 5,
    private val onEdgeValue: Int = 180,
    private val characters: String = DEFAULT_CHARACTERS
) : STBFont(ttf) {

    private val pixelDistScale: Float = onEdgeValue.toFloat() / padding
    private val packData = mutableMapOf<Int, PackData>()

    init {
        MemoryStack.stackPush().use { stack ->
            scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, pixelHeight)

            val a = stack.mallocInt(1)
            val d = stack.mallocInt(1)
            val lg = stack.mallocInt(1)
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, a, d, lg)
            ascent = a.get(0) * scale
            descent = d.get(0) * scale
            lineGap = lg.get(0) * scale

            val bitmap = MemoryUtil.memAlloc(bitmapSize * bitmapSize)

            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val xoff = stack.mallocInt(1)
            val yoff = stack.mallocInt(1)

            val packingContext = STBRPContext.mallocStack()
            val packingNodes = STBRPNode.mallocStack(bitmapSize)
            STBRectPack.stbrp_init_target(packingContext, bitmapSize, bitmapSize, packingNodes)
            STBRectPack.stbrp_setup_allow_out_of_mem(packingContext, true)
            STBRectPack.stbrp_setup_heuristic(packingContext, STBRectPack.STBRP_HEURISTIC_Skyline_BL_sortHeight)

            val packingBuffer = STBRPRect.mallocStack(characters.length)
            for (i in characters.indices) {
                STBTruetype.stbtt_FreeSDF(STBTruetype.stbtt_GetCodepointSDF(
                    fontInfo, scale, characters[i].toInt(), padding,
                    onEdgeValue.toByte(), pixelDistScale, w, h, xoff, yoff
                )!!)
                packingBuffer[i].id(characters[i].toInt())
                    .w(w[0].toShort())
                    .h(h[0].toShort())
                    .x(0.toShort())
                    .y(0.toShort())
                    .was_packed(false)
            }

            if (STBRectPack.stbrp_pack_rects(packingContext, packingBuffer) == 0) {
                error("Failed to pack")
            } else {
                for (j in characters.indices) {
                    val rect = packingBuffer[j]
                    val codepoint = rect.id()
                    val x = rect.x()
                    val y = rect.y()
                    val wasPacked = rect.was_packed()
                    if (wasPacked) {
                        val sdf = STBTruetype.stbtt_GetCodepointSDF(
                            fontInfo, scale, codepoint, padding,
                            onEdgeValue.toByte(), pixelDistScale, w, h, xoff, yoff
                        ) ?: error("Failed to produced sdf")

                        for (i in 0 until h[0]) {
                            val bitmapSlice = bitmap.slice((y + i) * bitmapSize + x, w[0])
                            bitmapSlice.put(sdf.slice(i * w[0], w[0]))
                        }

                        packData[codepoint] = PackData(
                            x.toFloat() / bitmapSize, y.toFloat() / bitmapSize,
                            (x + w[0]).toFloat() / bitmapSize, (y + h[0]).toFloat() / bitmapSize,
                            w[0], h[0],
                            xoff[0], yoff[0]
                        )

                        STBTruetype.stbtt_FreeSDF(sdf)
                    } else {
                        println("Failed to pack codepoint $codepoint / ${codepoint.toChar()}")
                    }
                }
            }

            STBImageWrite.stbi_flip_vertically_on_write(false)
            STBImageWrite.stbi_write_png("sdf.png", bitmapSize, bitmapSize, 1, bitmap, bitmapSize)

            val format =  BGFX.BGFX_TEXTURE_FORMAT_R8
            val bgfxMemory = BGFX.bgfx_make_ref_release(bitmap, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL)
            texture = Texture(BGFX.bgfx_create_texture_2d(bitmapSize, bitmapSize, false, 1, format, BGFX.BGFX_TEXTURE_NONE, bgfxMemory))
        }
    }

    override fun computeHeight(text: CharSequence): Double {
        var height = 0.0

        for (char in text) {
            height = maxOf(height, (packData[char.toInt()]?.h?.toDouble() ?: 0.0) - padding * 2)
        }

        return height
    }

    fun render(
        encoder: Encoder,
        view: View,
        x: Double,
        y: Double,
        text: CharSequence,
        color: Vector4fc = Vectors.ONE4f,
        outlineColor: Vector4fc = color,
        depth: Int = 0,
        drawScale: Float = 1f,
        extraThickness: Float = 0f,
        outlineThickness: Float = 0f
    ) {
        MemoryStack.stackPush().use { stack ->
            stack.withTransientVertexBuffer(text.length * 4, VERTEX_LAYOUT) { tvb ->
                stack.withTransientIndexBuffer(text.length * 6) { tib ->
                    var xPos = x
                    val yPos = y

                    val vert = tvb.handle.data()
                    val indx = tib.handle.data()

                    val advance = stack.mallocInt(1)
                    val bearing = stack.mallocInt(1)
                    var vertPos = 0

                    for (char in text) {
                        val pack = packData[char.toInt()]
                        STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, char.toInt(), advance, bearing)
                        val x1This = xPos + bearing[0] * scale * drawScale //+ (bearing.get(0) - (pack?.x0 ?: 0)) * scale * drawScale
                        val y1This = yPos + (descent * scale - (pack?.y0 ?: 0)) * drawScale //- ((pack?.y0 ?: 0) + descent * scale) * drawScale
                        val x2This = x1This + (pack?.w ?: 0) * drawScale
                        val y2This = y1This - (pack?.h ?: 0) * drawScale
                        vert.putFloat(x1This.toFloat())
                            .putFloat(y1This.toFloat())
                            .putFloat(pack?.s0 ?: 0f)
                            .putFloat(pack?.t0 ?: 0f)
                        vert.putFloat(x2This.toFloat())
                            .putFloat(y1This.toFloat())
                            .putFloat(pack?.s1 ?: 0f)
                            .putFloat(pack?.t0 ?: 0f)
                        vert.putFloat(x2This.toFloat())
                            .putFloat(y2This.toFloat())
                            .putFloat(pack?.s1 ?: 0f)
                            .putFloat(pack?.t1 ?: 0f)
                        vert.putFloat(x1This.toFloat())
                            .putFloat(y2This.toFloat())
                            .putFloat(pack?.s0 ?: 0f)
                            .putFloat(pack?.t1 ?: 0f)
                        indx.putShort((vertPos + 0).toShort())
                            .putShort((vertPos + 1).toShort())
                            .putShort((vertPos + 2).toShort())
                        indx.putShort((vertPos + 2).toShort())
                            .putShort((vertPos + 3).toShort())
                            .putShort((vertPos + 0).toShort())
                        xPos += advance.get(0) * scale * drawScale
                        vertPos += 4
                    }

                    vert.flip()
                    indx.flip()

                    val colorBuffer = stack.mallocFloat(4)

                    encoder.apply {
                        setState(Encoder.TEXT)
                        setTexture(0, UNIFORM_TEXTURE, texture)
                        setUniform(UNIFORM_BASE_COLOR, color.get(colorBuffer))
                        setUniform(UNIFORM_OUTLINE_COLOR, outlineColor.get(colorBuffer))
                        setUniform(UNIFORM_RENDER_SETTINGS, stack.floats(
                            onEdgeValue / 255f - (pixelDistScale / 255f) * extraThickness,
                            0.5f / (padding * drawScale),
                            onEdgeValue / 255f - (pixelDistScale / 255f) * (extraThickness + outlineThickness),
                            0f
                        ))
                        setVertexBuffer(tvb, VERTEX_LAYOUT_HANDLE, text.length * 4)
                        setIndexBuffer(tib, text.length * 6)
                        submit(SHADER, view.id, depth = depth)
                    }
                }
            }
        }
    }

    data class PackData(val s0: Float, val t0: Float, val s1: Float, val t1: Float, val w: Int, val h: Int, val x0: Int, val y0: Int)

    companion object {
        val DEFAULT_CHARACTERS = buildString {
            for (c in 0x21..0x7E) append(c.toChar())
        }
        private val UNIFORM_TEXTURE = Uniform.new("s_texture0", Uniform.Type.SAMPLER)
        private val UNIFORM_BASE_COLOR = Uniform.new("u_baseColor", Uniform.Type.VEC4)
        private val UNIFORM_OUTLINE_COLOR = Uniform.new("u_outlineColor", Uniform.Type.VEC4)
        private val UNIFORM_RENDER_SETTINGS = Uniform.new("u_renderSettings", Uniform.Type.VEC4)
        private val VERTEX_LAYOUT = VertexLayout.new(
            VertexAttribute(VertexAttribute.Attribute.POSITION, 2, VertexAttribute.Type.FLOAT),
            VertexAttribute(VertexAttribute.Attribute.TEXCOORD0, 2, VertexAttribute.Type.FLOAT)
        )
        private val VERTEX_LAYOUT_HANDLE = VertexLayoutHandle.new(VERTEX_LAYOUT)
        private val SHADER = ShaderProgram.loadFromFiles("/sdffont/vs", "/sdffont/fs")
    }
}