package cn.edu.xidian.ligroup.pn;

/*
 * 表示一个 arc，因为主要用于初始化PN，为了更方便操作，arc 的三个属性用字符串进行表示，就像其名字一样，StringArc
 */
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
