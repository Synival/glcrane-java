/* simple.frag
 * -----------
 * Straight-forward and dead-simple shader. */

#ifdef GL_ES
   precision highp float;
#endif

varying vec4 vColor;

void main (void) {
   gl_FragColor = vColor;
}
