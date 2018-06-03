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
	private double zoom;
	private Point mouseFrom;
	private Point mouseTo;
	private int canvasWidth;
	private int canvasHeight;
	private boolean isWireframe = false;
	private boolean isAxes = true;
	private IRenderable model;
	private FPSAnimator ani;
	private double[] rotationMatrix = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
	private Component glPanel;
	private boolean isModelCamera = false;
	private boolean isModelInitialized = false;

	public Viewer(Component glPanel) {
		this.glPanel = glPanel;
		this.zoom = 0.0;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!this.isModelInitialized) {
			this.initModel(gl);
			this.isModelInitialized = true;
		}
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(16640);
		gl.glMatrixMode(5888);
		this.setupCamera(gl);
		if (this.isAxes) {
			this.renderAxes(gl);
		}
		if (this.isWireframe) {
			gl.glPolygonMode(1032, 6913);
		} else {
			gl.glPolygonMode(1032, 6914);
		}
		this.model.render(gl);
		gl.glPolygonMode(1032, 6914);
	}

	private Vec mousePointToVec(Point pt) {
		double x = (double)(2 * pt.x) / (double)this.canvasWidth - 1.0;
		double y = 1.0 - (double)(2 * pt.y) / (double)this.canvasHeight;
		double z2 = 2.0 - x * x - y * y;
		if (z2 < 0.0) {
			z2 = 0.0;
		}
		double z = Math.sqrt(z2);
		return new Vec(x, y, z).normalize();
	}

	private void setupCamera(GL2 gl) {
		if (!this.isModelCamera) {
			Vec to;
			Vec from;
			Vec axis;
			gl.glLoadIdentity();
			if (this.mouseFrom != null && this.mouseTo != null && (axis = (from = this.mousePointToVec(this.mouseFrom)).cross(to = this.mousePointToVec(this.mouseTo)).normalize()).isFinite()) {
				double angle = 57.29577951308232 * Math.acos(from.dot(to));
				angle = Double.isFinite(angle) ? angle : 0.0;
				gl.glRotated(angle, axis.x, axis.y, axis.z);
			}
			gl.glMultMatrixd(this.rotationMatrix, 0);
			gl.glGetDoublev(2982, this.rotationMatrix, 0);
			gl.glLoadIdentity();
			gl.glTranslated(0.0, 0.0, -1.2);
			gl.glTranslated(0.0, 0.0, - this.zoom);
			gl.glMultMatrixd(this.rotationMatrix, 0);
			this.mouseFrom = null;
			this.mouseTo = null;
		} else {
			gl.glLoadIdentity();
			this.model.setCamera(gl);
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		this.ani = new FPSAnimator(30, true);
		this.ani.add(drawable);
		this.glPanel.repaint();
		this.initModel(gl);
	}

	public void initModel(GL2 gl) {
		gl.glCullFace(1029);
		gl.glEnable(2884);
		gl.glEnable(2977);
		gl.glEnable(2929);
		gl.glLightModelf(2898, 1.0f);
		gl.glEnable(2896);
		this.model.init(gl);
		if (this.model.isAnimated()) {
			this.startAnimation();
		} else {
			this.stopAnimation();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		this.canvasWidth = width;
		this.canvasHeight = height;
		gl.glMatrixMode(5889);
		gl.glLoadIdentity();
		gl.glFrustum(-0.1, 0.1, -0.1 * (double)height / (double)width, 0.1 * (double)height / (double)width, 0.1, 1000.0);
	}

	public void trackball(Point from, Point to) {
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
		if (!this.ani.isAnimating()) {
			this.ani.start();
		}
	}

	public void stopAnimation() {
		if (this.ani.isAnimating()) {
			this.ani.stop();
		}
	}

	private void renderAxes(GL2 gl) {
		gl.glLineWidth(2.0f);
		boolean flag = gl.glIsEnabled(2896);
		gl.glDisable(2896);
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
			gl.glEnable(2896);
		}
	}

	public void setModel(IRenderable model) {
		this.model = model;
		this.isModelInitialized = false;
	}
}