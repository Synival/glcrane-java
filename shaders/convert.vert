/* convert.vert
 * ------------
 * Used when converting RGB -> XYV (x, y, value). */

#ifdef GL_ES
   precision highp float;
#endif

attribute vec4 aPosition;
attribute vec2 aTexCoord0;

uniform mat4 uMatrixModelView;
uniform mat4 uMatrixProjection;

varying float vValue;
varying vec2 vTexCoord0;

void main (void) {
   gl_Position = uMatrixProjection * uMatrixModelView *
      vec4 (aPosition.xy, 0.00, 1.00);
   vValue = aPosition.w;
   vTexCoord0 = aTexCoord0;
}
