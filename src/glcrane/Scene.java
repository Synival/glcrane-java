package glcrane;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.Animator;

import glcrane.GLProgram;
import glcrane.Model;

public class Scene implements GLEventListener {
   // Constants. 
   static public final float[] colorWhite = new float[]{1, 1, 1, 1};

   // Lots of stuff we initialize.
   public GLProgram
      programNormal  = null,
      programSimple  = null,
      programConvert = null;
   public Model
      modelCrane  = null,
      modelPaint  = null,
      modelViewer = null;
   public Matrix4
      matrixPerspective  = null,
      matrixModelView    = null,
      matrixNormal       = null,
      matrixOrthographic = null,
      matrixViewer       = null,
      matrixIdentity     = null;
   public GLTexture
      textureFire   = null,
      texturePaint  = null,
      texturePaper  = null,
      textureShadow = null,
      textureUVMap  = null;
   public float
      rotation = 0.00f;

   // Window settings.
   public int x, y, width, height;
   public float aspect;
   private boolean initialized = false;

   // Animation and rendering.
   private Animator
      animator = null;
   public float totalTime = 0.00f;

   public void reshape (GLAutoDrawable drawable, int x, int y,
                        int width, int height) {
      this.x      = x;
      this.y      = y;
      this.width  = width;
      this.height = height;
      aspect      = (float) width / (float) height; 
      GL3 gl = drawable.getGL().getGL3();
      initMatrices (gl);
   }

   public void init (GLAutoDrawable drawable) {
      // Only run once.
      if (initialized)
         return;
      initialized = true;

      // Animate!
      try {
         this.animator = new Animator (drawable);
         animator.setUpdateFPSFrames (3, null);
         animator.start ();
      }
      catch (Exception e) {
         Utils.error (e, "OpenGL Error", "Couldn't set animator");
      }

      // Set up our OpenGL state.
      GL3 gl = drawable.getGL().getGL3();
      initState (gl);
      initTextures (gl);
      initShaders (gl);
      initModels (gl);
   }

   private void initState (GL3 gl) {
      gl.glDepthFunc (GL3.GL_LESS);
      gl.glEnable (GL3.GL_CULL_FACE);
      gl.glCullFace (GL3.GL_BACK);
      gl.glEnable (GL3.GL_BLEND);
      gl.glBlendFunc (GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
   }

   private void initTextures (GL3 gl) {
      textureFire   = new GLTexture (gl, "fire",   "images/fire.jpg");
      texturePaint  = new GLTexture (gl, "paint",  "images/paint.png");
      texturePaper  = new GLTexture (gl, "paper",  "images/paper.jpg");
      textureShadow = new GLTexture (gl, "shadow", "images/shadow.png");
      textureUVMap  = new GLTexture (gl, "uv_map", "images/uv_map.jpg");
   }

   private void initShaders (GL3 gl) {
      programNormal  = new GLProgram (gl, "normal");
      programSimple  = new GLProgram (gl, "simple");
      programConvert = new GLProgram (gl, "convert");
   }

   private void initModels (GL3 gl) {
      modelCrane =  new Model (gl, "crane");
      modelPaint  = new Model (gl, "paint");
      modelViewer = new Model (gl, "viewer");
   }

   private void initMatrices (GL3 gl) {
      // Modify our perspective matrix every time we resize.
      if (matrixPerspective == null)
         matrixPerspective = new Matrix4 ();
      else
         matrixPerspective.loadIdentity ();
      matrixPerspective.makePerspective ((float) Math.toRadians (45.00f),
         aspect, 1.50f, 100.00f);

      // Only initialize our model view matrix once.
      if (matrixModelView == null) {
         matrixModelView = new Matrix4 ();
         matrixModelView.loadIdentity ();
         matrixModelView.translate (0.00f, -0.25f, -4.50f);
         matrixModelView.rotate ((float) Math.toRadians (30.00f),
            1.00f, 0.00f, 0.00f);
      }

      // Normal matrix is modified later.
      if (matrixNormal == null)
         matrixNormal = new Matrix4 ();

      // The orthographic matrix is dependent on the screen size.
      if (matrixOrthographic == null)
         matrixOrthographic = new Matrix4 ();
      else
         matrixOrthographic.loadIdentity ();
      matrixOrthographic.makeOrtho (0, width, height, 0, -100, 100);

      // The identity matrix stays like this.
      if (matrixIdentity == null)
         matrixIdentity = new Matrix4 ();

      // Our viewer has its own matrix.
      if (matrixViewer == null)
         matrixViewer = new Matrix4 ();
      else
         matrixViewer.loadIdentity ();
      matrixViewer.translate (width - height / 4f, height * 0.75f, 0.00f);
      matrixViewer.scale (height / 4f, height / 4f, 1.00f);
   }

   public void dispose (GLAutoDrawable drawable) {
   }

   public void display (GLAutoDrawable drawable) {
      // Make sure we caught any previous errors.
      GL3 gl = drawable.getGL().getGL3();
      Utils.glCheckErrorConsole (gl);

      // Grey screen.
      gl.glClearColor (0.20f, 0.20f, 0.20f, 1.00f);
      gl.glClear (GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

      // Draw our crane and our texture viewer.
      renderCrane (gl);
      renderViewer (gl);
      GLProgram.use (gl, null);

      // Slowly rotate around.
      if (animator != null) {
         float t = 1.00f / animator.getLastFPS ();
         if (!Float.isNaN (t) && !Float.isInfinite (t))
            nextFrame (t);
      }
   }

   private void renderCrane (GL3 gl) {
      // Render using our 'normal' program.
      GLProgram.use (gl, programNormal);

      // Set up our matrices and other uniforms.
      setNormalMatrix (matrixModelView, matrixNormal);
      GLProgram.setUniforms (gl,
         matrixPerspective, matrixModelView, matrixNormal, 1.00f, colorWhite);

      // Render with depth.
      gl.glEnable (GL3.GL_DEPTH_TEST);
      modelCrane.setTexture ("shadow", textureShadow);
      modelCrane.setTexture ("body",   textureFire);
      modelCrane.render (gl);
      gl.glDisable (GL3.GL_DEPTH_TEST);

      // Check for errors.
      Utils.glCheckErrorConsole (gl);
   }

   private void renderViewer (GL3 gl) {
      GLProgram.use (gl, programNormal);
      GLProgram.setUniforms (gl,
         matrixOrthographic, matrixViewer, matrixIdentity, 0.00f, colorWhite);
      modelViewer.setTexture ("body", textureFire);
      modelViewer.render (gl);
      Utils.glCheckErrorConsole (gl);
   }

   private void nextFrame (float t) {
      totalTime += t;
      matrixModelView.rotate ((float) Math.toRadians (15.00f * t),
         0.00f, 1.00f, 0.00f);
   }

   static public void setNormalMatrix (Matrix4 in, Matrix4 out) {
      out.loadIdentity ();
      out.multMatrix (in);
      out.invert ();
      out.transpose ();
   }
}