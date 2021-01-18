$input a_position, a_texcoord0, i_data0, i_data1, i_data2, i_data3
$output v_texcoord0

#include "../stereographic.shaderh"

void main()
{
    mat4 model;
	model[0] = i_data0;
	model[1] = i_data1;
	model[2] = i_data2;
	model[3] = i_data3;

	vec4 worldPos = instMul(model, vec4(a_position, 1.0));
	gl_Position = computeStereographicPosition(worldPos);
	v_texcoord0 = a_texcoord0;
}
