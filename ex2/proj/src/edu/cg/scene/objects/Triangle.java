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
		
		Vec p2p1 = this.p2.sub(this.p1);
		Vec p3p1 = this.p3.sub(this.p1);
		Vec normal = p2p1.cross(p3p1);

		double d = -(normal.dot(p1.toVec()));
		return new Plain( normal.x,normal.y, normal.z, d);
	}

	@Override
	public Hit intersect(Ray ray) {
		
		// check if the ray intersects the plane at all
		Plain plane = this.toPlane();
		Hit hit = plane.intersect(ray);
		if(hit == null) {
			return null;
		}
		// make sure that the intersection is within the triangle
		// using barycentric coordinates
		
		Point hittingPoint = ray.getHittingPoint(hit);
		Vec p2p1 = this.p2.sub(this.p1); // b - a
		Vec p3p1 = this.p3.sub(this.p1); // c - a
		Vec hitp1 = hittingPoint.sub(this.p1); // a - P
		double d00 = p2p1.normSqr();
		double d01 = p2p1.dot(p3p1);
		double d11 = p3p1.normSqr();
		double d20 = hitp1.dot(p2p1);
		double d21 = hitp1.dot(p3p1);
		double denom = d00 * d11 - d01 * d01 ;
		double alpha = (d11 * d20 - d01 * d21) / denom;
		double beta = (d00 * d21 - d01 * d20) / denom;
		double gamma = 1 - alpha - beta;
		
		if((0 <= alpha) && (0 <= beta) && (0 <= gamma) && (alpha <= 1) && (beta <= 1) && (gamma <= 1)) {
			return hit;
		}
		return null;
	}
}
