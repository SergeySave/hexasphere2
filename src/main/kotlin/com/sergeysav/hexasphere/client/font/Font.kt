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
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class Font(ttf: ByteBuffer, private val fontSize: Float, private val bitmapSize: Int) {

    private val fontInfo = STBTTFontinfo.malloc()
    private val cdata: STBTTPackedchar.Buffer = STBTTPackedchar.malloc(95)
    private val scale: Float
    private val descent: Float
    private val texture: Texture
    private val samplerUniform = Uniform.new("s_texture0", Uniform.Type.SAMPLER)
    private val colorUniform = Uniform.new("u_color0", Uniform.Type.VEC4)
    private val vertexLayout = VertexLayout.new(
        VertexAttribute(VertexAttribute.Attribute.POSITION, 2, VertexAttribute.Type.FLOAT),
        VertexAttribute(VertexAttribute.Attribute.TEXCOORD0, 2, VertexAttribute.Type.FLOAT)
    )
    private val vertexLayoutHandle = VertexLayoutHandle.new(vertexLayout)
    private val shader = ShaderProgram.loadFromFiles("/font/vs", "/font/fs")

    init {
        MemoryStack.stackPush().use { stack ->
            if (!STBTruetype.stbtt_InitFont(fontInfo, ttf)) error("Failed to initialize STB TTF")
            scale = STBTruetype.stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize)

            val d = stack.mallocInt(1)
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, null, d, null)
            descent = d.get(0) * scale

            val bitmap = MemoryUtil.memAlloc(bitmapSize * bitmapSize)

            val packContext = STBTTPackContext.mallocStack(stack)
            if (!STBTruetype.stbtt_PackBegin(packContext, bitmap, bitmapSize, bitmapSize, 0, 1, MemoryUtil.NULL)) error("Failed to start packing")
            STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2)
            if (!STBTruetype.stbtt_PackFontRange(packContext, ttf, 0, -fontSize, 32, cdata)) error("Failed to pack font")
            STBTruetype.stbtt_PackEnd(packContext)

            val format =  BGFX.BGFX_TEXTURE_FORMAT_R8
            val bgfxMemory = BGFX.bgfx_make_ref_release(bitmap, BGFXUtil.releaseMemoryCb, MemoryUtil.NULL)
            texture = Texture(BGFX.bgfx_create_texture_2d(bitmapSize, bitmapSize, false, 1, format, BGFX.BGFX_TEXTURE_NONE, bgfxMemory))
        }
    }

    fun computeWidth(text: CharSequence): Double {
        return MemoryStack.stackPush().use { stack ->
            var width = 0.0
            val advance = stack.mallocInt(1)

            for (char in text) {
                STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, char.toInt(), advance, null)
                width += advance.get(0) * scale.toDouble()
            }

            width
        }
    }

    fun computeHeight(text: CharSequence): Double {
        return MemoryStack.stackPush().use { stack ->
            var height = 0.0
            val quad = STBTTAlignedQuad.mallocStack(stack)
            val xBuf = stack.floats(0.0f)
            val yBuf = stack.floats(0.0f)

            for (char in text) {
                STBTruetype.stbtt_GetPackedQuad(cdata, bitmapSize, bitmapSize, (char - 32).toInt(), xBuf, yBuf, quad, false)
                height = maxOf(height, quad.y1().toDouble() - quad.y0())
            }

            height
        }
    }

    fun render(encoder: Encoder, view: View, x: Double, y: Double, text: CharSequence, color: Vector4fc = Vectors.ONE4f, depth: Int = 0) {
        MemoryStack.stackPush().use { stack ->
            stack.withTransientVertexBuffer(text.length * 4, vertexLayout) { tvb ->
                stack.withTransientIndexBuffer(text.length * 6) { tib ->
                    var xPos = x
                    val yPos = y

                    val vert = tvb.handle.data()
                    val indx = tib.handle.data()

                    val xBuf = stack.floats(0.0f)
                    val yBuf = stack.floats(0.0f)
                    val quad = STBTTAlignedQuad.mallocStack(stack)
                    val advance = stack.mallocInt(1)
                    val bearing = stack.mallocInt(1)
                    var vertPos = 0

                    for (char in text) {
                        STBTruetype.stbtt_GetPackedQuad(cdata, bitmapSize, bitmapSize, (char - 32).toInt(), xBuf, yBuf, quad, false)
                        STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, char.toInt(), advance, bearing)
                        val width = advance.get(0) * scale
                        val height = quad.y1() - quad.y0()
                        val x1This = xPos + bearing.get(0) * scale
                        val y1This = yPos - quad.y0() + descent * scale
                        val x2This = x1This + (quad.x1() - quad.x0())
                        val y2This = y1This - height
                        vert.putFloat(x1This.toFloat())
                            .putFloat(y1This.toFloat())
                            .putFloat(quad.s0())
                            .putFloat(quad.t0())
                        vert.putFloat(x2This.toFloat())
                            .putFloat(y1This.toFloat())
                            .putFloat(quad.s1())
                            .putFloat(quad.t0())
                        vert.putFloat(x2This.toFloat())
                            .putFloat(y2This.toFloat())
                            .putFloat(quad.s1())
                            .putFloat(quad.t1())
                        vert.putFloat(x1This.toFloat())
                            .putFloat(y2This.toFloat())
                            .putFloat(quad.s0())
                            .putFloat(quad.t1())
                        indx.putShort((vertPos + 0).toShort())
                            .putShort((vertPos + 1).toShort())
                            .putShort((vertPos + 2).toShort())
                        indx.putShort((vertPos + 2).toShort())
                            .putShort((vertPos + 3).toShort())
                            .putShort((vertPos + 0).toShort())
                        xPos += width
                        vertPos += 4
                    }

                    vert.flip()
                    indx.flip()

                    val colorBuffer = stack.mallocFloat(4)

                    encoder.apply {
                        setState(Encoder.TEXT)
                        setTexture(0, samplerUniform, texture)
                        setUniform(colorUniform, color.get(colorBuffer))
                        setVertexBuffer(tvb, vertexLayoutHandle, text.length * 4)
                        setIndexBuffer(tib, text.length * 6)
                        submit(shader, view.id, depth = depth)
                    }
                }
            }
        }
    }

    fun dispose() {
        fontInfo.free()
        cdata.free()
        texture.dispose()
        samplerUniform.dispose()
        vertexLayoutHandle.dispose()
        vertexLayout.dispose()
        shader.dispose()
        colorUniform.dispose()
    }
}