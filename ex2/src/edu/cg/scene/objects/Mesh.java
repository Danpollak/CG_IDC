package edu.cg.scene.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;

public class Mesh extends Shape implements Iterable<Triangle> {
	public static class Triplet {
		public int i1, i2, i3;
		
		public Triplet() {
			i1 = i2 = i3 = 0;
		}
		
		public Triplet(int i1, int i2, int i3) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
		}
	}
	
	public Triangle makeTriangle(Triplet triplet) {
		return new Triangle(vertices.get(triplet.i1),
				vertices.get(triplet.i2),
				vertices.get(triplet.i3));
	}
	
	private List<Point> vertices;
	private List<Triplet> indices;
	
	public Mesh initVertices(List<Point> vertices) {
		this.vertices = new ArrayList<>(vertices);
		return this;
	}
	
	public Mesh initIndices(List<Triplet> indices) {
		this.indices = indices;
		return this;
	}
	
	public Mesh initIndices(int[] indices) {
		List<Triplet> triplets = new ArrayList<>(indices.length / 3);
		
		for(int i = 0; i < indices.length; i += 3)
			triplets.add(new Triplet(indices[i], indices[i+1], indices[i+2]));
		
		return initIndices(triplets);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Mesh:" + endl + 
				"Vertices:" + endl + vertices + endl +
				"Triangles: " + indices + endl;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return new Iterator<Triangle>() {
			private int currentTripletIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentTripletIndex < indices.size();
			}

			@Override
			public Triangle next() {
				if(!hasNext())
					throw new NoSuchElementException();
				
				Triplet triplet = indices.get(currentTripletIndex++);
				return makeTriangle(triplet);
			}
		};
	}

	@Override
	public Hit intersect(Ray ray) {
		//TODO: implement this method.
		throw new UnimplementedMethodException("intersect(Ray)");
	}

}
