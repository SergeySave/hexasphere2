$input v_uv

#include "../common.shaderh"

SAMPLERCUBE(s_texCube, 0);

void main()
{
    gl_FragColor = vec4(textureCube(s_texCube, v_uv).xyz, 1.0);
}
