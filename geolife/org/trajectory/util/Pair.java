package org.trajectory.util;

public class Pair<L, R> {
	private final L element0;
	private final R element1;
	
	public static <L, R> Pair<L, R> createPair(L element0, R element1) {
		return new Pair<L, R>(element0, element1);
	}
	
	public Pair(L element0, R element1) {
		this.element0 = element0;
		this.element1 = element1;
	}
	
	public L getLeft() {
		return element0;
	}
	
	public R getRight() {
		return element1;
	}
}
