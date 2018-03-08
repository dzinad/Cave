import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
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
    	private HashMap<BigInteger, List<InnerWay>> coverage = new HashMap<>();
    	
    	public Item() {}
    	
    	public void addWays(List<InnerWay> w) {
    		BigInteger res = null;
    		int c = 0;
    		for (InnerWay iw: w) {
    			if (res == null) {
    				res = iw.getCoverage();
    			}
    			else {
    				res  = res.and(iw.getCoverage());
    			}
    			c += iw.getComplexity();
    		}
    		coverage.put(res, w);
    		complexity.put(res, c);
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
        
        for (int i = 0; i < n - k; i++) {
        	for (int j = 0; j < k; j++) {
        		if (coverage[j][i] > 0) {
        			List<InnerWay> list = new ArrayList<>();
        			list.add(innerWays[j]);
        			res[i][i].addWays(list);
        		}
        	}
        	System.out.println(i + k + 1 + ": " + res[i][i]);
        }

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