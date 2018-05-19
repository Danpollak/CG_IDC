package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Sphere extends Shape {
	private Point center;
	private double radius;
	
	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + 
				"Center: " + center + endl +
				"Radius: " + radius + endl;
	}
	
	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}
	
	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}
	
	public Point getCenter(){
		return this.center;
	}

	@Override
	public Hit intersect(Ray ray) {

		double b,c;
		Vec normal;
		double t1, t2, minT;
		boolean isWithin = false; // used to indicate if its an inner intersection

		// compute the b and c factors for the quad equation
		b = 2*(ray.direction().dot(ray.source().sub(this.center)));
		c = ray.source().sub(this.center).normSqr() - Math.pow(this.radius, 2);

		t1 = (-b+(Math.sqrt((b*b)-4*c)))/2; // "far" point
		t2 = (-b-(Math.sqrt((b*b)-4*c)))/2; // "close" point

		// if there's no solution, return null
		if((Double.isNaN(t1)) || (Double.isNaN(t2))){
			return null;
		}
		// if the far point is negative, return null (as the close one will also be negative)
		if((t1 <= Ops.epislon)){
			return null;
		}

		if(t2 <= Ops.epislon){
			// if the "close" point is negative, the "far" point is the hit point, and an inner hit
			double minT = t1;
			Point intersection = ray.add(minT);
			// negate the normal as it is an inner hit
			normal = intersection.sub(this.center).normalize().neg();
			isWithin = true;
		} else {
			double minT = t2;
			Point intersection = ray.add(minT);
			normal = intersection.sub(this.center).normalize();
		}
		// make sure that the intersection is not in inifinity
		if(minT == Ops.infinity) {
			return new Hit(minT, new Vec());
		}
		// if the solution is infinity, return null
		if(minT >= Ops.infinity){
			return null;
		}

		// create the hit
		Hit hit = new Hit(minT, normal).setIsWithin(isWithin);
		return hit;
	}
}
