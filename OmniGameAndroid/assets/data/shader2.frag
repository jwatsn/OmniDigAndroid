+"attribute vec4 a_position;\n"
+"attribute vec4 a_color;\n"
+"attribute vec2 a_texCoord0;\n"
+"attribute vec2 a_texCoord1;\n"

+"uniform mat4 u_projTrans;\n"

+"varying vec4 v_color;\n"
+"varying vec2 v_texCoords;\n"
+"varying vec2 v_texCoords2;\n"

+"void main() {\n"

+"	v_color = a_color;\n"
+"	v_texCoords = a_texCoord0;\n"
+"	v_texCoords2 = a_texCoord1;\n"
+"	gl_Position =  u_projTrans * a_position;\n"
"}\n"