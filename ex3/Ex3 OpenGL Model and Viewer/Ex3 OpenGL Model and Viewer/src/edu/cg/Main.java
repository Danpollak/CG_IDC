package edu.cg;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import edu.cg.models.Empty;
import edu.cg.models.IRenderable;
import edu.cg.models.Locomotive;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class Main {
	static IRenderable[] models = new IRenderable[]{new Locomotive()};
	static Point prevMouse;
	static int currentModel;
	static Frame frame;

	public static void main(String[] args) {
		frame = new JFrame();
		GLProfile.initSingleton();
		GLProfile glp = GLProfile.get("GL2");
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setSampleBuffers(true);
		caps.setNumSamples(9);
		final GLJPanel canvas = new GLJPanel(caps);
		final Viewer viewer = new Viewer(canvas);
		viewer.setModel(Main.nextModel());
		frame.setSize(500, 500);
		frame.setLayout(new BorderLayout());
		frame.add((Component)canvas, "Center");
		canvas.addGLEventListener(viewer);
		frame.addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		canvas.addKeyListener(new KeyAdapter(){

			@Override
			public void keyTyped(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'P':
					case 'p': {
						viewer.toggleRenderMode();
						break;
					}
					case 'A':
					case 'a': {
						viewer.toggleAxes();
						break;
					}
					case 'L':
					case 'l': {
						viewer.toggleLightSpheres();
						break;
					}
					case 'M':
					case 'm': {
						viewer.setModel(Main.nextModel());
						break;
					}
					case '\u001b': {
						System.exit(0);
						break;
					}
				}
				canvas.repaint();
				super.keyTyped(e);
			}
		});
		canvas.addMouseMotionListener(new MouseMotionAdapter(){

	
			public void mouseDragged(MouseEvent e) {
				viewer.storeTrackball(Main.prevMouse, e.getPoint());
				Main.prevMouse = e.getPoint();
			}
		});
		canvas.addMouseListener(new MouseAdapter(){


			public void mousePressed(MouseEvent e) {
				Main.prevMouse = e.getPoint();
				viewer.startAnimation();
				super.mousePressed(e);
			}

			
			public void mouseReleased(MouseEvent e) {
				viewer.stopAnimation();
				super.mouseReleased(e);
			}
		});
		canvas.addMouseWheelListener(new MouseWheelListener(){


			public void mouseWheelMoved(MouseWheelEvent e) {
				double rot = e.getWheelRotation();
				viewer.zoom(rot);
				canvas.repaint();
			}
		});
		canvas.setFocusable(true);
		canvas.requestFocus();
		frame.setVisible(true);
		canvas.repaint();
	}

	private static IRenderable nextModel() {
		IRenderable model = models[currentModel++];
		frame.setTitle("Exercise 5 - " + model.toString());
		currentModel %= models.length;
		return model;
	}

}