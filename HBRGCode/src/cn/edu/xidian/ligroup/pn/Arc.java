package cn.edu.xidian.ligroup.pn;

public class Arc {
	
	private Place place;
	private int weight;
	
	public Arc(Place place, int weight) {
		this.place = place;
		this.weight = weight;
	}

	public Place getPlace() {
		return place;
	}

	public int getWeight() {
		return weight;
	}
}
