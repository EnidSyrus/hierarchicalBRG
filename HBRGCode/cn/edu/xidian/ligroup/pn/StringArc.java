package cn.edu.xidian.ligroup.pn;


public class StringArc {
	private String start;
	private String end;
	private String weight;
	public StringArc(String start, String end, String weight) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}
	public StringArc(String start, String end) {
		this.start = start;
		this.end = end;
		this.weight = "1";
	}
	public String getStart() {
		return start;
	}
	public String getEnd() {
		return end;
	}
	public String getWeight() {
		return weight;
	}
	@Override
	public String toString() {
		return start+"--"+weight+"-->"+end;
	}
}
