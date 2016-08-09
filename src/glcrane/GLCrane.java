package glcrane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.Color;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import glcrane.Scene;
import glcrane.Utils;

public class GLCrane {
   private GLCanvas canvas = null;
   private JFrame   jframe = null;

   public GLCrane () {
      // Before doing anything else, try to initialize OpenGL.
      try {
         // Make sure OpenGL 3.0 is usable.
         GLProfile profile = GLProfile.getDefault ();
         if (!profile.isGL3 ())
            throw new Exception ("OpenGL 3.0 unavailable.");

         GLCapabilities capabilities = new GLCapabilities (profile);
         canvas = new GLCanvas (capabilities);
         canvas.addGLEventListener (new Scene ());
      }
      catch (Exception e) {
         Utils.error (e, "OpenGL Error", "Failed to initialize OpenGL.");
      }

      // Add a JFrame that will close properly. 
      jframe = new JFrame ("glcrane"); 
      jframe.setSize (640, 480);
      jframe.setLocationRelativeTo (null);
      jframe.addWindowListener( new WindowAdapter() {
         public void windowClosing (WindowEvent windowevent) {
            jframe.dispose();
            System.exit (0);
         }
      });

      // We're going to add some things.
      Container contents = jframe.getContentPane();
      contents.setBackground (new Color (0.20f, 0.20f, 0.20f, 1.00f));

      // We need to give some credit here.
      JLabel label = new JLabel (
         "<html>3D Origami crane by JuanG3D is licensed under CC Attribution "+
         "(<u>https://skfb.ly/yxSK</u>)</html>",
         SwingConstants.CENTER);
      Dimension size = label.getPreferredSize();
      label.setBounds (0, 0, size.width + 8, size.height + 8);
      label.setForeground (new Color (1.00f, 1.00f, 1.00f, 1.00f));
      contents.setLayout (null);
      contents.add (label);

      // Add our canvas to the JFrame.
      contents.setLayout (new BorderLayout ());
      contents.add (canvas, BorderLayout.CENTER);

      // JFrame is complete - show it.
      jframe.setVisible (true);
   }
}