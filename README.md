# hierarchicalBRG

This is a repository for the paper named "Marking Estimation in Petri Nets Using Hierarchical Basis
Reachability Graphs".


Please see the PDF file named "Benchmark_HBRG.pdf" for the benchmarks described in the paper.

<h1>Please see HBRGCode directory for the codes of the paper.</h1>  

This program requires Java Development Kit (JDK) 6 or higher version.

We implement the key idea of the paper in <B>PN.java</B>, especially <B>getBRG</B> and <B>getBRGWithVector</B> functions.

The entry point of the program is the <B>main</B> function in <B>EntryPoint.java</B>

To test the efficiency of the proposed hierarchical BRG, the reader can call the function <B>testHBRG</B> in <B>EntryPoint.java</B>, whose declear is

```java
void testHBRG(int[][] Pre, int[][] Post, int[] M0, List<String> To, List<String> Tpri)
```

  ## Input of the program:
  * input a net system <N,M0> by its <B>Pre</B> matrix, <B>Post</B> matrix, the initial marking <B>M0</B>
  - the observable transtion set <B>To</B>
  * the set of primary observable transitions <B>Tpri</B>
  
 ## Output of the program:
 * BRG (and its node number)
 - HBRG (and its node number)
 * the time to generate BRG and HBRG
 
 #### The following is an example to run the program by testing the HBRG of the net shown in Figure 1 of the paper.
 ```java
public static void main(String[] args) {
		//Pre matrix
		int[][] Pre = {
				     /*t1,t2,t3,t4,t5,t6,t7,t8,t9*/
				/*p1*/{1, 0, 0, 0, 0, 0, 0, 0, 0},
				/*p2*/{0, 1, 0, 0, 0, 0, 0, 0, 0},
				/*p3*/{0, 0, 1, 0, 0, 0, 0, 0, 0},
				/*p4*/{0, 0, 0, 2, 0, 0, 0, 1, 0},
				/*p5*/{0, 0, 0, 0, 1, 0, 0, 0, 0},
				/*p6*/{0, 0, 0, 0, 0, 2, 0, 0, 0},
				/*p7*/{0, 0, 0, 0, 0, 0, 2, 0, 0},
				/*p8*/{0, 0, 0, 0, 0, 0, 0, 0, 1},
		};
		//Post matrix
		int[][] Post = {
				{0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,1,0,0},
				{0,1,0,0,0,0,0,0,0},
				{0,0,1,0,0,0,1,0,0},
				{0,0,0,2,0,0,0,0,0},
				{0,0,0,0,1,0,0,0,0},
				{0,0,0,0,0,2,0,0,0},
				{0,0,0,0,0,0,0,1,0},
		};
		//initial marking (one can change the token numbers in p1 and p7)
		int[] M0 = {1,//p1
		            0,0,0,0,0,
		            2,//p7
		            0};
		// observable transition set To={t2,t4,t6,t7,t9}
		List<String> To = Arrays.asList("t2","t4","t6","t7","t9");
		// primary observable transition Tpri = {t4,t6,t9}
		List<String> Tpri = Arrays.asList("t4","t6","t9");
		
		//To test the efficiency of HBRG
		testHBRG(Pre, Post, M0, To, Tpri) ;
	}
```
 # Some explanations of this program
1. The net of benchmark 1 is so big that it is not easy to get its Pre and Post matrices. So, we provide its Pre and Post matrices in EntryPoint.java (see Lines 103 - 200). The reader or reviewer can conveniently test the result of Table I in HBRG-Benchmark.pdf.
2. The <B>Pre</B> and <B>Post</B> matrices of the net <B>$N_{T\backslash T_{pri}}$</B> are generated in Lines 48-63 of EntryPoint.java
3. We re-numbered the set $T_{uo}$ for net $N_{T\backslash T_{pri}}$ (Line 65 of EntryPoint.java) since the dimension of the net changes from |P| x |T| to |P| x |T\Tpri|.
4. In EntryPoint.java, Line 79 generates the BRG; Lines 87-96 generate HBRG.
