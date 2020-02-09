package cn.edu.xidian.ligroup.pn;

import java.util.Arrays;

public class BRGArc {
	private BRGNode M1;
	private BRGNode M2;
	private Transition transition;
	private int[] evector;
	
	public BRGArc(BRGNode m1, BRGNode m2, Transition transition, int[] evector) {
		M1 = m1;
		M2 = m2;
		this.transition = transition;
		this.evector = evector;
	}

	@Override
	public String toString() {
		return "(" + M1 + "---"+
					transition + Arrays.toString(evector) + "--->" 
							+ M2 + ")";
	}
	
}
