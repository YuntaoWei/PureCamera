#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES v_Texture;
varying vec2 ft_Position;
uniform int filter_Type;

vec4 get_Color() {
    return texture2D(v_Texture, ft_Position);
}

void main() {
    gl_FragColor = get_Color();
}
