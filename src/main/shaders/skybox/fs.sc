$input v_uv

#include "../common.shaderh"

SAMPLERCUBE(s_texCube, 0);

float toSrgbGamma(float _val)
{
	if (_val <= 0.0031308)
	{
		return 12.92 * _val;
	}
	else
	{
		return 1.055 * pow(_val, (1.0/2.4) ) - 0.055;
	}
}

vec3 toSrgbGamma(vec3 _rgb)
{
	_rgb.x = toSrgbGamma(_rgb.x);
	_rgb.y = toSrgbGamma(_rgb.y);
	_rgb.z = toSrgbGamma(_rgb.z);
	return _rgb;
}

void main()
{
    float exposure = 0.0;

    vec3 color = textureCube(s_texCube, v_uv).xyz * pow(2.0, exposure);

    color = toSrgbGamma(saturate(color));

    gl_FragColor = vec4(color, 1.0);
}
