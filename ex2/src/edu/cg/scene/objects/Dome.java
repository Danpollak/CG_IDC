package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
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

		// if there's not sphere hit, there's no dome hit
		if(spheralHit == null) {
			return null;
		}

		Hit planerHit = this.plain.intersect(ray);
		Point spheralPnt = ray.getHittingPoint(spheralHit);
		Point planerPnt = ray.getHittingPoint(planerHit);
		double cameraToPlane = this.plain.equationValue(ray.source());
		double hitToPlane = this.plain.equationValue(spheralHit);
		boolean isWithin - spheralHit.isWithinTheSurface();

		if(isWithin){
			// handling an inner hit
			spheralHit.setWithin();
			if(cameraToPlane > Ops.epsilon){
				// it means that the surface is in front of the camera
				// check if the sphere is in front of the plane
				if(hitToPlane > 0){
					// the hit is on the sphere
					return spheralHit;
				} else {
					// the hit might be on the plane
					if(planerHit == null){
						return null;
					}
				}
				// the hit is not on the plane but only on the inner sphere
				return spheralHit;
			}
			// check if the ray hit the plane before it hit the sphere. if so, return the intersection
			if(hitToPlane > 0){
				return planerHit;
			}	
			return null;
		} else {
			// handling an outside hit
			// check if the ray hit the sphere before it hit the plane. if so, return the sphere hit
			if(hitToPlane > 0){
				return spheralHit;
			}
			// check that the ray hits the plane
			if(planerHit == null){
				return null;
			}
			// now check if the hit is "inside" the missing half of the sphere
			if(this.sphere.pointInSphere(planerHit)){
				return planerHit;
			}
			return null;
		}
		return null;
		// if(spheralPnt.isFinite()){
		// 	Vec arrow = spheralPnt.toVec()
		// 	.add(this.sphere.getCenter().toVec().neg());

		// 	double cos = arrow.dot(this.plain.normal());
		// 	if (cos > 0){
		// 		return spheralHit;
		// 	} else if(planerPnt.isFinite()){
		// 		return planerHit;
		// 	}
		// } 
		
		// return new Hit(Ops.infinity, new Vec());
	}
}
