package edu.cg.scene.objects;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public abstract class Shape implements Intersectable {
	public Vec getDiffuseCoefficient(Material material, Point p) {
		return material.Kd1;
	}
}
