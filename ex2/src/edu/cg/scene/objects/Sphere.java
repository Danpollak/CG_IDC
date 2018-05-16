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
	
	@Override
	public Hit intersect(Ray ray) {
		double b,c;
		b = 2*(ray.direction().dot(ray.source().sub(this.center)));
		c = ray.source().sub(this.center).normSqr() - Math.pow(this.radius, 2);
		double t1, t2, minT;
		t1 = (-b+(Math.sqrt((b*b)-4*c)))/2;
		t2 = (-b-(Math.sqrt((b*b)-4*c)))/2;
		if((Double.isNaN(t1)) || (t1 <= Ops.epsilon) || (t1 == Double.POSITIVE_INFINITY) || (t1 < 0)) {
			t1 = Ops.infinity;
		}
		if((Double.isNaN(t2)) || (t2 <= Ops.epsilon) || (t2 == Double.POSITIVE_INFINITY) || (t2 < 0)) {
			t2 = Ops.infinity;
		}
		minT = Math.min(t1, t2);
		if(minT == Ops.infinity) {
			return new Hit(minT, new Vec());
		}
		Point intersection = ray.add(minT);
		Vec normal = intersection.sub(this.center).normalize();
		Hit hit = new Hit(minT, normal);
		return hit;
	}
}
