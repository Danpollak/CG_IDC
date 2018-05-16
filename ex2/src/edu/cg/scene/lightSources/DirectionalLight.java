package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public class DirectionalLight extends Light {
	private Vec direction = new Vec(0, -1, -1);

	public DirectionalLight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Directional Light:" + endl + super.toString() +
				"Direction: " + direction + endl;
	}

	@Override
	public DirectionalLight initIntensity(Vec intensity) {
		return (DirectionalLight)super.initIntensity(intensity);
	}
	
	@Override
	public Vec getDirection(Point p) {
		return this.direction;
	}
	
	public Vec getIntensity(Point p) {
		return this.intensity;
	}
	
	public Vec getLightOnHitPoint(Point p) {
		return this.getDirection(null).mult(this.getIntensity(null));
	}
}
