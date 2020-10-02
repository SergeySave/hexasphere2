$input v_color0

/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

void main()
{
	gl_FragColor = vec4(v_color0, 1.0);
}
