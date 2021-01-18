$input a_position, a_color0, a_color1, a_texcoord1
$output v_color0, v_color1, v_texcoord1

#include "../common.shaderh"

void main()
{
	gl_Position = mul(u_modelViewProj, vec4(a_position, 1.0) );
	v_color0 = a_color0;
	v_color1 = a_color1;
	v_texcoord1 = a_texcoord1;
}
