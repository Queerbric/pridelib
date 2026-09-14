#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 center_pos;
layout (location = 2) flat in ivec2 radius;
layout (location = 3) in vec4 vertex_color;

layout (location = 0) out vec4 fragColor;

void main() {
	float outer_radius = radius.x;
	float inner_radius = radius.x - radius.y;

	float distance = distance(pos, center_pos);
	float aa = fwidth(distance);

	float outer_factor = 1.0 - smoothstep(outer_radius - aa, outer_radius, distance);
	float inner_factor = smoothstep(inner_radius - aa, inner_radius, distance);
	float factor = outer_factor * inner_factor;

	fragColor = vec4(vertex_color.rgb, factor) * ColorModulator;
}
