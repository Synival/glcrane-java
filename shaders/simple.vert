/* simple.vert
 * -----------
 * Straight-forward and dead-simple shader. */

#ifdef GL_ES
   precision highp float;
#endif

attribute vec3 aPosition;
uniform mat4 uMatrixModelView;
uniform mat4 uMatrixProjection;
uniform vec4 uColor;

varying vec4 vColor;

void main (void) {
   gl_Position = uMatrixProjection * uMatrixModelView * vec4 (aPosition, 1.00);
   vColor      = uColor;
}
