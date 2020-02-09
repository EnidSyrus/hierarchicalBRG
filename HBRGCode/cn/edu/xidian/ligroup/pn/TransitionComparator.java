package cn.edu.xidian.ligroup.pn;

import java.util.Comparator;

public class TransitionComparator implements Comparator<Transition> {

	@Override
	public int compare(Transition o1, Transition o2) {
		String name1 = o1.getName();
		String name2 = o2.getName();
		int nameLen1 = name1.length();
		int nameLen2 = name2.length();
		if(nameLen1 == nameLen2) {
			return name1.compareTo(name2);
		} else {
			return nameLen1 - nameLen2;
		}
	}

}
