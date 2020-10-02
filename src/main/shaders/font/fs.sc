$input v_texcoord0

/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

SAMPLER2D(s_texture0, 0);
uniform vec4 u_color0;

void main()
{
	gl_FragColor = u_color0 * vec4(1.0, 1.0, 1.0, texture2D(s_texture0, v_texcoord0).r);
}
