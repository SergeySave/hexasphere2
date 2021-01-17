$input v_texcoord0

#include "../common.shaderh"

SAMPLER2D(s_diffuse1, 0);

void main()
{
	gl_FragColor = vec4(texture2D(s_diffuse1, v_texcoord0).xyz, 1.0);
}
