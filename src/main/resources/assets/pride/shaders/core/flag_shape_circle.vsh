#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 ScreenSize;

out vec4 vertexColor;
out vec2 uv;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
	uv = Position.xy / ScreenSize;

	vertexColor = Color;
}
