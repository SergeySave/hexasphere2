$input a_position

/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

void main()
{
	gl_Position = mul(u_modelViewProj, vec4(a_position, 1.0) );
}