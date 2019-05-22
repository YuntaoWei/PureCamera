#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES v_Texture;
varying vec2 ft_Position;

uniform int filter_Type;

vec2 tex_Size = vec2(200.0, 200.0);
vec2 mosaicSize = vec2(8.0, 8.0);

vec4 get_original_Color() {
    return texture2D(v_Texture, ft_Position);
}

vec4 get_gray_filter_color(vec4 original_color) {
    float gray = 0.3 * original_color.r + 0.59 * original_color.g + 0.11 * original_color.b;
    return vec4(gray, gray, gray, 1.0);
}

vec4 get_relief_filter_color(vec4 original_color) {
    vec2 tex = ft_Position;
    vec2 upLeftUV = vec2(tex.x - 1.0 / tex_Size.x, tex.y - 1.0 / tex_Size.y);
    vec4 curColor = original_color;
    vec4 upLeftColor = texture2D(v_Texture, upLeftUV);
    vec4 delColor = curColor - upLeftColor;
    float h = 0.3 * delColor.x + 0.59 * delColor.y + 0.11 * delColor.z;
    vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);
    return vec4(h, h, h, 0.0) + bkColor;
}

vec4 get_mosaic_filter_color(vec4 original_color) {
    vec2 intXY = vec2(ft_Position.x * tex_Size.x, ft_Position.y * tex_Size.y);
    vec2 XYMosaic = vec2(floor(intXY.x / mosaicSize.x) * mosaicSize.x, floor(intXY.y / mosaicSize.y) * mosaicSize.y);
    vec2 UVMosaic = vec2(XYMosaic.x / tex_Size.x, XYMosaic.y / tex_Size.y);
    return texture2D(v_Texture, UVMosaic);
}

vec4 get_wb_filter_color(vec4 original_color) {
    float average = (original_color.r + original_color.g + original_color.b) * 255.0 / 3.0;
    if(average > 100.0) {
        return vec4(1.0, 1.0, 1.0, 1.0);
    } else {
        return vec4(0.0, 0.0, 0.0, 1.0);
    }
}

vec4 get_positive_filter_color(vec4 original_color) {
    return vec4(1.0 - original_color.r, 1.0 - original_color.g, 1.0 - original_color.b, original_color.a);
}

vec4 comic_filter(mat3 _filter, vec2 _xy) {
    mat3 _filter_pos_delta_x = mat3(vec3(-1.0, 0.0, 1.0), vec3(0.0, 0.0, 1.0), vec3(1.0, 0.0, 1.0));
    mat3 _filter_pos_delta_y = mat3(vec3(-1.0, -1.0, -1.0), vec3(-1.0, 0.0, 0.0), vec3(-1.0, 1.0, 1.0));
    vec4 final_color = vec4(0.0, 0.0, 0.0, 0.0);
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            vec2 _xy_new = vec2(_xy.x + _filter_pos_delta_x[i][j], _xy.y + _filter_pos_delta_y[i][j]);
            vec2 _uv_new = vec2(_xy_new.x / tex_Size.x, _xy_new.y / tex_Size.y);
            final_color += texture2D(v_Texture, _uv_new) * _filter[i][j];
        }
    }
    return final_color;
}

vec4 get_comic_filter_color() {
    vec2 intXY = vec2(ft_Position.x * tex_Size.x, ft_Position.y * tex_Size.y);
    mat3 _smooth_fil = mat3(-0.5, -1.0, 0.0,
    -1.0, 0.0, 1.0,
    0.0, 1.0, 0.5);
    vec4 delColor = comic_filter(_smooth_fil, intXY);
    float deltaGray = 0.3 * delColor.x + 0.59 * delColor.y + 0.11 * delColor.z;
    if (deltaGray < 0.0) deltaGray = -1.0 * deltaGray;
    deltaGray = 1.0 - deltaGray;
    return vec4(deltaGray, deltaGray, deltaGray, 1.0);
}

void main() {
    vec4 color = get_original_Color();
    if(filter_Type == 1) {
        color = get_gray_filter_color(color);
    } else if(filter_Type == 2) {
        color = get_relief_filter_color(color);
    } else if(filter_Type == 3) {
        color = get_mosaic_filter_color(color);
    } else if(filter_Type == 4) {
        color = get_wb_filter_color(color);
    } else if(filter_Type == 5) {
        color = get_positive_filter_color(color);
    } else if (filter_Type == 6) {
        color = get_comic_filter_color();
    }

    gl_FragColor = color;
}

