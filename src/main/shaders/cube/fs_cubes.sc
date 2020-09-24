$input v_position, v_color0, v_color1

/*
 * Copyright 2011-2020 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common.shaderh"

void main()
{
    float value = 0.075;
    vec3 pos = (v_position);
    vec3 depth = v_color1;
    float thingy = (length(pos) - 1 + value/2)/value;
	gl_FragColor = v_color0; // vec4(thingy, thingy, thingy, 1.0); //
}
