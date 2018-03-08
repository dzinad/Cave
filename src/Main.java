import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Main {

    private static final String INPUT_FILE_NAME = "input.in";
    private static final String OUTPUT_FILE_NAME = "output.out";
    private static int n, k, junctionsNum;
    private static Stack<Integer> way;
    private static Stack<Integer> innerWay = new Stack<>();
    private static int[][] matrix, matrixCopy;
    private static int[] starts, ends;
    private static boolean ifEnter = true;
    //private static HashMap<Integer, Stack<Integer>> innerWays;
    private static int[][] coverage;
    private static InnerWay[] innerWays;
    private static int innerWaysLength = 0;
    private static Item[][] res;    

    static class InnerWay {
    	private int start, end;
    	private Stack<Integer> innerGrots;
    	private BigInteger coverage;
    	private int complexity = 0;
    	
    	public InnerWay(int start, int end, Stack<Integer> innerGrots, String coverage) {
    		this.start = start;
    		this.end = end;
    		this.innerGrots = innerGrots;
    		this.coverage = new BigInteger(coverage);
    		setComplexity();
    	}
    	
    	public BigInteger getCoverage() {
    		return coverage;
    	}
    	
    	private void setComplexity() {
    		int prev = start;
    		for (Integer grot: innerGrots) {
    			complexity += matrix[prev][grot];
    			prev = grot;
    		}
    		complexity += matrix[prev][end];
    	}
    	
    	public int getComplexity() {
    		return complexity;
    	}
    	
    	@Override
    	public String toString() {
    		return start + " - " + end;
    	}
    }
    
    static class Item {
    	//private List<List<Integer>> ways = new ArrayList<>(); //индексы в массиве innerWays
    	private HashMap<BigInteger, Integer> complexity = new HashMap<>();
    	private HashMap<BigInteger, Set<InnerWay>> coverage = new HashMap<>();
    	
    	public Item() {}
    	
    	public void addWays(Set<InnerWay> w) {
    		BigInteger res = null;
    		int c = 0;
    		for (InnerWay iw: w) {
    			if (res == null) {
    				res = iw.getCoverage();
    			}
    			else {
    				res  = res.or(iw.getCoverage());
    			}
    			c += iw.getComplexity();
    		}
    		Integer prevC = complexity.get(res);
    		if (prevC != null && prevC <= c) {
    			return;
    		}
    		coverage.put(res, w);
    		complexity.put(res, c);
    	}
    	
    	public void mergeWays(Item item1, Item item2) {
    		for (BigInteger cov1: item1.coverage.keySet()) {
    			Set<InnerWay> ways1 = item1.coverage.get(cov1);
    			//int c1 = item1.complexity.get(cov1);
    			for (BigInteger cov2: item2.coverage.keySet()) {
        			Set<InnerWay> ways2 = item2.coverage.get(cov2);
        			//int c3 = item1.complexity.get(cov1);
    				Set<InnerWay> tmp = new HashSet<>();
    				tmp.addAll(ways1);
    				tmp.addAll(ways2);
    				/*for (InnerWay iw: ways2) {
    					if (tmp.add(iw)) {
    						c3 += iw.getComplexity();
    					}
    				}*/
    				addWays(tmp);
    			}
    		}
    	}
    	
    	@Override
    	public String toString() {
    		String res = "";
    		for( BigInteger cov: coverage.keySet()) {
    			res += "coverage: " + cov + "\n";
    			for (InnerWay iw: coverage.get(cov)) {
    				res += iw + " and ";
    			}
    			res += "\ncomplexity = " + complexity.get(cov);
    			res += "\nor";
    		}
    		return res;
    	}
    	
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        initData();
        processData();
        //outputData();
        long end = System.currentTimeMillis();
        System.out.println("time: " + (end - start));
    }

    private static void initData() {
        way = new Stack<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(INPUT_FILE_NAME));
        }
        catch (Exception e) {}
        n = scanner.nextInt();
        k = scanner.nextInt();
        innerWays = new InnerWay[k];
        matrix = new int[n + 1][n + 1];
        matrixCopy = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = i; j <= n; j++) {
                matrix[i][j] = matrix[j][i] = matrixCopy[i][j] = matrixCopy[j][i] = -1;
            }
        }
        junctionsNum = 3 * n / 2;
        int start, end, hard;
        starts = new int[junctionsNum + 1];
        ends = new int[junctionsNum + 1];
        for (int i = 0; i < junctionsNum; i++) {
            start = scanner.nextInt();
            end = scanner.nextInt();
            hard = scanner.nextInt();
            starts[i] = start;
            ends[i] = end;
            matrix[start][end] = matrix[end][start] = matrixCopy[start][end] = matrixCopy[end][start] = hard;
        }
        coverage = new int[k][n - k];
        res = new Item[n - k][n - k];
    }

    private static void outputData() {
		/*System.out.println("way:");
		if (way == null) {
			System.out.println("way null");
			return;
		}*/
        way.pop();
        int last = way.pop();
        StringBuffer sb = new StringBuffer();
        for (Integer w: way) {
            sb.append(w + " ");
            //System.out.print(w + " ");
        }
        sb.append(last + "");
        //System.out.println("");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));
            writer.append(sb.toString());
            writer.close();
        }
        catch (IOException e) {}

    }

    private static void processData() {
        for (int i = 0; i < n - k; i++) {
        	for (int j = i; j < n - k; j++) {
            	res[i][j] = new Item();        		
        	}
        }

        for (int i = 0; i < junctionsNum; i++) {
            if (starts[i] <= k && ends[i] <= k && starts[i] != ends[i]) {
                Stack<Integer> innerGrots = findInnerWayFromOuterToOuter(starts[i], ends[i]);
                for (Integer grot: innerGrots) {
                	coverage[innerWaysLength][grot - k - 1] = 1;
                }
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < n - k; j++) {
                	sb.append(coverage[innerWaysLength][j] + "");
                }
                InnerWay tmp = new InnerWay(starts[i], ends[i], innerGrots, sb.toString());
                innerWays[innerWaysLength] = tmp;
                innerWaysLength++;
            }
        }
        
        func(0, n - k - 1);
        
        /*for (int i = 0; i < n - k; i++) {
        	for (int j = 0; j < k; j++) {
        		if (coverage[j][i] > 0) {
        			Set<InnerWay> set = new HashSet<>();
        			set.add(innerWays[j]);
        			res[i][i].addWays(set);
        		}
        	}
        	//System.out.println(i + k + 1 + ": " + res[i][i]);
        }*/
        
        /*for (int l = 1; l < n - k; l++) {
            for (int i = 0; i < n - k - l; i++) {
            	res[i][i + l] = new Item();
            	//res[i][i + l].mergeWays(res[i][i + l - 1], res[i + 1][i + l]);
            	//System.out.println((i + k + 1) + ", " + (i + k + 1 + l) + ": " + res[i][i + l]);
            }
        }*/
        System.out.println("res: " + res[0][n - k - 1]);
        
        /*for (int i = 0; i < n - k - 1; i++) {
        	res[i][i + 1].mergeWays(res[i][i], res[i + 1][i + 1]);
        	//System.out.println((i + k + 1) + ", " + (i + k + 2) + ": " + res[i][i + 1]);
        }*/

    }

    private static void func(int start, int end) {
    	if (start == end) {
        	for (int j = 0; j < k; j++) {
        		if (coverage[j][start] > 0) {
        			Set<InnerWay> set = new HashSet<>();
        			set.add(innerWays[j]);
        			res[start][start].addWays(set);
        		}
        	}    		
        	return;
    	}

    	int m = (start + end) / 2;
    	func(start, m);
    	func(m + 1, end);

    	res[start][end].mergeWays(res[start][m], res[m + 1][end]);    	
    }
    
    private static void set(int start, int end) {

    	
    }
    
    private static Stack<Integer> findInnerWayFromOuterToOuter(int start, int end) {
        if (start == end) {
            return new Stack<Integer>();
        }
        Stack<Integer> innerWay = new Stack<>();
        for (int i = k + 1; i <= n; i++) {
            if (matrix[start][i] >= 0) {
                findInnerWay(i, end, innerWay);
                break;
            }
        }

        int prev = start;
        for (Integer w: innerWay) {
            matrix[prev][w] = matrix[w][prev] = matrixCopy[prev][w];
            prev = w;
        }
        innerWay.pop();
        return innerWay;
    }

    private static boolean findInnerWay(int start, int end, Stack<Integer> innerWay) {
        innerWay.push(start);
        if (matrix[start][end] >= 0) {
            innerWay.push(end);
            return true;
        }
        for (int i = k + 1; i <= n; i++) {
            if (matrix[start][i] >= 0) {
                int saved = matrix[start][i];
                matrix[start][i] = matrix[i][start] = -1;
                boolean found = findInnerWay(i, end, innerWay);
                if (found) {
                    return true;
                }
                else {
                    innerWay.pop();
                    matrix[start][i] = matrix[i][start] = saved;
                }
            }
        }
        return false;
    }
    private static void testData() {
        System.out.println("n = " + n + ", k = " + k);
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("");
        }
    }

}