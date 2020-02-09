package cn.edu.xidian.ligroup.pn;

import java.util.List;

public class BRG {
	private List<BRGNode> nodes;
	private List<BRGArc> arcs;
	public BRG(List<BRGNode> nodes, List<BRGArc> arcs) {
		this.nodes = nodes;
		this.arcs = arcs;
	}
	public List<BRGNode> getNodes() {
		return nodes;
	}
	public List<BRGArc> getArcs() {
		return arcs;
	}
	
	public int getNodeNum() {
		return nodes.size();
	}
	
	public String toString() {
		return nodes + "\t\n" + arcs;
	}
}
