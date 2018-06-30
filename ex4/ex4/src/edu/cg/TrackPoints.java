package edu.cg;

import edu.cg.algebra.Point;

public class TrackPoints {

	public static CyclicList<Point> track1() {
		CyclicList<Point> ans = new CyclicList<>();
		ans.add(new Point(.7,-.2,.1));
		ans.add(new Point(-.2,-.6,.5));
		ans.add(new Point(-.7,-.4,.6));
		ans.add(new Point(-.78,0,.6));
		ans.add(new Point(-.7,.4,.6));
		ans.add(new Point(-.2,.6,.5));
		ans.add(new Point(.7,.2,.1));
		return ans;
	}

	public static CyclicList<Point> track2() {
		CyclicList<Point> ans = new CyclicList<>();
		ans.add(new Point(0, 0, 0));
		ans.add(new Point(-.5, 0, .5));
		ans.add(new Point(0, 0, 1));
		ans.add(new Point(.5, 0, .5));
		return ans;
	}

	public static CyclicList<Point> track3() {
		CyclicList<Point> ans = new CyclicList<>();
		ans.add(new Point(-.5,-.5,0));
		ans.add(new Point(-.5,.5,.5));
		ans.add(new Point(.5,.5,0));
		ans.add(new Point(.5,-.5,.5));
		return ans;
	}
	
	//TODO: add more functions track4(), track5()...
}
