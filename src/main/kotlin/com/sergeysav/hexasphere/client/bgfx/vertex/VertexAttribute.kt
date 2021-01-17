package com.sergeysav.hexasphere.client.bgfx.vertex

import com.sergeysav.hexasphere.client.bgfx.BGFXException
import org.lwjgl.bgfx.BGFX

data class VertexAttribute(
    val attribute: Attribute,
    val num: Int,
    val type: Type,
    val normalized: Boolean = false,
    val packAsInt: Boolean = false
) {

    init {
        if (num < 1 || num > 4) throw BGFXException("Invalid Vertex Attribute")
    }

    enum class Attribute(val bgfxValue: Int) {
        POSITION(BGFX.BGFX_ATTRIB_POSITION),
        NORMAL(BGFX.BGFX_ATTRIB_NORMAL),
        TANGENT(BGFX.BGFX_ATTRIB_TANGENT),
        BITANGENT(BGFX.BGFX_ATTRIB_BITANGENT),
        COLOR0(BGFX.BGFX_ATTRIB_COLOR0),
        COLOR1(BGFX.BGFX_ATTRIB_COLOR1),
        COLOR2(BGFX.BGFX_ATTRIB_COLOR2),
        COLOR3(BGFX.BGFX_ATTRIB_COLOR3),
        INDICES(BGFX.BGFX_ATTRIB_INDICES),
        WEIGHT(BGFX.BGFX_ATTRIB_WEIGHT),
        TEXCOORD0(BGFX.BGFX_ATTRIB_TEXCOORD0),
        TEXCOORD1(BGFX.BGFX_ATTRIB_TEXCOORD1),
        TEXCOORD2(BGFX.BGFX_ATTRIB_TEXCOORD2),
        TEXCOORD3(BGFX.BGFX_ATTRIB_TEXCOORD3),
        TEXCOORD4(BGFX.BGFX_ATTRIB_TEXCOORD4),
        TEXCOORD5(BGFX.BGFX_ATTRIB_TEXCOORD5),
        TEXCOORD6(BGFX.BGFX_ATTRIB_TEXCOORD6),
        TEXCOORD7(BGFX.BGFX_ATTRIB_TEXCOORD7),
    }

    enum class Type(val bgfxValue: Int) {
        UINT8(BGFX.BGFX_ATTRIB_TYPE_UINT8),
        UINT10(BGFX.BGFX_ATTRIB_TYPE_UINT10),
        INT16(BGFX.BGFX_ATTRIB_TYPE_INT16),
        HALF(BGFX.BGFX_ATTRIB_TYPE_HALF),
        FLOAT(BGFX.BGFX_ATTRIB_TYPE_FLOAT),
    }
}