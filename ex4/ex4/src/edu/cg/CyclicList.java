package edu.cg;

import java.util.ArrayList;

public class CyclicList<T> extends ArrayList<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public T get(int i) {
		int size = size();
		return super.get(((i % size) + size) % size);
	}
}
