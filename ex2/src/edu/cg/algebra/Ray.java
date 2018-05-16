package edu.cg.algebra;

import edu.cg.scene.objects.Surface;

public class Ray {
	private final Point source;
	private final Vec direction;
	private Surface surface;
	
	public Ray(Point source, Vec direction) {
		this.source = source;
		this.direction = direction.normalize();
		this.surface = null;
	}
	
	public Ray(Point p0, Point p1) {
		this(p0, p1.sub(p0));
	}
	
	public Point source() {
		return source;
	}
	
	public Vec direction() {
		return direction;
	}
	
	public Surface surface() {
		return surface;
	}
	
	public Point add(double t) {
		// returns: p0 + t*direction
		return source.add(t, direction);
	}
	
	public Point getHittingPoint(Hit hit) {
		return add(hit.t());
	}
	
	public Ray inverse() {
		return new Ray(source, direction.neg());
	}
	
	public void setSurface(Surface surface) {
		this.surface = surface;
	}
	
}
