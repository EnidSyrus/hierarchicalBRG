package cn.edu.xidian.ligroup.pn;

import java.util.Arrays;


public class ArrayMarking {
	private int[] M;
	
	public ArrayMarking(int[] M) {
		this.M = M;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(M);
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
		ArrayMarking other = (ArrayMarking) obj;
		if (!Arrays.equals(M, other.M))
			return false;
		return true;
	}
	
	public int[] getArray() {
		return M;
	}

	@Override
	public String toString() {
		return Arrays.toString(M).toString();
	}
}
