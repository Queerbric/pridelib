#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0; // Center Position
in ivec2 UV2; // Radius

out vec2 pos;
out vec2 center_pos;
flat out ivec2 radius;
out vec4 vertex_color;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
	pos = Position.xy;

	vertex_color = Color;
	center_pos = UV0;
	radius = UV2;
}
