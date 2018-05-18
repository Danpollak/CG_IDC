package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Triangle extends Shape {
	private Point p1, p2, p3;
	
	public Triangle() {
		p1 = p2 = p3 = null;
	}
	
	public Triangle(Point p1, Point p2, Point p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Triangle:" + endl +
				"p1: " + p1 + endl + 
				"p2: " + p2 + endl +
				"p3: " + p3 + endl;
	}

	public Plain toPlane(){

		Vec p2p1 = this.p2.toVec().add(this.p1.toVec().neg());
		Vec p3p1 = this.p3.toVec().add(this.p1.toVec().neg());
		Vec Normal = p2p1.cross(p3p1);

		double d = -(Normal.dot(p1.toVec()));
		return new Plain( Normal.x,Normal.y, Normal.z, d);
	}

	@Override
	public Hit intersect(Ray ray) {
		
		// this is done using the moller-trumbpre algorithm.
		Vec v1,v2,v3,e1,e2,h,s,q;
		double a,f,u,v,t;

		
		v1 = this.p1.toVec();
		v2 = this.p2.toVec();
		v3 = this.p3.toVec();

		
		e1 = v2.add(v1.neg());
		e2 = v3.add(v1.neg());

		h = ray.direction().cross(e2);
		a = e1.dot(h);
		
		f = 1/a;
		s = ray.source().toVec().add(v1.neg());
		u = f*(s.dot(h));

		q = s.cross(e1);
		v = f*(ray.direction().dot(q));

		t = f*(e2.dot(q));

		// checking validity of the result
		if((Math.abs(a) < Ops.epsilon)||(u < 0) || (u > 1) || (u < 0)|| (u+v>1)){
			return new Hit(Ops.infinity,new Vec());	
		}

		// get the normal to the triangle
		Vec p2p1 = this.p2.toVec().add(this.p1.toVec().neg());
		Vec p3p1 = this.p3.toVec().add(this.p1.toVec().neg());
		Vec normal = p2p1.cross(p3p1);

		return new Hit(t, normal);
	}
}
