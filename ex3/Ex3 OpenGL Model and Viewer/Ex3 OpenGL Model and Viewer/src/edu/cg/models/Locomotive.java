package edu.cg.models;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.algebra.Vec;

/**
 * A simple axes dummy
 *
 */
public class Locomotive implements IRenderable {

	private boolean isLightSpheres;
	private Vec DARK_RED = new Vec(0.4,0,0);
	private Vec RED = new Vec(0.8,0,0);
	private Vec BLUE = new Vec(0.2,0.2,0.4);
	private Vec BROWN = new Vec(0.5,0.25,0.1);
	private Vec BLACK = new Vec(0,0,0);
	private Vec WHITE = new Vec(1,1,1);
	private Vec GREY = new Vec(0.6,0.6,0.6);
	private double width = 0.4;


	public void render(GL2 gl) {
		gl.glDisable(GL2.GL_LIGHTING);
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		gl.glTranslated(0,0.2,0);
		gl.glPushMatrix();
		this.renderCabin(gl,glu,quad);
		gl.glPopMatrix();
		this.renderEngine(gl,glu,quad);
	}

	@Override
	public String toString() {
		return "Locomotive";
	}


	//If your scene requires more control (like keyboard events), you can define it here.
	@Override
	public void control(int type, Object params) {
		switch (type) {
			case IRenderable.TOGGLE_LIGHT_SPHERES:
			{
				isLightSpheres = ! isLightSpheres;
				break;
			}
			default:
				System.out.println("Control type not supported: " + toString() + ", " + type);
		}
	}

	@Override
	public boolean isAnimated() {
		return false;
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public void setCamera(GL2 gl) {

	}

	public void setColor(GL2 gl, Vec color){
		gl.glColor3d(color.x,color.y,color.z);
	}

	private void renderWheel(GL2 gl, GLU glu, GLUquadric quad){
		double radius = 0.1;
		double innerRadius = 0.075;
		double depth = 0.1;
		// create wheel cylinder
		// center the wheel
		gl.glTranslated(0, 0, -depth/2);
		setColor(gl,BROWN);
		glu.gluCylinder(quad,radius,radius,depth,26,1);
		// create wheel sides
		gl.glRotated(180,1,0,0);
		// create inner circle
		glu.gluDisk(quad,0,innerRadius,26,1);
		// create outer ring
		setColor(gl, DARK_RED);
		glu.gluDisk(quad,innerRadius,radius,26,1);
		// move to the other side of the wheel
		gl.glRotated(180, 1, 0, 0);
		gl.glTranslated(0.0, 0.0, depth);
		// repeat on side
		setColor(gl, BROWN);
		glu.gluDisk(quad,0,innerRadius,26,1);
		// create outer ring
		setColor(gl, DARK_RED);
		glu.gluDisk(quad,innerRadius,radius,26,1);
	}

	private void renderWheels(GL2 gl, GLU glu, GLUquadric quad, double depth){
		// render wheels
		gl.glPushMatrix();
		gl.glTranslated(width/2,0,-depth);
		gl.glPushMatrix();
		gl.glTranslated(-0.4/2,0,0);
		gl.glRotated(-90,0,1,0);
		this.renderWheel(gl,glu,quad);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(width/2,0,0);
		gl.glRotated(90,0,1,0);
		this.renderWheel(gl,glu,quad);
		gl.glPopMatrix();
		gl.glPopMatrix();

	}

	private void renderHeadlights(GL2 gl, GLU glu, GLUquadric quad){
		double radius = 0.05;
		double depth = 0.05;
		// create headlight cylinder
		setColor(gl,BLACK);
		glu.gluCylinder(quad,radius,radius,depth,26,1);
		// create wheel sides
		gl.glTranslated(0,0,depth);
		// create headlight
		setColor(gl, WHITE);
		glu.gluDisk(quad,0,radius,26,1);
	}

	private void renderChimeny(GL2 gl, GLU glu, GLUquadric quad){
		double narrowHeight = 0.2;
		double wideHeight = 0.1;
		double narrowR = 0.065;
		double wideR =0.095;
		setColor(gl, RED);
		// create an upwards cylinder
		gl.glRotated(-90,1,0,0);
		glu.gluCylinder(quad,narrowR,narrowR,narrowHeight,26,1);
		gl.glTranslated(0.0, 0.0, narrowHeight);
		glu.gluCylinder(quad,wideR,wideR,wideHeight,26,1);
		gl.glRotated(180,1,0,0);
		glu.gluDisk(quad,0,wideR,26,1);
		gl.glTranslated(0.0, 0.0, -wideR);
		gl.glRotated(-180,1,0,0);
		glu.gluDisk(quad,0,wideR,26,1);
		gl.glTranslated(0.0, 0.0, -wideR);
	}

	private void renderEngine(GL2 gl, GLU glu, GLUquadric quad){
		double engineHeight = 0.2;
		double engineDepth = 0.5;
		gl.glTranslated(-width/2,-0.4,engineDepth);
		setColor(gl,RED);
		this.renderBox(gl,width,engineHeight,engineDepth);
		// render chimney
		gl.glPushMatrix();
		gl.glTranslated(width/2,engineHeight,-0.25);
		this.renderChimeny(gl,glu,quad);
		gl.glPopMatrix();
		this.renderWheels(gl,glu,quad,0.2);
		// render headlights
		gl.glPushMatrix();
		gl.glTranslated(width/2,engineHeight/2,0);
		gl.glPushMatrix();
		gl.glTranslated(-0.1,0,0);
		this.renderHeadlights(gl,glu,quad);
		gl.glPopMatrix();
		gl.glTranslated(0.1,0,0);
		this.renderHeadlights(gl,glu,quad);
		gl.glPopMatrix();
	}

	private void renderCabin(GL2 gl, GLU glu, GLUquadric quad){
		double height = 0.4;
		double depth = 0.8;
		gl.glTranslated(-width/2,-0.4,0);
		this.setColor(gl,RED);
		gl.glPushMatrix();
		renderBox(gl,width,height,depth);
		gl.glPopMatrix();
		// handle windows on window side
		gl.glPushMatrix();
		// avoid z conflicts
		gl.glTranslated(0.01,0,0);
		gl.glBegin(GL2.GL_QUADS);
		this.setColor(gl,BLUE);
		gl.glVertex3d(width, 0.175, -0.075);
		gl.glVertex3d(width, 0.175, -0.225);
		gl.glVertex3d(width, height-0.05, -0.225);
		gl.glVertex3d(width, height-0.05, -0.075);
		gl.glVertex3d(width, 0.175, -0.35);
		gl.glVertex3d(width, 0.175, -0.5);
		gl.glVertex3d(width, height-0.05, -0.5);
		gl.glVertex3d(width, height-0.05, -0.35);
		gl.glVertex3d(width, 0.175, -0.6);
		gl.glVertex3d(width, 0.175, -0.75);
		gl.glVertex3d(width, height-0.05, -0.75);
		gl.glVertex3d(width, height-0.05, -0.6);
		gl.glEnd();
		gl.glPopMatrix();

		// handle windows on door side
		gl.glPushMatrix();
		// avoid z conflicts
		gl.glTranslated(-0.01,0,0);
		gl.glBegin(GL2.GL_QUADS);
		this.setColor(gl,BLUE);
		// door
		gl.glVertex3d(0, height-0.05, -0.075);
		gl.glVertex3d(0, height-0.05, -0.225);
		gl.glVertex3d(0, 0.02, -0.225);
		gl.glVertex3d(0, 0.02, -0.075);
		// windows
		gl.glVertex3d(0, height-0.05, -0.35);
		gl.glVertex3d(0, height-0.05, -0.5);
		gl.glVertex3d(0, 0.175, -0.5);
		gl.glVertex3d(0, 0.175, -0.35);
		gl.glVertex3d(0, height-0.05, -0.6);
		gl.glVertex3d(0, height-0.05, -0.75);
		gl.glVertex3d(0, 0.175, -0.75);
		gl.glVertex3d(0, 0.175, -0.6);
		gl.glEnd();
		gl.glPopMatrix();

		// render back window
		gl.glPushMatrix();
		// avoid z conflicts
		gl.glTranslated(0,0,-0.01);
		gl.glBegin(GL2.GL_QUADS);
		this.setColor(gl,BLUE);
		gl.glVertex3d(0.05, height-0.05, -depth);
		gl.glVertex3d(width-0.05, height-0.05, -depth);
		gl.glVertex3d(width-0.05, 0.175, -depth);
		gl.glVertex3d(0.05, 0.175, -depth);
		gl.glEnd();
		gl.glPopMatrix();

		// render back window
		gl.glPushMatrix();
		// avoid z conflicts
		gl.glTranslated(0,0,0.01);
		gl.glBegin(GL2.GL_QUADS);
		this.setColor(gl,BLUE);
		gl.glVertex3d(width-0.05, height-0.05, 0);
		gl.glVertex3d(0.05, height-0.05, 0);
		gl.glVertex3d(0.05, 0.175, 0);
		gl.glVertex3d(width-0.05, 0.175, 0);

		gl.glEnd();
		gl.glPopMatrix();


		// render wheels
		this.renderWheels(gl,glu,quad,0.5);

		// render roof
		this.renderRoof(gl,glu,quad,width,height,depth);
	}

	private void renderBox(GL2 gl, double width, double height, double depth){
		gl.glBegin(GL2.GL_QUADS);
		// draw front
		gl.glVertex3d(0,0,0);
		gl.glVertex3d(width,0,0);
		gl.glVertex3d(width,height,0);
		gl.glVertex3d(0,height,0);
		// draw back
		gl.glVertex3d(0,height,-depth);
		gl.glVertex3d(width,height,-depth);
		gl.glVertex3d(width,0,-depth);
		gl.glVertex3d(0,0,-depth);
		// draw sides
		gl.glVertex3d(0,0,0);
		gl.glVertex3d(0,height,0);
		gl.glVertex3d(0,height,-depth);
		gl.glVertex3d(0,0,-depth);
		gl.glVertex3d(width,0,-depth);
		gl.glVertex3d(width,height,-depth);
		gl.glVertex3d(width,height,0);
		gl.glVertex3d(width,0,0);
		// draw top and bottom
		gl.glVertex3d(width,height,0);
		gl.glVertex3d(width,height,-depth);
		gl.glVertex3d(0,height,-depth);
		gl.glVertex3d(0,height,0);
		gl.glVertex3d(0,0,0);
		gl.glVertex3d(0,0,-depth);
		gl.glVertex3d(width,0,-depth);
		gl.glVertex3d(width,0,0);
		gl.glEnd();
	}

	private void renderRoof(GL2 gl, GLU glu, GLUquadric quad, double width, double height, double depth){
		gl.glPushMatrix();
		gl.glTranslated(width/2,height,-depth);
		gl.glScaled(2,0.5,1);
		this.setColor(gl,GREY);
		glu.gluCylinder(quad,0.1,0.1,depth,26,1);
		gl.glRotated(180,1,0,0);
		glu.gluDisk(quad,0,0.1,26,1);
		gl.glRotated(-180,1,0,0);
		gl.glTranslated(0,0,depth);
		glu.gluDisk(quad,0,0.1,26,1);
		gl.glTranslated(0,0,0.01);
		gl.glPopMatrix();
	}
}