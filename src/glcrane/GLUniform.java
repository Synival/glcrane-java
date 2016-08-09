package glcrane;

import com.jogamp.opengl.GL3;
import glcrane.GLProgramVariable;

public class GLUniform extends GLProgramVariable {
   public GLUniform (GL3 gl, GLProgram program, int id) {
      super (gl, program, id);
   }
   public String variableType () {
      return "uniform";
   }
   protected void getFunc (GL3 gl, int index, int[] values, byte[] bytes) {
      gl.glGetActiveUniform (program.id, index, MAX_NAME_LEN,
         values, 0, values, 1, values, 2, bytes, 0);
   }
   protected int getID (GL3 gl, String name) {
      return gl.glGetUniformLocation (program.id, name);
   }
}