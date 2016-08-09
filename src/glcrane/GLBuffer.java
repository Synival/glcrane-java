package glcrane;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import glcrane.Utils;

public class GLBuffer {
   final int type, size, id;
   private float[] data;
   private int storage;

   public GLBuffer (GL3 gl, float[] data, int type, int size, int storage) {
      // Store data.
      this.type = type;
      this.size = size;

      // Get a buffer id.
      int[] ids = new int[1];
      gl.glGenBuffers (1, ids, 0);
      this.id = ids[0];

      // Initialize data.
      setData (gl, data, storage);
   }

   public void setData (GL3 gl, float[] data, int storage) {
      gl.glBindBuffer (GL3.GL_ARRAY_BUFFER, id);
      if (data != null) {
         FloatBuffer f = Buffers.newDirectFloatBuffer (data); 
         gl.glBufferData (GL3.GL_ARRAY_BUFFER, f.capacity () *
            Buffers.SIZEOF_FLOAT, f, storage);
      }
      this.data    = data;
      this.storage = storage;
      Utils.glCheckErrorConsole (gl);
   }
   public float[] getData () {
      return this.data;
   }
   public int getStorage () {
      return this.storage;
   }
}