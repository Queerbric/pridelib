#version 330

#moj_import <minecraft:dynamictransforms.glsl>

in vec2 pos;
in vec2 center_pos;
flat in ivec2 radius;
in vec4 vertex_color;

out vec4 fragColor;

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
