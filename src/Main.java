import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Main {

    private static final String INPUT_FILE_NAME = "input.in";
    private static final String OUTPUT_FILE_NAME = "output.out";
    private static int n, k, junctionsNum;
    private static List<Integer> way;
    private static Stack<Integer> innerWay = new Stack<>();
    private static int[][] matrix, matrixCopy;
    private static int[] starts, ends;
    private static boolean ifEnter = true;
    //private static HashMap<Integer, Stack<Integer>> innerWays;
    private static int[][] coverage;
    private static int[][] coverageCopy;
    private static boolean[][] compat;
    private static InnerWay[] innerWays;
    private static int innerWaysLength = 0;
    private static Item[] arr;
    private static HashSet<Integer> best;
    
    static class Item {
    	private HashSet<HashSet<Integer>> data;
    	
    	public Item() {
    		data = new HashSet<>();
    	}
    	
    	public Item(HashSet<HashSet<Integer>> hs) {
    		data = hs;
    	}
    	
    	public HashSet<HashSet<Integer>> getData() {
    		return data;
    	}
    	
    	public void addWay(Integer w) {
    		HashSet<Integer> tmp = new HashSet<>();
    		tmp.add(w);
    		data.add(tmp);
    	}
    	
    	public HashSet<Integer> getBestOption() {
    		int minComplexity = -1;
    		HashSet<Integer> res = null;
    		for (HashSet<Integer> hs: data) {
    			int complexity = 0;
    			for (Integer i: hs) {
    				complexity += innerWays[i].getComplexity() - matrix[innerWays[i].getStart()][innerWays[i].getEnd()];
    			}
    			if (complexity < minComplexity || minComplexity == -1) {
    				minComplexity = complexity;
    				res = hs;
    			}
    		}
    		return res;
    	}
    	
    	@Override
    	public String toString() {
    		String res = "";
    		for (HashSet<Integer> hs: data) {
    			for (Integer i: hs) {
    				res += innerWays[i] + " and ";
    			}
    			res += "\nor\n";
    		}
    		return res;
    	}
    }
    
    static class InnerWay {
    	private int start, end;
    	private Stack<Integer> innerGrots;
    	private int complexity = 0;
    	
    	public InnerWay(int start, int end, Stack<Integer> innerGrots) {
    		this.start = start;
    		this.end = end;
    		this.innerGrots = innerGrots;
    		setComplexity();
    	}
    	
    	private void setComplexity() {
    		int prev = start;
    		for (Integer grot: innerGrots) {
    			complexity += matrix[prev][grot];
    			prev = grot;
    		}
    		complexity += matrix[prev][end];
    	}
    	
    	public int getStart() {
    		return start;
    	}
    	
    	public int getEnd() {
    		return end;
    	}
    	
    	public int getComplexity() {
    		return complexity;
    	}
    	
    	public Stack<Integer> getInnerGrots() {
    		return innerGrots;
    	}
    	
    	@Override
    	public String toString() {
    		return start + " - " + end;
    	}
    }
    
    public static void main(String[] args) {
        //long start = System.currentTimeMillis();
        initData();
        processData();
        outputData();
        //long end = System.currentTimeMillis();
        //System.out.println("time: " + (end - start));
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
        compat = new boolean[k][k];
        for (int i = 0; i < k; i++) {
        	for (int j = i; j < k; j++) {
        		compat[i][j] = compat[j][i] = true; 
        	}
        }
        arr = new Item[n - k];
        for (int i = 0; i < n - k; i++) {
        	arr[i] = new Item();
        }
        way = new ArrayList<>();
    }

    private static void outputData() {
    	int index = 0;
        StringBuffer sb = new StringBuffer();
        for (Integer w: way) {
        	if (index == way.size() - 1) {
        		break;
        	}
            sb.append(w + " ");
            index++;
        }
        sb.append(way.get(index) + "");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));
            writer.append(sb.toString());
            writer.close();
        }
        catch (IOException e) {}
    }

    private static void processData() {

        for (int i = 0; i < junctionsNum; i++) {
            if (starts[i] <= k && ends[i] <= k && starts[i] != ends[i]) {
                Stack<Integer> innerGrots = findInnerWayFromOuterToOuter(starts[i], ends[i]);
                InnerWay tmp = new InnerWay(starts[i], ends[i], innerGrots);
                innerWays[innerWaysLength] = tmp;
                for (Integer grot: innerGrots) {
                	arr[grot - k - 1].addWay(innerWaysLength);
                }
                innerWaysLength++;
            }
        }
        
        for (int i = 0; i < k; i++) {
        	for (int j = i + 1; j < k; j++) {
        		for (Integer g: innerWays[j].getInnerGrots()) {
        			if (innerWays[i].getInnerGrots().contains(g)) {
        				compat[i][j] = compat[j][i] = false;
        				break;
        			}
        		}
        	}
        		
        }
        
        Item res = f(0, n - k - 1);
        best = res.getBestOption();
        findFinalWay(1);
        /*System.out.println("way:");
        for (Integer w: way) {
        	System.out.print(w + " ");
        }*/
    }
    
    private static void findFinalWay(int start) {
    	for (int i = 1; i <= k; i++) {
    		if (matrix[start][i] >= 0) {
    			matrix[start][i] = matrix[i][start] = -1;    			
    			way.add(start);
    			
    			for (Integer b: best) {
    				if (innerWays[b].getStart() == start && innerWays[b].getEnd() == i) {
    					Stack<Integer> tmp = innerWays[b].getInnerGrots(); 
    					for (Integer grot: tmp) {
    						way.add(grot);
    					}
    				}
    				else if (innerWays[b].getEnd() == start && innerWays[b].getStart() == i) {
    					Stack<Integer> tmp = innerWays[b].getInnerGrots(); 
    					while (!tmp.empty()) {
    						way.add(tmp.pop());
    					}
    				}
    			}
    			
    			findFinalWay(i);
    		}
    	}
    }
    
    private static Item f(int start, int end) {
    	if (start == end) {
    		return arr[start];
    	}
    	
    	int m = (start + end) / 2;
    	return merge(f(start, m), f(m + 1, end));
    }
    
    private static Item merge(Item item1, Item item2) {
    	HashSet<HashSet<Integer>> hs1 = item1.getData();
    	HashSet<HashSet<Integer>> hs2 = item2.getData();
    	HashSet<HashSet<Integer>> res = new HashSet<>();
    	for (HashSet<Integer> conj1: hs1) {
    		for (HashSet<Integer> conj2: hs2) {
    			boolean canMerge = true;
    			for (Integer iw1: conj1) {
    				for (Integer iw2: conj2) {
    					if (!compat[iw1][iw2]) {
    						canMerge=  false;
    						break;
    					}
    				}
    				if (!canMerge) {
    					break;
    				}
    			}
    			if (!canMerge) {
    				continue;
    			}
    			HashSet<Integer> tmp = new HashSet<>();
    			tmp.addAll(conj1);
    			tmp.addAll(conj2);
    			res.add(tmp);
    		}
    	}
    	return new Item(res);
    }
    
    private static void print(int[][] m) {
    	System.out.println("====================");
    	for (int i = 0; i < m.length; i++) {
    		for (int j = 0; j < m[0].length; j++) {
    			System.out.print(m[i][j] + " ");
    		}
    		System.out.println("");
    	}
    	System.out.println("====================");
    }
    
    private static int process(int[][] cov) {
    	//print(cov);
    	if (cov.length == 0) {
    		//print(cov);
    		//System.out.println("ret = 0");
    		return 0;
    	}
    	if (cov.length == 1) {
    		int min = -1;
    		for (int i = 0; i < cov[0].length; i++) {
    			if (cov[0][i] >= 0 && (cov[0][i] < min || min == -1)) {
    				min = cov[0][i];
    			}
    		}
    		//print(cov);
    		//System.out.println("ret = " + min);
    		return min;
    	}
    	int[][] newCov;
    	Set<Integer> columns = new HashSet<>();
    	List<Integer> cs = new ArrayList<>();
    	for (int i = 0; i < cov[0].length; i++) {
    		if (cov[0][i] >= 0) {
    			columns.add(i);
    		}
    	}
    	
    	for (Integer col: columns) {
    		List<Integer> rows = new ArrayList<>();
    		Set<Integer> cols = new HashSet<>();
    		cols.addAll(columns);
    		for (int i = 0; i < cov.length; i++) {
    			if (cov[i][col] >= 0) {
    				rows.add(i);
    			}
    		}
    		for (Integer row: rows) {
    			for (int i = 0; i < cov[0].length; i++) {
    				if (cov[row][i] >= 0) {
    					cols.add(i);
    				}
    			}
    		}
    		//print(cov);
    		//System.out.println("col: " + col + ", -rows: " + rows.size());
    		int d1 = cov.length - rows.size();
    		int d2 = cov[0].length - cols.size();
    		int l1 = 0, l2 = 0;
    		newCov = new int[d1][d2];
    		for (int i = 0; i < cov.length; i++) {
    			l2 = 0;
    			if (rows.contains(i)) {
    				continue;
    			}
    			for (int j = 0; j < cov[0].length; j++) {
    				if (cols.contains(j)) {
    					continue;
    				}
    				newCov[l1][l2] = cov[i][j];
    				l2++;
    			}
    			l1++;
    		}
    		int tmp = process(newCov);
    		if (tmp != -1) {
        		cs.add(cov[rows.get(0)][col] + process(newCov));    			
    		}
    	}
    	int ret = (cs.size() == 0) ? -1 : Collections.min(cs);
    	//print(cov);
    	//System.out.println("ret = " + ret);
    	return ret;
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