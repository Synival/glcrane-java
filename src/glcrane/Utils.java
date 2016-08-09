package glcrane;

import javax.swing.JOptionPane;
import java.awt.EventQueue;
import com.jogamp.opengl.GL3;

public class Utils {
   public static void error (Exception e, String title, String message) {
      // Is there a message in the exception?
      String fullMessage = message;

      // Write the stack trace if we have one.
      if (e != null) {
         fullMessage = fullMessage + "\n\n" + e.getMessage();
         e.printStackTrace ();
      }

      // For some reason, we have to do this to prevent JOptionPane from
      // hanging.  Odd!
      EventQueue.invokeLater (new UtilErrorRunnable (title, fullMessage));
   }

   static class UtilErrorRunnable implements Runnable {
      private final String title, message;

      public UtilErrorRunnable (String title, String message) {
         this.title   = title;
         this.message = message;
      }
      @Override public void run () {
         JOptionPane.showMessageDialog (null, message, title,
            JOptionPane.ERROR_MESSAGE);
      }
   }

   public static void glCheckError (GL3 gl) throws Exception {
      int err = gl.glGetError ();
      String log = null;

      while (err != GL3.GL_NO_ERROR) {
         if (log == null)
            log = "";
         else
            log += "\n";
         log += "glGetError(): " + glName (err);
         err = gl.glGetError ();
      }
      if (log != null)
         throw new Exception (log);
   }

   public static boolean glCheckErrorConsole (GL3 gl) {
      try {
         glCheckError (gl);
         return false;
      }
      catch (Exception e) {
         e.printStackTrace ();
         System.exit (0);
         return true;
      }
   }

   public static String glName (int err) {
      switch (err) {
         case GL3.GL_NO_ERROR:          return "GL_NO_ERROR";
         case GL3.GL_INVALID_ENUM:      return "GL_INVALID_ENUM";
         case GL3.GL_INVALID_VALUE:     return "GL_INVALID_VALUE";
         case GL3.GL_INVALID_OPERATION: return "GL_INVALID_OPERATION";
         case GL3.GL_INVALID_FRAMEBUFFER_OPERATION:
            return "GL_INVALID_FRAMEBUFFER_OPERATION";
         case GL3.GL_OUT_OF_MEMORY:     return "GL_OUT_OF_MEMORY";
         case GL3.GL_STACK_UNDERFLOW:   return "GL_STACK_UNDERFLOW";
         case GL3.GL_STACK_OVERFLOW:    return "GL_STACK_OVERFLOW";
         case GL3.GL_BYTE:              return "GL_BYTE";
         case GL3.GL_UNSIGNED_BYTE:     return "GL_UNSIGNED_BYTE";
         case GL3.GL_SHORT:             return "GL_SHORT";
         case GL3.GL_UNSIGNED_SHORT:    return "GL_UNSIGNED_SHORT";
         case GL3.GL_INT:               return "GL_INT";
         case GL3.GL_UNSIGNED_INT:      return "GL_UNSIGNED_INT";
         case GL3.GL_FIXED:             return "GL_FIXED";
         case GL3.GL_HALF_FLOAT:        return "GL_HALF_FLOAT";
         case GL3.GL_FLOAT:             return "GL_FLOAT";
         case GL3.GL_DOUBLE:            return "GL_DOUBLE";
         case GL3.GL_FLOAT_VEC2:        return "GL_FLOAT_VEC2";
         case GL3.GL_FLOAT_VEC3:        return "GL_FLOAT_VEC3";
         case GL3.GL_FLOAT_VEC4:        return "GL_FLOAT_VEC4";
         case GL3.GL_FLOAT_MAT2:        return "GL_FLOAT_MAT2";
         case GL3.GL_FLOAT_MAT3:        return "GL_FLOAT_MAT3";
         case GL3.GL_FLOAT_MAT4:        return "GL_FLOAT_MAT4";
         case GL3.GL_SAMPLER_1D:        return "GL_SAMPLER_1D";
         case GL3.GL_SAMPLER_2D:        return "GL_SAMPLER_2D";
         case GL3.GL_SAMPLER_3D:        return "GL_SAMPLER_3D";
         default:
            return "(unknown: 0x" + Integer.toHexString (err) + ")";
      }
   }
}
