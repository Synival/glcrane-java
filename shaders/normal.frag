/* shader.frag
 * -----------
 * Fragment shader used for all rendering. */

#ifdef GL_ES
   precision highp float;
#endif

varying vec3 vNormal;
varying vec2 vTexCoord0;
varying float vLight;

uniform sampler2D uTex0;
uniform float uTime;
uniform vec4 uColor;

void main (void) {
   gl_FragColor = texture2D (uTex0, vTexCoord0) * uColor;
   gl_FragColor.rgb *= vLight;
}
