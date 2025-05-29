#version 150

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 in_position;
in vec4 in_color;
in vec2 in_center_pos;
in ivec2 in_radius;

out vec2 pos;
out vec2 center_pos;
flat out ivec2 radius;
out vec4 vertex_color;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(in_position, 1.0);
	pos = in_position.xy;

	vertex_color = in_color;
	center_pos = in_center_pos;
	radius = in_radius;
}
