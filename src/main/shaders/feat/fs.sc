
/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

uniform vec4 u_color;

void main()
{
	gl_FragColor = u_color;// vec4(1.0, 0.5, 0.0, 1.0);
}
