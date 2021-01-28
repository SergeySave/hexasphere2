$input a_position, a_color0
$output v_color0

#include "../common.shaderh"

void main()
{
	gl_Position = mul(u_viewProj, vec4(a_position, 1.0));
	v_color0 = a_color0;
}
