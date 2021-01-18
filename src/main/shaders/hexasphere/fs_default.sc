$input v_color0, v_color1, v_texcoord1

#include "../common.shaderh"

uniform vec4 u_outlineSettings;

void main()
{
    float threshold = 1.0 - u_outlineSettings.r - v_color1.a;
    float smoothing = u_outlineSettings.g;
    vec4 outlineColor = vec4(v_color1.rgb, 1.0);

    float outlineFactor = smoothstep(threshold - smoothing, threshold + smoothing, v_texcoord1);
	gl_FragColor = mix(v_color0, outlineColor, outlineFactor);
}
