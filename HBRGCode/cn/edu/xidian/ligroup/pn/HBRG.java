package cn.edu.xidian.ligroup.pn;

import java.util.HashMap;
import java.util.List;

public class HBRG {
	private BRG primaryBRG;
	private List<BRGNode> primaryNodes;
	
	private HashMap<BRGNode, BRG> secondaryBRGs;
	
	public HBRG(BRG primaryBRG) {
		this.primaryBRG = primaryBRG;
		this.primaryNodes = primaryBRG.getNodes();
		
		secondaryBRGs = new HashMap<BRGNode, BRG>();
	}
	
	public void addSecondLayer(BRGNode key, BRG brg) {
		secondaryBRGs.put(key, brg);
	}
	
	public String toString() {
		StringBuilder retStr = new StringBuilder();
		retStr.append("Primary BRG\t\n");
		retStr.append(primaryBRG);
		retStr.append("\t\n\t\n Secondary Layer BRGs\t\n");
		
		for(BRGNode node : primaryNodes) {
			BRG brg = secondaryBRGs.get(node);
			retStr.append(node + "\t\n");
			retStr.append(brg);
			retStr.append("t\n================================\t\n");
		}
		
		return retStr.toString();
	}
}
