$input a_position, a_color0
$output v_color0

#include "../stereographic.shaderh"

void main()
{
    vec4 worldPos = mul(u_model[0], vec4(a_position, 1.0));
	gl_Position = computeStereographicPosition(worldPos);
	v_color0 = a_color0;
}
