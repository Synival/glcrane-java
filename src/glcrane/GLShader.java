package glcrane;

import java.nio.file.Files;
import java.nio.file.Paths;
import com.jogamp.opengl.GL3;

import glcrane.Utils;

public class GLShader {
   public final String typeStr, filename;
   public final int type, id;

   public GLShader (GL3 gl, int type, String filename) {
      // What kind of shader is this?
      String typeStr;
      switch (type) {
         case GL3.GL_FRAGMENT_SHADER:
            typeStr = "fragment";
            break;
         case GL3.GL_VERTEX_SHADER:
            typeStr = "vertex";
            break;
         default:
            typeStr = null;
            Utils.error (null, "Shader Error",
               "Invalid shader type '" + type + "'");
      }

      // Attempt to read and compile source.
      int id = -1;
      try {
         // Read source code.
         String src = new String (
            Files.readAllBytes (Paths.get (filename)));

         // Attempt to compile.
         id = gl.glCreateShader (type);
         gl.glShaderSource (id, 1, new String[] {src}, null);
         gl.glCompileShader (id);

         // Check for failure.
         int iv[] = new int[1];
         gl.glGetShaderiv (id, GL3.GL_COMPILE_STATUS, iv, 0);
         if (iv[0] == GL3.GL_FALSE) {
            // Get the error log.
            gl.glGetShaderiv (id, GL3.GL_INFO_LOG_LENGTH, iv, 0);
            byte[] bytes = new byte[iv[0]];
            gl.glGetShaderInfoLog (id, iv[0], null, 0, bytes, 0); 
            String log = new String (bytes);

            // Throw our error.
            throw new Exception ("Compilation log from '" + filename + "':\n"
               + log);
         }
      }
      catch (Exception e) {
         Utils.error (e, "Shader Error",
            "Couldn't load " + typeStr + " shader '" + filename + "'.");
      }

      // Store final variables.
      this.typeStr  = typeStr;
      this.type     = type;
      this.filename = filename;
      this.id       = id;
   }
}