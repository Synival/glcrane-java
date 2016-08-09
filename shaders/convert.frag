/* convert.frag
 * ------------
 * Used when converting RGB -> XYV (x, y, value). */

#ifdef GL_ES
   precision highp float;
#endif

varying float vValue;
uniform sampler2D uTex0;
uniform vec4 uColor;
varying vec2 vTexCoord0;

void main (void) {
   gl_FragColor = vec4 (1.00, 1.00, 1.00, vValue) *
      texture2D (uTex0, vTexCoord0) * uColor;
}
