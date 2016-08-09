package glcrane;

import java.io.File;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class GLTexture {
   public final String name, filename;
   public final Texture texture;
   public final int id;

   public GLTexture (GL3 gl, String name) {
      this (gl, name, "images/" + name + ".png");
   }

   public GLTexture (GL3 gl, String name, String filename) {
      // Store initial arguments.
      this.name     = name;
      this.filename = filename;

      // Attempt to load the image.
      Texture texture = null;
      int id = 0;
      try {
         texture = TextureIO.newTexture (new File (filename), false);
         texture.setTexParameteri (gl, GL3.GL_TEXTURE_MIN_FILTER,
            GL3.GL_LINEAR);
         texture.setTexParameteri (gl, GL3.GL_TEXTURE_MAG_FILTER,
            GL3.GL_LINEAR);
         texture.setTexParameteri (gl, GL3.GL_TEXTURE_WRAP_S,
            GL3.GL_CLAMP_TO_EDGE);
         texture.setTexParameteri (gl, GL3.GL_TEXTURE_WRAP_T,
            GL3.GL_CLAMP_TO_EDGE);
         id = texture.getTextureObject ();
      } catch (Exception e) {
         Utils.error (e, "Texture Error", "Couldn't process texture '" +
            this.filename + "'.");
      }
      this.texture = texture;
      this.id = id;

      // Make sure everything worked.
      Utils.glCheckErrorConsole (gl);

      // Perform some logging.
      System.out.println ("Loaded '" + filename + "' as '" +
         name + "' (" + id + ")."); 
   }
}
