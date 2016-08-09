package glcrane;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;

import com.jogamp.opengl.GL3;

public class ModelGroup {
   final public Model model;
   final public String name;
   private GLBuffer
      positionBuffer = null,
      normalBuffer   = null,
      texCoordBuffer = null;
   private int
      coordCount = -1,
      coordSize  = -1,
      polyCount  = -1,
      polySize   = -1;

   public ModelGroup (GL3 gl, Model model, String name, Shape3D shape) {
      // Store parameters.
      this.model = model;
      this.name  = name;

      // Get our geometry data.
      TriangleArray triangles = (TriangleArray) shape.getGeometry ();

      // So, Java3D does something incredibly stupid:
      //    The ObjectFile loader gathers vertex data for coordinates,
      // normals, colors and texcoords, then builds indices for faces.
      // This is exactly what we need.
      //    Unfortunately, they chose to use a GeometryArray instead of
      // an IndexedGeometryArray, destroying index data and forcing us
      // to rebuild it ourselves with more vertex data than necessary.
      //    To make problems worse, the buffers are interleaved and no
      // offsets are provided - so we have to guess.

      float[] coords = triangles.getInterleavedVertices ();
      coordCount = triangles.getVertexCount ();
      coordSize  = coords.length / coordCount;
      polySize   = 3;
      polyCount  = coordCount / polySize;

      // Do some logging.
      System.out.println ("   " + name + ": "
         + coordCount + " vertices * "
         + coordSize + " floats/vertex = "
         + coords.length + " floats");

      // TODO: The arrays present and their offsets are total guesswork
      // and assumptions.  Please feel free to tell me how to make them
      // not that. 

      float[] texCoords = new float[coordCount * 2];
      float[] normals   = new float[coordCount * 3];
      float[] positions = new float[coordCount * 3];

      int i, it, in, ip, ic;
      for (i = 0, it = 0, in = 0, ip = 0, ic = 0; i < coordCount; i++) {
         texCoords[it++] = coords[ic++];
         texCoords[it++] = coords[ic++];
         normals[in++]   = coords[ic++];
         normals[in++]   = coords[ic++];
         normals[in++]   = coords[ic++];
         positions[ip++] = coords[ic++];
         positions[ip++] = coords[ic++];
         positions[ip++] = coords[ic++];
      }

      // Create buffers for our objects.
      this.texCoordBuffer = new GLBuffer (gl, texCoords, GL3.GL_FLOAT, 2,
         GL3.GL_STATIC_DRAW);
      this.normalBuffer   = new GLBuffer (gl, normals,   GL3.GL_FLOAT, 3,
         GL3.GL_STATIC_DRAW);
      this.positionBuffer = new GLBuffer (gl, positions, GL3.GL_FLOAT, 3,
         GL3.GL_STATIC_DRAW);
   }

   public void render (GL3 gl) {
      // Set all of our attributes to array buffers.
      GLProgram p = GLProgram.getCurrent ();
      p.setAttribute (gl, "aPosition",  positionBuffer);
      p.setAttribute (gl, "aNormal",    normalBuffer);
      p.setAttribute (gl, "aTexCoord0", texCoordBuffer);

      // Make sure all attributes are set properly.
      p.checkInitialized (p.attributes.values().toArray (
         new GLProgramVariable[0]));

      // Is there a texture to use?
      GLTexture t = model.getTexture (this.name);
      if (t != null) {
         gl.glActiveTexture (GL3.GL_TEXTURE0);
         gl.glBindTexture (GL3.GL_TEXTURE_2D, t.id);
      }
 
      // Draw!
      gl.glDrawArrays (GL3.GL_TRIANGLES, 0, this.coordCount);
      Utils.glCheckErrorConsole (gl); 
   }

   int getCoordCount () { return coordCount; }
   int getCoordSize  () { return coordSize;  }
   int getPolyCount  () { return polyCount;  }
   int getPolySize   () { return polySize;   }
}