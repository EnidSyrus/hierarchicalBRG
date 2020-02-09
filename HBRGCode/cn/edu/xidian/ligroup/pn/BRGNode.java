package cn.edu.xidian.ligroup.pn;

import java.util.Arrays;

public class BRGNode {
	private ArrayMarking marking;
	private int[] vector;
	
	public BRGNode(ArrayMarking marking, int[] vector) {
		this.marking = marking;
		this.vector = vector;
	}

	@Override
	public String toString() {
		return "<" + marking + "," + Arrays.toString(vector) + ">";
	}

	public ArrayMarking getMarking() {
		return marking;
	}

	public int[] getVector() {
		return vector;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((marking == null) ? 0 : marking.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BRGNode other = (BRGNode) obj;
		if (marking == null) {
			if (other.marking != null)
				return false;
		} else if (!marking.equals(other.marking))
			return false;
		return true;
	}
}
