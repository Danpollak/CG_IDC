package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Dome extends Shape {
	private Sphere sphere;
	private Plain plain;
	
	public Dome() {
		sphere = new Sphere().initCenter(new Point(0, -0.5, -6));
		plain = new Plain(new Vec(-1, 0, -1), new Point(0, -0.5, -6));
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Dome:" + endl + 
				sphere + plain + endl;
	}
	
	@Override
	public Hit intersect(Ray ray) {
		Hit spheralHit = this.sphere.intersect(ray);
		Hit planerHit = this.plain.intersect(ray);
		Point spheralPnt = ray.getHittingPoint(spheralHit);
		Point planerPnt = ray.getHittingPoint(planerHit);
		
		if(spheralPnt.isFinite()){
			Vec arrow = spheralPnt.toVec()
			.add(this.sphere.getCenter().toVec().neg());

			double cos = arrow.dot(this.plain.normal());
			if (cos > 0){
				return spheralHit;
			} else if(planerPnt.isFinite()){
				return planerHit;
			}
		} 
		// no renderable points, BOO!
		throw new UnimplementedMethodException("intersect(Ray) Dome");
	}
}
