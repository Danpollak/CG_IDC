package edu.cg.scene.objects;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Ray;

public interface Intersectable {
	public Hit intersect(Ray ray);
}
