$input v_texcoord0

#include "../common.shaderh"

SAMPLER2D(s_texture0, 0);
uniform vec4 u_baseColor;
uniform vec4 u_outlineColor;
uniform vec4 u_renderSettings;

void main()
{
    float threshold = u_renderSettings.r;
    float smoothing = u_renderSettings.g;
    float outlineThreshold = u_renderSettings.b;

    float distance = texture2D(s_texture0, v_texcoord0).r;
    float outlineFactor = smoothstep(threshold - smoothing, threshold + smoothing, distance);
    vec4 color = mix(u_outlineColor, u_baseColor, outlineFactor);

    float alpha = smoothstep(outlineThreshold - smoothing, outlineThreshold + smoothing, distance);

	gl_FragColor = vec4(color.rgb, color.a * alpha);
}
