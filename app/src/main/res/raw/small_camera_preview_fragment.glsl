#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES v_Texture;
varying vec2 ft_Position;
uniform int filter_Type;


vec4 get_Color() {
    if(filter_Type == 1) {
        vec4 color = texture2D(v_Texture, ft_Position);
        float gray = 0.3 * color.r + 0.59 * color.g + 0.11 * color.b;
        return vec4(gray, gray, gray, 1.0);
    }
    return texture2D(v_Texture, ft_Position);
}

void main() {
    gl_FragColor = get_Color();
}
