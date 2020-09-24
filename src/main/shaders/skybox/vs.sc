$input a_position
$output v_uv

#include "../common.shaderh"

void main()
{
    gl_Position = mul(u_modelViewProj, vec4(a_position, 1.0) );
    gl_Position.z = gl_Position.w;
    v_uv = a_position;
}
