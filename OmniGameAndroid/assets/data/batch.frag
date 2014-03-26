#ifdef GL_ES 
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec4 v_color;
vec4 v_color2;
varying vec2 v_texCoords;

void main() { 
		
	gl_FragColor = texture2D(u_texture, v_texCoords);
	gl_FragColor.rgb  *= v_color.a;
}