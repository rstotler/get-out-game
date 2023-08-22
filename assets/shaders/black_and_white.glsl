#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

uniform float target_alpha;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 grayscale = vec3(gray);

    float alpha = color.a;
    if(alpha > 0.0f) {
        alpha = target_alpha;
    }

    gl_FragColor = vec4(grayscale.rgb, alpha);
}
