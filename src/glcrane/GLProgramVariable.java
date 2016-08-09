package glcrane;

import java.util.Arrays;
import com.jogamp.opengl.GL3;

import glcrane.GLProgram;
import glcrane.Utils;

abstract public class GLProgramVariable {
   static final int MAX_NAME_LEN = 64;

   public final GLProgram program;
   public final int       id, size, type;
   public final String    name;

   protected abstract void getFunc (GL3 gl,
      int index, int[] values, byte[] bytes);
   protected abstract int getID (GL3 gl, String name);
   public abstract String variableType ();

   private boolean initialized = false;
   public boolean isInitialized () {
      return this.initialized;
   }
   public void setInitialized () {
      this.initialized = true;
   }

   public String fullName() {
      return variableType () + "." + program.name + "." + name;
   }

   public GLProgramVariable (GL3 gl, GLProgram program, int index) {
      // Remember constructor arguments.
      this.program = program;

      // Values to store in final variables.
      int    size = -1;
      int    type = -1;
      int    id   = -1;
      String name = null;

      GLProgram.use (gl, program);
      try {
         int[] values = new int[3];
         byte[] bytes = new byte[MAX_NAME_LEN];
         getFunc (gl, index, values, bytes);
         Utils.glCheckError (gl);
         size = values[1];
         type = values[2];
         name = new String (Arrays.copyOfRange (bytes, 0, values[0]));
         id = getID (gl, name);
      }
      catch (Exception e) {
         Utils.error (e, "Program Attribute Error",
            "Couldn't initialize attribute " + index + " for program '" +
               program.name + "'.");
      }

      // Store final variables.
      this.size = size;
      this.type = type;
      this.name = name;
      this.id   = id;

      // TODO: remove me!
      System.out.println ("   " + variableType() + "[" + id + "]: " +
         this.name + " (" + this.size + ", " + Utils.glName (this.type) + ")");
   }
}