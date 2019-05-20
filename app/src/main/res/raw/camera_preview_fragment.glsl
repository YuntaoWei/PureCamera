#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES v_Texture;
varying vec2 ft_Position;
uniform int filter_Type;

uniform float texture_Width;
uniform float texture_Height;
uniform float mosaic_Size;

vec4 get_original_Color() {
    return texture2D(v_Texture, ft_Position);
}

vec4 get_gray_filter_color(vec4 original_color) {
    float gray = 0.3 * original_color.r + 0.59 * original_color.g + 0.11 * original_color.b;
    return vec4(gray, gray, gray, 1.0);
}

vec4 get_relief_filter_color(vec4 original_color) {
    vec2 tex = ft_Position;
    vec2 upLeftUV = vec2(tex.x - 1.0 / texture_Width, tex.y - 1.0 / texture_Height);
    vec4 curColor = original_color;
    vec4 upLeftColor = texture2D(v_Texture, upLeftUV);
    vec4 delColor = curColor - upLeftColor;
    float h = 0.3 * delColor.x + 0.59 * delColor.y + 0.11 * delColor.z;
    vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);
    return vec4(h, h, h, 0.0) + bkColor;
}

vec4 get_mosaic_filter_color(vec4 original_color) {
    vec2 intXY = vec2(ft_Position.x * texture_Width, ft_Position.y * texture_Height);
    vec2 XYMosaic = vec2(floor(intXY.x / mosaic_Size) * mosaic_Size, floor(intXY.y / mosaic_Size) * mosaic_Size);
    vec2 UVMosaic = vec2(XYMosaic.x / texture_Width, XYMosaic.y / texture_Height);
    return texture2D(v_Texture, UVMosaic);
}

void main() {
    vec4 color = get_original_Color();
    if(filter_Type == 1) {
        color = get_gray_filter_color(color);
    } else if(filter_Type == 2) {
        color = get_relief_filter_color(color);
    } else if(filter_Type == 3) {
        color = get_mosaic_filter_color(color);
    }

    gl_FragColor = color;
}
