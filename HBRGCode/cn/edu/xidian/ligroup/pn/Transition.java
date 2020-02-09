package cn.edu.xidian.ligroup.pn;

import java.util.ArrayList;
import java.util.List;

public class Transition implements Comparable<Transition> {

	private String name;
	private List<Arc> preSet;
	private List<Arc> postSet;
	private boolean unobservable;
	private boolean fault;
	
	public Transition(String name) {
		this.name = name;
		preSet = new ArrayList<Arc>();
		postSet = new ArrayList<Arc>();
		unobservable = false;
		fault = false;
	}
	
	public Transition(String name, boolean unobservable, boolean fault) {
		this.name = name;
		this.unobservable = unobservable;
		this.fault = fault;
		preSet = new ArrayList<Arc>();
		postSet = new ArrayList<Arc>();
	}
	
	public boolean isUnobservable() {
		return this.unobservable;
	}
	public boolean isFault() {
		return this.fault;
	}
	public void setUnobservable(boolean value) {
		this.unobservable = value;
	}
	public void setFault(boolean fault) {
		this.fault = fault;
	}
	public void addPreArc(Arc arc) {
		preSet.add(arc);
	}

	public void addPostArc(Arc arc) {
		postSet.add(arc);
	}


	public List<Arc> getPreSet() {
		return preSet;
	}


	public List<Arc> getPostSet() {
		return postSet;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition other = (Transition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	@Override
	public int compareTo(Transition t) {
		return name.compareToIgnoreCase(t.name);
	}
	
	public boolean isEnabled() {
		boolean enabled = true;
		List<Arc> preSet = getPreSet();
		for(Arc arc : preSet) {
			if(arc.getPlace().getToken() < arc.getWeight()) {
				enabled = false;
				break;
			}
		}
		return enabled;
	}
	
	public void fire() {
		List<Arc> preSet = getPreSet();
		for(Arc arc : preSet) {
			Place p = arc.getPlace();
			p.setToken(p.getToken() - arc.getWeight());
		}
		
		List<Arc> postSet = getPostSet();
		for(Arc arc : postSet) {
			Place p = arc.getPlace();
			p.setToken(p.getToken() + arc.getWeight());
		}
	}

	@Override
	public String toString() {
		return name;
	}
	
}
