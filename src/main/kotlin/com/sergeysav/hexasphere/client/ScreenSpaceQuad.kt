package com.sergeysav.hexasphere.client

import com.sergeysav.hexasphere.client.bgfx.BGFXUtil
import com.sergeysav.hexasphere.client.bgfx.Encoder
import com.sergeysav.hexasphere.client.bgfx.VertexAttribute
import com.sergeysav.hexasphere.client.bgfx.VertexLayout
import com.sergeysav.hexasphere.client.bgfx.VertexLayoutHandle
import com.sergeysav.hexasphere.client.bgfx.withTransientVertexBuffer
import org.lwjgl.system.MemoryStack

object ScreenSpaceQuad {
    private val vertexLayout: VertexLayout =
        VertexLayout.new(
            VertexAttribute(VertexAttribute.Attribute.POSITION, 3, VertexAttribute.Type.FLOAT),
            VertexAttribute(VertexAttribute.Attribute.TEXCOORD0, 2, VertexAttribute.Type.FLOAT)
        )

    private val vertexLayoutHandle: VertexLayoutHandle = VertexLayoutHandle.new(vertexLayout)

    fun setEncoderBuffers(encoder: Encoder,
                          textureWidth: Float,
                          textureHeight: Float,
                          originBottomLeft: Boolean = false,
                          width: Float = 1.0f,
                          height: Float = 1.0f,
                          stream: Int = 0
    ) {
        MemoryStack.stackPush().use { stack ->
            stack.withTransientVertexBuffer(3, vertexLayout) {
                val zz = 0.0f

                val minx = -width
                val maxx =  width
                val miny = 0.0f
                val maxy = height*2.0f

                val texelHalfW = BGFXUtil.texelHalf/textureWidth
                val texelHalfH = BGFXUtil.texelHalf/textureHeight
                val minu = -1.0f + texelHalfW
                val maxu =  1.0f + texelHalfW

                var minv = texelHalfH
                var maxv = 2.0f + texelHalfH

                if (originBottomLeft) {
                    val temp = minv
                    minv = maxv
                    maxv = temp

                    minv -= 1.0f
                    maxv -= 1.0f
                }

                val bytes = it.handle.data()
                bytes.putFloat(minx).putFloat(miny).putFloat(zz).putFloat(minu).putFloat(minv)
                bytes.putFloat(maxx).putFloat(miny).putFloat(zz).putFloat(maxu).putFloat(minv)
                bytes.putFloat(maxx).putFloat(maxy).putFloat(zz).putFloat(maxu).putFloat(maxv)
                bytes.flip()

                encoder.setVertexBuffer(it, vertexLayoutHandle, 3, stream, 0)
            }
        }
    }
}