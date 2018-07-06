package edu.cg.models;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import edu.cg.CyclicList;
import edu.cg.TrackPoints;
import edu.cg.algebra.CubicSpline;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.algebra.CubicSpline.PolyVec;

public class Track implements IRenderable {
	private IRenderable vehicle;
	private CyclicList<Point> trackPoints;
	private Texture texGrass = null;
	private Texture texTrack = null;
	private CubicSpline curve;
	private double t = 0;
	private double vel = 0.025;
	private int currSeg = 0;

	public Track(IRenderable vehicle, CyclicList<Point> trackPoints) {
		this.vehicle = vehicle;
		this.trackPoints = trackPoints;
	}

	public Track(IRenderable vehicle) {
		this(vehicle, TrackPoints.track1());
	}

	public Track() {
		// TODO: uncomment this and change it if for your needs.
		this(new Locomotive());
	}

	@Override
	public void init(GL2 gl) {
		// Build your track splines here.
		// Compute the length of each spline.
		// Do not repeat those calculations over and over in the render method.
		// It will make the application to run not smooth.
		curve = new CubicSpline(trackPoints);
		loadTextures(gl);
		vehicle.init(gl);
	}

	private void loadTextures(GL2 gl) {
		File fileGrass = new File("grass.jpg");
		File fileRoad = new File("track.png");
		try {
			texTrack = TextureIO.newTexture(fileRoad, true);
			texGrass = TextureIO.newTexture(fileGrass, false);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(GL2 gl) {
		renderField(gl);
		renderTrack(gl);
		renderVehicle(gl);
	}

	private void renderVehicle(GL2 gl) {
		gl.glPushMatrix();

		PolyVec seg = curve.polys.get(currSeg);

		Point pos = seg.position(t);
		Vec up = seg.normal(t);
		Vec forward = seg.tangent(t);
		Vec right = up.cross(forward);
		gl.glTranslatef(pos.x, pos.y, pos.z); // move to curve(t)
//
//		// rotate from "standard" base to "curve(t)" base
		double[] mat = {
		right.x, right.y, right.z, 0,
		up.x, up.y, up.z, 0,
		forward.x, forward.y,forward.z, 0,
		0, 0, 0, 1 };
		gl.glMultMatrixd(mat, 0);

		gl.glScaled(.1, .1, .1);
		gl.glTranslated(0, .35, 0);
		gl.glRotated(90, 0, 1, 0);

		vehicle.render(gl);
		gl.glPopMatrix();

		double dt = vel / seg.length();
		t += dt;
		if (t > 1) {
			t = 0;
			currSeg++;
		} else if (t < 0) {
			t = 1;
			currSeg--;
		}
	}

	private void renderField(GL2 gl) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texGrass.getTextureObject());

		boolean lightningEnabled;
		if ((lightningEnabled = gl.glIsEnabled(GL2.GL_LIGHTING)))
			gl.glDisable(GL2.GL_LIGHTING);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LOD, 1);

		gl.glBegin(GL2.GL_QUADS);

		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-1.2, -1.2, -.02);
		gl.glTexCoord2d(4, 0);
		gl.glVertex3d(1.2, -1.2, -.02);
		gl.glTexCoord2d(4, 4);
		gl.glVertex3d(1.2, 1.2, -.02);
		gl.glTexCoord2d(0, 4);
		gl.glVertex3d(-1.2, 1.2, -.02);

		gl.glEnd();

		if (lightningEnabled)
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	private void renderTrack(GL2 gl) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texTrack.getTextureObject());

//		boolean lightningEnabled;
//		if ((lightningEnabled = gl.glIsEnabled(GL2.GL_LIGHTING)))
			gl.glDisable(GL2.GL_LIGHTING);

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LOD, 2);

		gl.glBegin(GL2.GL_TRIANGLES);

		CyclicList<PolyVec> segments = this.curve.polys;
		for (PolyVec segment : segments) {
			double segmentLength = segment.length();
			double numOfBoards = (int) (segmentLength / 0.1) + 1;
			double dt = 1 / numOfBoards;
			double t = 0;
			for (int i = 0; i < numOfBoards; i++) {
				this.renderTrackBoard(gl, segment, t, dt);
				t += dt;
			}

		}
		gl.glEnd();

//		if (lightningEnabled)
//			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void renderTrackBoard(GL2 gl, PolyVec segment, double t, double dt) {
		// get normals for the segment start and end
		Vec n0 = segment.tangent(t).cross(segment.normal(t));
		Vec n1 = segment.tangent(t + dt).cross(segment.normal(t + dt));
		// get the positions of the start and end of the segment
		Point ps = segment.position(t); // point start
		Point pe = segment.position(t + dt); // point end
		/** 
		 * p0 - ps - p1 (t)
		 * |  \		  |
		 * |    \ 	  |
		 * |	  \   |
		 * |        \ |
		 * p3 - pe - p2 (t+dt)
		 * */
		double offset = 0.03f;
		Point p0 = ps.add(n0.mult(offset));
		Point p1 = pe.add(n1.mult(offset));
		Point p2 = pe.add(n1.mult(-offset));
		Point p3 = ps.add(n0.mult(-offset));
		
		// creating triangle p0-p1-p2 (both sides)
        gl.glTexCoord2d(0, 0);
        this.createPointVertex(gl, p0);
        gl.glTexCoord2d(0, 1);
        this.createPointVertex(gl, p1);
        gl.glTexCoord2d(1, 1);
        this.createPointVertex(gl, p2);
        gl.glTexCoord2d(1, 1);
        this.createPointVertex(gl, p2);
        gl.glTexCoord2d(0, 1);
        this.createPointVertex(gl, p1);
        gl.glTexCoord2d(0, 0);
        this.createPointVertex(gl, p0);
        
     // creating triangle p0-p3-p2 (both sides)
        gl.glTexCoord2d(0, 0);
        this.createPointVertex(gl, p0);
        gl.glTexCoord2d(1, 0);
        this.createPointVertex(gl, p3);
        gl.glTexCoord2d(1, 1);
        this.createPointVertex(gl, p2);
        gl.glTexCoord2d(1, 1);
        this.createPointVertex(gl, p2);
        gl.glTexCoord2d(1, 0);
        this.createPointVertex(gl, p3);
        gl.glTexCoord2d(0, 0);
        this.createPointVertex(gl, p0);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void control(int type, Object params) {
		switch (type) {
		case KeyEvent.VK_UP:
			// increase the locomotive velocity
			vel += 0.005;

			break;

		case KeyEvent.VK_DOWN:
			// decrease the locomotive velocity
			vel -= 0.005;
			break;

		case KeyEvent.VK_ENTER:
			try {
				Method m = TrackPoints.class.getMethod("track" + params);
				trackPoints = (CyclicList<Point>) m.invoke(null);
				curve = new CubicSpline(trackPoints);				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case IRenderable.TOGGLE_LIGHT_SPHERES:
			vehicle.control(type, params);
			break;

		default:
			System.out.println("Unsupported operation for Track control");
		}
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

	@Override
	public void setCamera(GL2 gl) {

		PolyVec seg = curve.polys.get(currSeg);

		Point center = seg.position(t);
		Vec up = seg.normal(t);
		Vec forward = seg.tangent(t);

		Point eye = center.add(forward.mult(-0.25)).add(up.mult(0.25)); // above and behind

		GLU glu = new GLU();
		glu.gluLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);

	}

	@Override
	public void destroy(GL2 gl) {
		texGrass.destroy(gl);
		texTrack.destroy(gl);
		vehicle.destroy(gl);
	}
	
	public void createPointVertex(GL2 gl, Point p) {
		gl.glVertex3d(p.x, p.y, p.z);
	}
}
