attribute vec4 v_Position;
uniform mat4 v_Matrix;
attribute vec4 vt_Position;

varying vec2 ft_Position;
void main() {
    gl_Position =  v_Position;
    ft_Position = (v_Matrix * vt_Position).xy;
}
