package glcrane;

import java.util.Collection;
import java.util.Hashtable;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;

import glcrane.GLShader;
import glcrane.Utils;

public class GLProgram {
   public final String        name;
   public final int           id;
   public final GLShader      srcVert, srcFrag;
   public final Hashtable<String, GLAttribute>       attributes;
   public final Hashtable<String, GLUniform>         uniforms;
   public final Hashtable<String, GLProgramVariable> variables;

   public GLProgram (GL3 gl, String name) {
      // Integer values retrieved from glGetXXXiv().
      int[] iv = new int[1];

      // Record our name immediately.
      this.name = name;

      // Get our vertex + fragment shaders.
      GLShader srcVert = null;
      GLShader srcFrag = null;
      try {
         srcVert = new GLShader (gl, GL3.GL_VERTEX_SHADER,
            "shaders/" + name + ".vert");
         srcFrag = new GLShader (gl, GL3.GL_FRAGMENT_SHADER,
            "shaders/" + name + ".frag");
      }
      catch (Exception e) {}
      this.srcVert = srcVert;
      this.srcFrag = srcFrag;

      // Link our program.
      int id = -1;
      try {
         // Create program and attempt to link it.
         id = gl.glCreateProgram ();
         gl.glAttachShader (id, srcVert.id);
         gl.glAttachShader (id, srcFrag.id);
         gl.glLinkProgram (id);

         // Check for bad linking.
         gl.glGetProgramiv (id, GL3.GL_LINK_STATUS, iv, 0);
         if (iv[0] == GL3.GL_FALSE) {
            // Get the error log.
            gl.glGetProgramiv (id, GL3.GL_INFO_LOG_LENGTH, iv, 0);
            byte[] bytes = new byte[iv[0]];
            gl.glGetProgramInfoLog (id, iv[0], null, 0, bytes, 0);
            String log = new String (bytes);
            throw new Exception ("Linker log:\n" + log);
         }
      }
      catch (Exception e) {
         Utils.error (e, "Program error", "Couldn't link program '" +
            name + "'.");
      }
      this.id = id;

      // Log our program.
      System.out.println ("Program '" + name + "' (" + id + "):");

      // Keep track of all variables regardless of type.
      Hashtable<String, GLProgramVariable> variables = new
         Hashtable<String, GLProgramVariable> ();

      // Get all attributes.
      Hashtable<String, GLAttribute> attributes = new
         Hashtable<String, GLAttribute> ();
      GLAttribute a;
      try {
         gl.glGetProgramiv (id, GL3.GL_ACTIVE_ATTRIBUTES, iv, 0);
         for (int i = 0; i < iv[0]; i++) {
            a = new GLAttribute (gl, this, i);
            variables.put  (a.name, a);
            attributes.put (a.name, a);
         }
      }
      catch (Exception e) {}
      this.attributes = attributes;

      // Get all uniforms.
      Hashtable<String, GLUniform> uniforms = new
         Hashtable<String, GLUniform> ();
      GLUniform u;
      try {
         gl.glGetProgramiv (id, GL3.GL_ACTIVE_UNIFORMS, iv, 0);
         for (int i = 0; i < iv[0]; i++) {
            u = new GLUniform (gl, this, i);
            variables.put (u.name, u);
            uniforms.put  (u.name, u);
         }
      }
      catch (Exception e) {}
      this.uniforms = uniforms;

      // Set remaining final variables.
      this.variables = variables;

      // Initialize our samplers.
      for (int i = 0; i < 16; i++)
         setUniform (gl, "uTex" + i, i);
   }

   public boolean setUniform (GL3 gl, String name, Matrix4 mat) {
      GLUniform u = this.uniforms.get (name);
      if (u == null)
         return false;
      if (u.type == GL3.GL_FLOAT_MAT4)
         gl.glUniformMatrix4fv (u.id, 1, false, mat.getMatrix(), 0);
      else if (u.type == GL3.GL_FLOAT_MAT3) {
         float[] data = new float[9], m = mat.getMatrix ();
         data[0] = m[0]; data[1] = m[1]; data[2] = m[2];
         data[3] = m[4]; data[4] = m[5]; data[5] = m[6];
         data[6] = m[8]; data[7] = m[9]; data[8] = m[10];
         gl.glUniformMatrix3fv (u.id, 1, false, data, 0);
      }
      else
         return false;
      if (!Utils.glCheckErrorConsole (gl))
         u.setInitialized ();
      return true;
   }

   public boolean setUniform (GL3 gl, String name, float f) {
      GLUniform u = this.uniforms.get (name);
      if (u == null)
         return false;
      if (u.type != GL3.GL_FLOAT)
         return false;
      gl.glUniform1f (u.id, f);
      if (!Utils.glCheckErrorConsole (gl))
         u.setInitialized ();
      return true; 
   }

   public boolean setUniform (GL3 gl, String name, float[] f) {
      GLUniform u = this.uniforms.get (name);
      if (u == null)
         return false;

      // Determine the number of floats required by this uniform.
      int size = 0;
      switch (u.type) {
         case GL3.GL_FLOAT:      size = 1; break;
         case GL3.GL_FLOAT_VEC2: size = 2; break;
         case GL3.GL_FLOAT_VEC3: size = 3; break;
         case GL3.GL_FLOAT_VEC4: size = 4; break;
         default:
            return false;
      }

      // Does it match?
      if (f.length / u.size != size)
         return false;

      // Assign uniforms based on size.
      switch (size) {
         case 1: gl.glUniform1fv (u.id, u.size, f, 0); break;
         case 2: gl.glUniform2fv (u.id, u.size, f, 0); break;
         case 3: gl.glUniform3fv (u.id, u.size, f, 0); break;
         case 4: gl.glUniform4fv (u.id, u.size, f, 0); break;
      }
      if (!Utils.glCheckErrorConsole (gl))
         u.setInitialized ();
      return true; 
   }

   public boolean setUniform (GL3 gl, String name, int i) {
      GLUniform u = this.uniforms.get (name);
      if (u == null)
         return false;
      if (u.type != GL3.GL_INT && u.type != GL3.GL_SAMPLER_2D)
         return false;
      gl.glUniform1i (u.id, i);
      if (!Utils.glCheckErrorConsole (gl))
         u.setInitialized ();
      return true;
   }

   public boolean setAttribute (GL3 gl, String name, GLBuffer buffer) {
      GLAttribute a = this.attributes.get (name);
      if (a == null)
         return false;
      gl.glBindBuffer (GL3.GL_ARRAY_BUFFER, buffer.id);
      gl.glVertexAttribPointer (a.id, buffer.size, GL3.GL_FLOAT, false, 0, 0);
      if (!Utils.glCheckErrorConsole (gl))
         a.setInitialized ();
      return true;
   }

   public int checkInitialized (Collection<GLProgramVariable> variables) {
      return checkInitialized (variables.toArray ( 
         new GLProgramVariable[variables.size ()]));
   }

   public int checkInitialized (GLProgramVariable[] variables) {
      int count = 0;
      for (GLProgramVariable pv : variables) {
         if (pv.isInitialized ())
            count++;
         else
            System.out.println ("Warning: " + pv.fullName () +
               " is uninitialized.");
      }
      return count;
   }

   // ------------
   // STATIC CLASS
   // ------------

   private static GLProgram current = null;

   static void use (GL3 gl, GLProgram program) {
      if (current == program)
         return;
      gl.glUseProgram ((program != null) ? program.id : 0);
      Utils.glCheckErrorConsole (gl);
      current = program;
   }

   static GLProgram getCurrent () {
      return current;
   }

   static void setUniforms (GL3 gl, Matrix4 mp, Matrix4 mmv, Matrix4 mn,
         float light, float[] color) {
      if (mp    != null) current.setUniform (gl, "uMatrixProjection", mp);
      if (mmv   != null) current.setUniform (gl, "uMatrixModelView",  mmv);
      if (mn    != null) current.setUniform (gl, "uMatrixNormal",     mn);
      if (light >= 0.0f) current.setUniform (gl, "uLightIntensity",   light);
      if (color != null) current.setUniform (gl, "uColor",            color);
   }
}