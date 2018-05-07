package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Mat3x3;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Plain extends Shape {
	//implicit form of a plain: ax + by + cz + d = 0;
	private double a, b, c, d;

	private transient Vec normal = null;
	private transient Vec v1 = null;
	private transient Vec v2 = null;
	private transient Mat3x3 At = null;
	private transient Point zero = null;

	public Plain(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public Plain(Vec normal, Point p0) {
		this(normal.x, normal.y, normal.z, -normal.dot(p0.toVec()));
	}

	public Plain() {
		this(new Vec(0, 1, 0), new Point(0, -1, 0));
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Plain: a = " + a + ", b = " + b + ", c = " + c + ", d = " + d + endl;
	}

	public synchronized Vec normal() {
		if(normal == null)
			normal = new Vec(a, b, c).normalize();
		return normal;
	}

	private synchronized Vec v1() {
		if(v1 == null) {
			Vec xAxis = new Vec(1, 0, 0);
			v1 = normal().cross(xAxis).normalize();
			if(!v1.isFinite()) {
				Vec yAxis = new Vec(0, 1, 0);
				v1 = normal().cross(yAxis).normalize();
				if(!v1.isFinite()) {
					Vec zAxis = new Vec(0, 0, 1);
					v1 = normal().cross(zAxis).normalize();
				}
			}
		}
		return v1;
	}

	private synchronized Vec v2() {
		if(v2 == null)
			v2 = normal().cross(v1()).normalize();
		return v2;
	}

	private synchronized Mat3x3 At() {
		if(At == null)
			At = new Mat3x3(v1(), v2(), normal()).transpose();
		return At;
	}

	private synchronized Point zero() {
		if(zero == null) {
			Ray ray = new Ray(new Point(), normal().neg());
			Hit hit = intersect(ray);
			if(hit == null) {
				ray = ray.inverse();
				hit = intersect(ray);
			}
			zero = ray.getHittingPoint(hit);
		}
		return zero;
	}

	@Override
	public Vec getDiffuseCoefficient(Material material, Point p) {
		if(!material.isCheckerBoard)
			return super.getDiffuseCoefficient(material, p);

		Vec b = p.sub(zero());
		Vec x = At().mult(b);
		int alpha = (int)(x.x * 1.5);
		int beta = (int)(x.y * 1.5);

		if(x.x < 0)
			--alpha;

		if(x.y < 0)
			--beta;

		int alphaPlusBeta = alpha + beta;

		return alphaPlusBeta % 2 == 0 ? material.Kd1 : material.Kd2;
	}

	@Override
	public Hit intersect(Ray ray) {
		//TODO: implement this method.
		throw new UnimplementedMethodException("intersect(Ray)");
	}
}
