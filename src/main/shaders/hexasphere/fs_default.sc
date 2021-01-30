$input v_color0, v_color1, v_color2, v_color3, v_texcoord1

#include "../common.shaderh"

uniform vec4 u_outlineSettingsA;
uniform vec4 u_outlineSettingsB;

vec3 blendToColorAtThickness(vec3 baseColor, vec4 topColor, float radialCoord, float thickness, float smoothing) {
    float threshold = 1.0 - thickness;
    float factor = smoothstep(threshold - smoothing, threshold + smoothing, radialCoord);
    return mixColorCorrected(baseColor, topColor.rgb, topColor.a * factor);
}

float blendSmoothnessAtThickness(float baseSmooth, float newSmooth, float radialCoord, float thickness, float smoothing) {
    float threshold = 1.0 - thickness;
    float factor = smoothstep(threshold - smoothing, threshold + smoothing, radialCoord);
    return mix(baseSmooth, newSmooth, factor);
}

void main()
{
    // Extract all of the variables
    vec3 baseColor = v_color0.rgb;

    vec4 outerOutlineColor = v_color1.rgba;
    float outerOutlineThickness = v_color3.r;

    vec4 innerOutlineColor = v_color2.rgba;
    float innerOutlineThickness = v_color3.g;

    vec4 globalOutlineColor = u_outlineSettingsA.rgba;
    float globalOutlineThickness = u_outlineSettingsB.r;

    float borderBaseSmoothing = u_outlineSettingsB.g;
    float borderBorderSmoothing = u_outlineSettingsB.b;
    float radialCoord = v_texcoord1;

    // Start with the base color
    vec3 color = baseColor;
    float smoothing = borderBaseSmoothing;
    // Apply the global border first
    color = blendToColorAtThickness(color, globalOutlineColor, radialCoord, globalOutlineThickness, smoothing);

    // Then apply the inner border
    color = blendToColorAtThickness(color, innerOutlineColor, radialCoord, innerOutlineThickness + outerOutlineThickness, smoothing);
    smoothing = blendSmoothnessAtThickness(smoothing, borderBorderSmoothing, radialCoord, innerOutlineThickness + outerOutlineThickness, 0.0);

    // Then apply the outer border
    color = blendToColorAtThickness(color, outerOutlineColor, radialCoord, outerOutlineThickness, smoothing);

    // Output the color
	gl_FragColor = vec4(color, 1.0);
}
