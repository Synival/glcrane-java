package glcrane;

import com.jogamp.opengl.GL3;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;

import javax.media.j3d.Shape3D;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import glcrane.Utils;

public class Model {
   final public String name, filename;
   private Scene object = null;
   private ModelGroup[] groups = null;
   private Hashtable<String, GLTexture> textures =
      new Hashtable<String, GLTexture> ();

   public Model (GL3 gl, String name) {
      this (gl, name, "models/" + name + ".obj");
   }

   public Model (GL3 gl, String name, String filename) {
      // Store initial parameters.
      this.name = name;
      this.filename = filename;

      // Gracefully bail if anything fails.
      try {
         // Attempt to load our file.
         ObjectFile loader = new ObjectFile (ObjectFile.TRIANGULATE);
         object = loader.load (filename);
         System.out.println ("Loaded '" + filename + "' as '" + name + "':");

         // Get geometry groups.
         @SuppressWarnings("unchecked") Hashtable<String, Shape3D>
            objects = object.getNamedObjects();
         groups = new ModelGroup[objects.size()];

         // Iterate through all of our groups.
         Set <Entry <String, Shape3D>> entries = objects.entrySet();
         int i = 0;
         for (Entry <String, Shape3D> entry : entries)
            groups[i++] = new ModelGroup (gl, this,
               entry.getKey(), entry.getValue());
      }
      catch (Exception e) {
         Utils.error (e, "Model Error", "Couldnt load '" + name + "'.");
      }
   }

   public void render (GL3 gl) {
      // Make sure all uniforms and attributes are set.
      GLProgram p = GLProgram.getCurrent ();
      p.checkInitialized (p.uniforms.values().toArray (
         new GLProgramVariable[0]));

      // Draw all geometry groups.
      for (int i = 0; i < groups.length; i++)
         groups[i].render (gl);
   }

   public void setTexture (String name, GLTexture texture) {
      textures.put (name, texture);
   }
   public GLTexture getTexture (String name) {
      return textures.get (name);
   }
}