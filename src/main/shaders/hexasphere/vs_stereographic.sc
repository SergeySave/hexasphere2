$input a_position, a_color0, a_color1, a_color2, a_color3, a_texcoord1
$output v_color0, v_color1, v_color2, v_color3, v_texcoord1

#include "../stereographic.shaderh"

void main()
{
    vec4 worldPos = mul(u_model[0], vec4(a_position, 1.0));
	gl_Position = computeStereographicPosition(worldPos);
	v_color0 = a_color0.rgb;
	v_color1 = a_color1;
	v_color2 = a_color2;
	v_color3 = a_color3.rg;
	v_texcoord1 = a_texcoord1;
}
