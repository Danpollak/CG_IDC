package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public class Spotlight extends PointLight {
	private Vec direction;
	private double angle = 0.866; //cosine value ~ 30 degrees
	
	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}
	
	public Spotlight initAngle(double angle) {
		this.angle = angle;
		return this;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() + 
				"Direction: " + direction + endl +
				"Angle: " + angle + endl;
	}
	
	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}
	
	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}
	
	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}
	
	@Override
	public Vec getDirection(Point src) {
		Vec direction = this.position.sub(src);
		return direction;
	}
	
	public Vec getIntensity(Point src) {
		double d = this.position.sub(src).norm();
		double theta = this.direction.dot(getDirection(src));
		double decay = this.kc + this.kl*d + this.kq*d*d;
		return this.intensity.mult(theta/decay);
	}
	
	public Vec getLightOnHitPoint(Point src) {
		return this.getDirection(src).mult(this.getIntensity(src));
	}
}