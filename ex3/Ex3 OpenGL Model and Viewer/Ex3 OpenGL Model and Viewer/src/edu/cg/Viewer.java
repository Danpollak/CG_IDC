package edu.cg;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.FPSAnimator;
import edu.cg.algebra.Vec;
import edu.cg.models.IRenderable;
import java.awt.Component;
import java.awt.Point;

public class Viewer
		implements GLEventListener {
	private double zoom; //How much to zoom in? >0 mean magnify, <0 means shrink
	private Point mouseFrom,mouseTo; //From where to where was the mouse dragged between the last redraws?
	private boolean isWireframe = false;//Should we display wireframe or not?
	private boolean isAxes = true; //Should we display axes or not?
	private IRenderable model;//Model to display
	private FPSAnimator ani; //This object is responsible to redraw the model with a constant FPS
	private Component glPanel; //We store the OpenGL panel component object to refresh the scene
	private boolean isModelCamera = false;//Whether the camera is relative to the model, rather than the world (ex6)
	private boolean isModelInitialized = false;//Whether model.init() was called.
	private int width, height;//the frustum's borders!
	private double[] rotationMatrix = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};   //always start with I4X4

	public Viewer(Component glPanel) {
		this.glPanel = glPanel;
		this.zoom = 0.0;
	}

	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!this.isModelInitialized) {
			this.initModel(gl);
			this.isModelInitialized = true;
		}
		// gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); //BackGround could be black or white!
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		this.setupCamera(gl);
		if (this.isAxes) {
			this.renderAxes(gl);
		}
		
		if (this.isWireframe) {	
			gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
		} else {
			gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
		}
		this.model.render(gl);
		gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
	}

	private Vec projectToSphere(Point p){
		System.out.println("projecting points to plane");
		double planerX = (double)(2 * p.x) / (double) width - 1.0;
		double planerY = 1.0 - (double)(2 * p.y) / (double) height;
		double z = 2 - (planerX* planerX) - (planerY* planerY);
		z = Math.max(0,z);
		z = Math.sqrt(z);
		return new Vec(planerX, planerY, z).normalize();
	}
	
	private void trackBallRotateModel(GL2 gl, Point initial, Point finalp){
		System.out.println("Rotating the model for trackBall");
		Vec initailProjection;
		Vec finalProjection;
		Vec normal;
		
		initailProjection = projectToSphere(initial);
		finalProjection = projectToSphere(finalp);
		normal = initailProjection.cross(finalProjection).normalize();
		if(normal.isFinite()){
			double angle = 57.29577951308232 * Math.acos(initailProjection.dot(finalProjection));
			angle = Double.isFinite(angle) ? angle : 0.0;
			gl.glRotated(angle, normal.x, normal.y, normal.z);
		}
	}

	private void trackBallZoom(GL2 gl){
		gl.glMultMatrixd(this.rotationMatrix, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, this.rotationMatrix, 0);
		gl.glLoadIdentity();
		gl.glTranslated(0.0, 0.0, -1.2);
		gl.glTranslated(0.0, 0.0, - zoom);
		gl.glMultMatrixd(this.rotationMatrix, 0);
	}
	
	private void setupCamera(GL2 gl) {
		// This implements the trackball!
		gl.glLoadIdentity();
		if (!this.isModelCamera) {
			if (this.mouseFrom != null && this.mouseTo != null) 
					trackBallRotateModel(gl, mouseFrom,mouseTo);
			trackBallZoom(gl);
			
			//By this point, we should have already changed the point of view, now set these to null
			//so we don't change it again on the next redraw.
			mouseFrom = null;
			mouseTo = null;
		} else {
			model.setCamera(gl);
		}
	}

	
	public void dispose(GLAutoDrawable drawable) {
		// TODO Typically there's nothing to do here
		// well clearly, removing this method will make the world collapse.
	}

	
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		// Initialize display callback timer
		//ani = new FPSAnimator(30, true);
		//ani.add(drawable);
		glPanel.repaint();
		initModel(gl);
	}

	public void initModel(GL2 gl) {
		System.out.println("set culling, lighting, depthTest etc'...");
		gl.glCullFace(GL2.GL_BACK);// Set Culling Face To Back Face
		gl.glEnable(GL2.GL_CULL_FACE);// Enable back face culling
		gl.glEnable(GL2.GL_DEPTH_TEST);// Enable Depth Test
		this.model.init(gl);
		if (this.model.isAnimated()) {
			this.startAnimation();
		} else {
			this.stopAnimation();
		}
	}

	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		// Remember the width and height of the canvas for the trackball.
		this.width = width;
		this.height = height;
		
		//Set the projection to perspective.
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(-0.1,
				0.1,
				-0.1 * (double)height / (double)width,
				0.1 * (double)height / (double)width,
				0.1,
				1000.0);
	}

	public void storeTrackball(Point from, Point to) {
		if (!this.isModelCamera) {
			if (this.mouseFrom == null) {
				this.mouseFrom = from;
			}
			
			this.mouseTo = to;
			this.glPanel.repaint();
		}
	}

	public void zoom(double s) {
		if (!this.isModelCamera) {
			this.zoom += s * 0.1;
			this.glPanel.repaint();
		}
	}

	public void toggleRenderMode() {
		this.isWireframe = !this.isWireframe;
		this.glPanel.repaint();
	}

	public void toggleLightSpheres() {
		this.model.control(0, null);
		this.glPanel.repaint();
	}

	public void toggleAxes() {
		this.isAxes = !this.isAxes;
		this.glPanel.repaint();
	}

	public void toggleModelCamera() {
		this.isModelCamera = !this.isModelCamera;
		this.glPanel.repaint();
	}

	public void startAnimation() {
		//if (!this.ani.isAnimating()) {
		//		this.ani.start();
		//	}
	}

	public void stopAnimation() {
		//	if (this.ani.isAnimating()) {
		//		this.ani.stop();
		//	}
	}

	private void renderAxes(GL2 gl) {
		gl.glLineWidth(2.0f);
		boolean flag = gl.glIsEnabled(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(1);
		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(1.0, 0.0, 0.0);
		gl.glColor3d(0.0, 1.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 1.0, 0.0);
		gl.glColor3d(0.0, 0.0, 1.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 1.0);
		gl.glEnd();
		if (flag) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	public void setModel(IRenderable model) {
		this.model = model;
		this.isModelInitialized = false;
	}
}