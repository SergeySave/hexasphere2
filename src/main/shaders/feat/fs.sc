$input v_texcoord0

/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

SAMPLER2D(s_diffuse1, 0);

void main()
{
	gl_FragColor = vec4(texture2D(s_diffuse1, v_texcoord0).xyz, 1.0);
}
