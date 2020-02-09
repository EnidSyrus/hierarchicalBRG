package cn.edu.xidian.ligroup.pn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PN {

	private List<Place> sortedPlaces;
	private List<Transition> sortedTransitions;
	private ArrayMarking initialMarking;

	private Transition[] unobservableFaultTransitions;
	private Transition[] observableTransitions;

	private int[][] Pre;
	private int[][] Post;
	private int[][] N;
	private int[][] Nuf;
	private int[][] NufT;
	
	
	private int placeNum;
	private int transitionNum;
	private int observableTranNum;
	private int unobservableTranNum;
	private int faultTranNum;
	private int unobservableFaultTranNum;
	
	/*To construct a Petri net by string sequences*/
	public PN(List<String> places, List<String> transitions, List<StringArc> ptArcs, List<StringArc> tpArcs) {
		consPN(places, transitions, ptArcs, tpArcs);
		consUnoTran(new ArrayList<String>(), new ArrayList<String>());
	}

	private void consPN(List<String> places, List<String> transitions, List<StringArc> ptArcs, List<StringArc> tpArcs) {
		sortedPlaces = new ArrayList<Place>();
		sortedTransitions = new ArrayList<Transition>();

		for (String name : places) {
			int delimiter = name.indexOf('-');
			if (delimiter == -1) {
				sortedPlaces.add(new Place(name));
			} else {
				String pname = name.substring(0, delimiter);
				int token = Integer.parseInt(name.substring(delimiter + 1));
				sortedPlaces.add(new Place(pname, token));
			}
		}
		for (String name : transitions) {
			sortedTransitions.add(new Transition(name));
		}
		
		Collections.sort(sortedPlaces, new PlaceCompartor());
		Collections.sort(sortedTransitions, new TransitionComparator());

		for (StringArc arc : ptArcs) {
			String pName = arc.getStart();
			String tName = arc.getEnd();
			int weight = Integer.parseInt(arc.getWeight());

			Place p = null;
			Transition t = null;
			int index = Collections.binarySearch(sortedPlaces, new Place(pName), new PlaceCompartor());
			if (index < 0)
				throw new RuntimeException("There is no place named " + pName);
			p = sortedPlaces.get(index);

			index = Collections.binarySearch(sortedTransitions, new Transition(tName), new TransitionComparator());
			if (index < 0) {
				throw new RuntimeException("There is no transition named " + tName);
			}
			t = sortedTransitions.get(index);

			t.addPreArc(new Arc(p, weight));
		}

		for (StringArc arc : tpArcs) {
			String tName = arc.getStart();
			String pName = arc.getEnd();
			int weight = Integer.parseInt(arc.getWeight());

			Transition t = null;
			Place p = null;

			int index = Collections.binarySearch(sortedTransitions, new Transition(tName),new TransitionComparator());

			if (index < 0) {
				throw new RuntimeException("There is no transition named " + tName);
			}
			t = sortedTransitions.get(index);

			index = Collections.binarySearch(sortedPlaces, new Place(pName), new PlaceCompartor());
			if (index < 0)
				throw new RuntimeException("There is no place named " + pName);
			p = sortedPlaces.get(index);

			t.addPostArc(new Arc(p, weight));
		}

		initialMarking = new ArrayMarking(getM0());
		initPrePostAndN();
		placeNum = sortedPlaces.size();
		transitionNum = sortedTransitions.size();
	}

	/*To construct a Petri net with unobservable, faulty, primary observable, secondary observable transitions by string sequences*/
	public PN(List<String> places, List<String> transitions, List<StringArc> ptArcs, List<StringArc> tpArcs,
			List<String> uncontrols, List<String> faults) {
		consPN(places, transitions, ptArcs, tpArcs);
		consUnoTran(uncontrols, faults);
	}

	private void consUnoTran(List<String> unobservables, List<String> faults) {
		Transition t = null;
		int index = -1;
		for(String tran : unobservables) {
			index = Collections.binarySearch(sortedTransitions, new Transition(tran), new TransitionComparator());
			if (index < 0) {
				throw new RuntimeException("There is no transition named " + tran);
			}
			t = sortedTransitions.get(index);
			t.setUnobservable(true);
		}
		// set fault flag
		for(String tran : faults) {
			index = Collections.binarySearch(sortedTransitions, new Transition(tran), new TransitionComparator());
			if (index < 0) {
				throw new RuntimeException("There is no transition named " + tran);
			}
			t = sortedTransitions.get(index);
			t.setFault(true);
		}
		
		unobservableTranNum = unobservables.size();
		faultTranNum = faults.size();
		unobservableFaultTranNum = unobservableTranNum + faultTranNum;
		observableTranNum = transitionNum - unobservableFaultTranNum;
		
		unobservableFaultTransitions = new Transition[transitionNum];
		observableTransitions = new Transition[transitionNum];
		
		for (int i = 0; i < transitionNum; i++) {
			t = sortedTransitions.get(i);
			if (unobservables.contains(t.getName()) || faults.contains(t.getName())) {//unobservable
				unobservableFaultTransitions[i] = t;
				observableTransitions[i] = null;
			} else { //observable
				unobservableFaultTransitions[i] = null;
				observableTransitions[i] = t;
			}
		}
		
		Nuf = new int[placeNum][unobservableFaultTranNum];
		int i = 0;
		int j = 0;
		int k = 1;
		for(i = 0; i < transitionNum; i++) {
			if(unobservableFaultTransitions[i] != null) {
				for(j = 0; j < placeNum; j++) {
					Nuf[j][k-1] = N[j][i];
				}
				k++;
			}
		}
		
		NufT = new int[unobservableFaultTranNum][placeNum];
		for(i = 0,k = 0; i < placeNum; i++,k++) {
			for(j = 0; j < unobservableFaultTranNum; j++) {
				NufT[j][k] = Nuf[i][j];
			}
		}
	}
	
	/* To construct a Petri net by its Pre/Post matrices and initial marking M0
	   Note that places are named p1,p2,p3,... by this constructor. Accordingly, transitions are named t1,t2,t3,...
	  */
	public PN(int[][] Pre, int[][] Post, int[] M0) {
		if (Pre.length != Post.length || Pre[0].length != Post[0].length) {
			throw new RuntimeException("The dimension of Pre can not match that of Post");
		}
		int pnum = Pre.length;
		int tnum = Pre[0].length;

		ArrayList<String> places = new ArrayList<String>();
		ArrayList<String> transitions = new ArrayList<String>();
		ArrayList<StringArc> ptArcs = new ArrayList<StringArc>();
		ArrayList<StringArc> tpArcs = new ArrayList<StringArc>();

		for (int i = 0; i < tnum; i++) {
			transitions.add("t" + (i + 1));
		}
		for (int i = 0; i < pnum; i++) {
			places.add("p" + (i + 1) + "-" + M0[i]);
		}
		for (int i = 0; i < pnum; i++) {
			for (int j = 0; j < tnum; j++) {
				if (Pre[i][j] < 0) {
					throw new RuntimeException("The element of Pre must be non-negative");
				} else if (Pre[i][j] > 0) {
					ptArcs.add(new StringArc("p" + (i + 1), "t" + (j + 1), "" + Pre[i][j]));
				}

			}
		}
		for (int i = 0; i < pnum; i++) {
			for (int j = 0; j < tnum; j++) {
				if (Post[i][j] < 0) {
					throw new RuntimeException("The element of Pre must be non-negative");
				} else if (Post[i][j] > 0) {
					tpArcs.add(new StringArc("t" + (j + 1), "p" + (i + 1), "" + Post[i][j]));
				}

			}
		}
		consPN(places, transitions, ptArcs, tpArcs);
	}

	public PN(int[][] Pre, int[][] Post, int[] M0, List<String> unobservables, List<String> faults) {
		this(Pre,Post,M0);
		consUnoTran(unobservables, faults);
	}

	private int[] getM0() {
		int[] m0 = new int[sortedPlaces.size()];
		for (int i = 0; i < sortedPlaces.size(); i++) {
			m0[i] = sortedPlaces.get(i).getToken();
		}
		return m0;
	}

	private void initPrePostAndN() {
		int rowNum = sortedPlaces.size();
		int columnNum = sortedTransitions.size();
		Pre = new int[rowNum][columnNum];
		Post = new int[rowNum][columnNum];
		N = new int[rowNum][columnNum];
		initZeroMatrix(Pre);
		initZeroMatrix(Post);
		initZeroMatrix(N);
		int wtp = 0, wpt = 0;

		Transition t = null;
		Place p = null;
		for (int i = 0; i < rowNum; i++) {
			p = sortedPlaces.get(i);
			for (int j = 0; j < columnNum; j++) {
				t = sortedTransitions.get(j);
				wtp = 0;
				List<Arc> postArcs = t.getPostSet();
				for (Arc arc : postArcs) {
					if (arc.getPlace().equals(p)) {
						wtp = arc.getWeight();
						Post[i][j] = wtp;
						break; // Assume that there is only one arc from p to t
								// if arc exists.
					}
				}
				wpt = 0;
				List<Arc> preArcs = t.getPreSet();
				for (Arc arc : preArcs) {
					if (arc.getPlace().equals(p)) {
						wpt = arc.getWeight();
						Pre[i][j] = wpt;
						break;
					}
				}
				N[i][j] = wtp - wpt;
			}
		}
	}

	private void initZeroMatrix(int[][] matrix) {
		int row = matrix.length;
		int column = matrix[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				matrix[i][j] = 0;
			}
		}
	}

	public int[][] getN() {
		return N;
	}

	public int[][] getPre() {
		return Pre;
	}

	public int[][] getPost() {
		return Post;
	}
	
	public int[][] getNuf() {
		return Nuf;
	}
	public int[][] getNufT() {
		return NufT;
	}
	public ArrayMarking getInitialMarking() {
		return initialMarking;
	}
	
	public void setInitialMarking(ArrayMarking M0) {
		this.initialMarking = M0;
	}
	
	private int[][] computeYminMt(ArrayMarking M, int tindex) {
		List<int[]> AB = new ArrayList<int[]>();
		int[] mArray = M.getArray();
		int columnNum = placeNum + unobservableFaultTranNum;
		int[] initRow = new int[columnNum];
		int i = 0;
		int j = 0;
		for(i = 0; i < placeNum; i++) {
			initRow[i] = mArray[i] - Pre[i][tindex];
		}
		for(i = placeNum; i < columnNum; i++) {
			initRow[i] = 0;
		}
		AB.add(initRow);
		
		int columnCount = placeNum;
		
		int Aistarjstar = 0;
		int istar = 0;
		int jstar = 0;
		int[] tempRowContent = null;
		
		boolean AGEZero = false;
		while(AGEZero == false) {
			boolean isFind = false;
			for(istar = 0; istar < AB.size(); istar++) {
				tempRowContent = AB.get(istar);
				for(jstar = 0; jstar < columnCount; jstar++) {
					if(tempRowContent[jstar] < 0) {
						Aistarjstar = tempRowContent[jstar];
						isFind = true;
						break;
					}
				}
				if(isFind == true)
					break;
			}
			if(isFind == false) { // A >= 0
				AGEZero = true;
				continue;
			}
			
			List<Integer> IPlus = new ArrayList<Integer>();
			for(i = 0; i < unobservableFaultTranNum; i++) {
				if(NufT[i][jstar] > 0) {
					IPlus.add(i);
				}
			}
			if(IPlus.size() == 0) {
				AB.remove(istar);
				continue;
			}
			
			for(Integer ii : IPlus) {
				initRow = new int[columnNum];
				int[] AIstar = AB.get(istar);
				for(i = 0; i < placeNum; i++) {
					initRow[i] = AIstar[i] + NufT[ii][i];
				}
				int x = 0;
				for(i = placeNum; i < columnNum; i++) {
					if(x == ii) {
						initRow[i] = AIstar[i] + 1;
					} else {
						initRow[i] = AIstar[i];
					}
					x++;
				}
				AB.add(initRow);
			}
			AB.remove(istar);
		}
		
		int[] tempRowContent2 = null;
		for(i = 0; i < AB.size(); i++) {
			tempRowContent = AB.get(i);
			if(tempRowContent[0] == Integer.MIN_VALUE) {
				continue;
			}
			for(j = 0; j < AB.size(); j++) {
				if(j == i) {
					continue;
				}
				tempRowContent2 = AB.get(j);
				if(tempRowContent2[0] == Integer.MIN_VALUE) {
					continue;
				}
				boolean greater = true;
				for(int k = placeNum; k < columnNum; k++) {
					greater = greater && (tempRowContent2[k] >= tempRowContent[k]);
				}
				if(greater) {
					tempRowContent2[0] = Integer.MIN_VALUE;
				}
			}
		}
		
		int totalNum = 0;
		for(i = 0; i < AB.size(); i++) {
			tempRowContent = AB.get(i);
			if(tempRowContent[0] != Integer.MIN_VALUE)
				totalNum++;
		}
		
		int[][] result = new int[totalNum][unobservableFaultTranNum];
		int tempNum = -1;
		for(i = 0; i < AB.size(); i++) {
			tempRowContent = AB.get(i);
			if(tempRowContent[0] != Integer.MIN_VALUE) {
				tempNum++;
				for(j = placeNum; j < columnNum; j++) {
					result[tempNum][j-placeNum] = tempRowContent[j];
				}
			}
		}
		
		int index = -1;
		int[][] YminMt = new int[totalNum][transitionNum];
		for(i = 0; i < totalNum; i++) {
			index = 0;
			for(j = 0; j < transitionNum; j++) {
				if(unobservableFaultTransitions[j] != null) {
					YminMt[i][j] = result[i][index];
					index++;
				} else {
					YminMt[i][j] = 0;
				}
			}
		}
		return YminMt;
	}

	/* Compute the basis marking graph*/
	public BRG getBRG() {
		List<BRGNode> marked = new ArrayList<BRGNode>();
		List<BRGArc> brgArcs = new ArrayList<BRGArc>();
		Queue<BRGNode> unmarked = new LinkedList<BRGNode>();
		
		int[] zeroVec = new int[transitionNum];
		marked.add(new BRGNode(initialMarking, zeroVec));
		unmarked.add(new BRGNode(initialMarking, zeroVec));
		
		BRGNode oldNode = null;
		BRGNode newNode = null;
		ArrayMarking oldM = null;
		ArrayMarking newM = null;
		int[] oldMA = null;
		int[] newMA = null;
		while (!unmarked.isEmpty()) {
			oldNode = unmarked.remove();
			oldM = oldNode.getMarking();
			for (int i = 0; i < transitionNum; i++) {
				if(observableTransitions[i] == null) 
					continue;
				int[][] Ymins = computeYminMt(oldM, i);
				if (Ymins.length == 0)
					continue;
				
				oldMA = oldM.getArray();
				for(int j = 0; j < Ymins.length; j++) {
					int[] evector = Ymins[j];
					int[] CuYmin = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						for(int y = 0; y < transitionNum; y++) {
							CuYmin[x] += N[x][y] * evector[y]; 
						}
					}
					//fire ti
					int[] Nt = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						Nt[x] = N[x][i];
					}
					
					
					newMA = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						newMA[x] = oldMA[x] + CuYmin[x] + Nt[x];
					}
					
					newM = new ArrayMarking(newMA);
					newNode = new BRGNode(newM, zeroVec);
					if (!marked.contains(newNode)) {
						marked.add(newNode);
						unmarked.add(newNode);
					} 
					
					brgArcs.add(new BRGArc(oldNode, newNode, sortedTransitions.get(i), evector));	
				}
			}
		}

		return new BRG(marked, brgArcs);
	}
	
	
	public BRG getBRGWithVector() {
		List<BRGNode> marked = new ArrayList<BRGNode>();
		List<BRGArc> brgArcs = new ArrayList<BRGArc>();
		Queue<BRGNode> unmarked = new LinkedList<BRGNode>();
		
		int[] zeroVec = new int[transitionNum];
		marked.add(new BRGNode(initialMarking, zeroVec));
		unmarked.add(new BRGNode(initialMarking, zeroVec));
		
		BRGNode oldNode = null;
		BRGNode newNode = null;
		ArrayMarking oldM = null;
		ArrayMarking newM = null;
		int[] oldMA = null;
		int[] newMA = null;
		while (!unmarked.isEmpty()) {
			oldNode = unmarked.remove();
			oldM = oldNode.getMarking();
			
			for (int i = 0; i < transitionNum; i++) {
				if(observableTransitions[i] == null) 
					continue;
				int[][] Ymins = computeYminMt(oldM, i);
				if (Ymins.length == 0)
					continue;
				
				oldMA = oldM.getArray();
				for(int j = 0; j < Ymins.length; j++) {
					int[] evector = Ymins[j];
					int[] CuYmin = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						for(int y = 0; y < transitionNum; y++) {
							CuYmin[x] += N[x][y] * evector[y]; 
						}
					}
					
					int[] vec = oldNode.getVector();
					int[] nodeVec = new int[transitionNum];
					for(int a = 0; a < transitionNum; a++) {
						nodeVec[a] = vec[a];
					}
					
					for(int a = 0; a < transitionNum; a++) {
						nodeVec[a] = nodeVec[a] + evector[a];
					}
					
					//fire ti
					int[] Nt = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						Nt[x] = N[x][i];
					}
					
					nodeVec[i] = nodeVec[i] + 1;
					
					newMA = new int[placeNum];
					for(int x = 0; x < placeNum; x++) {
						newMA[x] = oldMA[x] + CuYmin[x] + Nt[x];
					}
					
					newM = new ArrayMarking(newMA);
					newNode = new BRGNode(newM, nodeVec);
					if (!marked.contains(newNode)) {
						marked.add(newNode);
						unmarked.add(newNode);
					} 
					
					brgArcs.add(new BRGArc(oldNode, newNode, sortedTransitions.get(i), evector));	
				}
			}
		}

		return new BRG(marked, brgArcs);
	}
}
