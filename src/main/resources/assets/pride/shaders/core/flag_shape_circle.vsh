#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec2 UV0; // Center Position
layout (location = 3) in ivec2 UV2; // Radius

layout (location = 0) out vec2 pos;
layout (location = 1) out vec2 center_pos;
layout (location = 2) flat out ivec2 radius;
layout (location = 3) out vec4 vertex_color;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
	pos = Position.xy;

	vertex_color = Color;
	center_pos = UV0;
	radius = UV2;
}
