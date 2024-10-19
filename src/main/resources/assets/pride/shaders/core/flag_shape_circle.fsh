#version 150

in vec4 vertexColor;
in vec2 uv;

uniform vec2 center_pos;
uniform vec2 radius;
uniform vec2 ScreenSize;

out vec4 fragColor;

void main() {
	float outer_radius = radius.x;
	float inner_radius = radius.x - radius.y;

	float distance = distance(uv * ScreenSize, center_pos);
	float aa = fwidth(distance);

	float outer_factor = 1.0 - smoothstep(outer_radius - aa, outer_radius, distance);
	float inner_factor = smoothstep(inner_radius - aa, inner_radius, distance);
	float factor = outer_factor * inner_factor;

	fragColor = vec4(vertexColor.rgb, factor);
}
