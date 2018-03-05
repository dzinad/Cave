import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

class InnerWay {
	private Stack<Integer> innerGrots;
	private int start, end;
	private int complexity;
	
	public InnerWay(int start, int end, Stack<Integer> innerGrots) {
		this.innerGrots = innerGrots;
		this.start = start;
		this.end = end;
		countComplexity();
	}
	
	private void countComplexity() {
		complexity = 0;
        int prev = start;
        for (Integer w: innerGrots) {
        	//coverage[key1][w] = coverage[key2][w] = true;
        	complexity += Main.matrix[prev][w];
        	prev = w;
        }
        complexity += Main.matrix[prev][end];
	}
	
	public Stack<Integer> getInnerGrots() {
		return innerGrots;
	}
	
	public int getComplexity() {
		return complexity;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
}

public class Main {

    private static final String INPUT_FILE_NAME = "input.in";
    private static final String OUTPUT_FILE_NAME = "output.out";
    private static int n, k, junctionsNum;
    private static Stack<Integer> way;
    private static Stack<Integer> innerWay = new Stack<>();
    public static int[][] matrix;
    private static int[][] matrixCopy;
    private static int[] starts, ends;
    //private static HashMap<Integer, InnerWay> innerWays;
    private static InnerWay[] innerWays;
    private static int innerWaysLength = 0;
    private static int outerRingComplexity = 0;

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
        matrix = new int[n + 1][n + 1];
        matrixCopy = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = i; j <= n; j++) {
                matrix[i][j] = matrix[j][i] = matrixCopy[i][j] = matrixCopy[j][i] = -1;
            }
        }
        junctionsNum = 3 * n / 2;
        int start, end, hard;
        //innerWays = new HashMap<>();
        innerWays = new InnerWay[k];
        starts = new int[junctionsNum + 1];
        ends = new int[junctionsNum + 1];
        for (int i = 0; i < junctionsNum; i++) {
            start = scanner.nextInt();
            end = scanner.nextInt();
            hard = scanner.nextInt();
            starts[i] = start;
            ends[i] = end;
            matrix[start][end] = matrix[end][start] = matrixCopy[start][end] = matrixCopy[end][start] = hard;
            if (start <= k && end <= k) {
            	outerRingComplexity += hard;
            }
        }
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
        for (int i = 0; i < junctionsNum; i++) {
            if (starts[i] <= k && ends[i] <= k && starts[i] != ends[i]) {
                Stack<Integer> innerWay = findInnerWayFromOuterToOuter(starts[i], ends[i]); 
                InnerWay tmp = new InnerWay(starts[i], ends[i], innerWay);
                //int key1 = starts[i] * k + ends[i];
                //int key2 = ends[i] * k + starts[i];
                innerWays[innerWaysLength] = tmp;
                innerWaysLength++;
                //innerWays.put(key1, tmp);
                //innerWays.put(key2, tmp);
            }
        }
        
        HashSet<HashSet<InnerWay>> coverage = findCoverages(k + 1);
        System.out.println("coverages: ");
        for (HashSet<InnerWay> conj: coverage) {
        	for (InnerWay iw: conj) System.out.print(iw.getStart() + "-" + iw.getEnd() + " and ");
        	System.out.println("\nor");
        }
        
        /*boolean[] used = new boolean[n + 1];
        List<InnerWay> usedInnerWays = findCoverageSets(0, used);
        System.out.println("used inner ways: ");
        for (InnerWay iw: usedInnerWays) {
        	System.out.println(iw.getStart() + " - " + iw.getEnd());
        }*/
        /*for (InnerWay iw: innerWays) {
        	System.out.println(iw.getInnerGrots());
        }*/
    }


    private static HashSet<HashSet<InnerWay>> findCoverages(int startGrot) {
    	HashSet<HashSet<InnerWay>> res = new HashSet<>();
    	if (startGrot == n) {
    		for (InnerWay iw: innerWays) {
    			if (iw.getInnerGrots().contains(startGrot)) {
    				HashSet<InnerWay> tmp = new HashSet<>();
    				tmp.add(iw);
    				res.add(tmp);
    			}
    		}
    		return res;
    	}

    	for (InnerWay iw: innerWays) {
    		if (iw.getInnerGrots().contains(startGrot)) {
    			HashSet<HashSet<InnerWay>> tmp = findCoverages(startGrot + 1);
    			for (HashSet<InnerWay> conj: tmp) {
    				HashSet<InnerWay> newConj = new HashSet<>();
    				newConj.addAll(conj);
    				Stack<Integer> coveredGrots = new Stack<>();
    				boolean shouldAdd = true;
    				for (InnerWay oneWay: conj) {
    					if (oneWay.equals(iw)) {
    						continue;
    					}
    					for (Integer grot: iw.getInnerGrots()) {
        					if (oneWay.getInnerGrots().contains(grot)) {
        						shouldAdd = false;
        						break;
        					}
    					}
    					if (!shouldAdd) {
    						break;
    					}
    				}
    				if (!shouldAdd) {
    					continue;
    				}
    				newConj.add(iw);
    				List<HashSet<InnerWay>> toRemove = new ArrayList<>();
        			for (HashSet<InnerWay> prevConj: res) {
        				if (newConj.containsAll(prevConj)) {
        					shouldAdd = false;
        					break;
        				}
        				if (prevConj.containsAll(newConj)) {
        					toRemove.add(prevConj);
        				}
        			}
        			if (!shouldAdd) {
        				continue;
        			}
        			for (HashSet<InnerWay> tr: toRemove) {
        				res.remove(tr);
        			}
    				/*Stack<Integer> coveredGrots = new Stack<>();
        			for (HashSet<InnerWay> prevConj: res) {
        				for (InnerWay oneWay: prevConj) {
            				coveredGrots.addAll(oneWay.getInnerGrots());            				
        				}
        			}
        			
        			for (InnerWay oneWay: newConj) {
        				for (Integer grot: oneWay.getInnerGrots()) {
        					if (coveredGrots.contains(grot)) {
        						shouldAdd = false;
        						break;
        					}
        				}
        				if (!shouldAdd) {
        					break;
        				}
        			}
        			if (!shouldAdd) {
        				continue;
        			}*/
        			res.add(newConj);        				
    			}
    		}
    	}
    	return res;
    }
    
    private static List<InnerWay> findCoverageSets(int startIndex, boolean[] used) {
    	InnerWay curWay = innerWays[startIndex];
    	System.out.print("curway: " + curWay.getInnerGrots() + ", used: ");
    	for (boolean b: used) System.out.print(b + " ");
    	System.out.println("");
    	
    	List<InnerWay> ways = new ArrayList<>();
    	Stack<Integer> coverage = curWay.getInnerGrots();
    	ways.add(curWay);
    	if (startIndex == k - 1) {
    		boolean ifCovered = true;
    		for (int i = k + 1; i <= n; i++) {
    			if (used[i] == false) {
    				ifCovered = false;
    				break;
    			}
    		}
        	for (Integer grot: coverage) {
        		used[grot] = false;
        	}
    		if (!ifCovered) {
    			return null;
    		}
        	return ways;    		
    	}

    	boolean isWayOK = true;
    	int index = -1;
    	for (Integer grot: coverage) {
    		if (used[grot]) {
    			index = grot;
    			isWayOK = false;
    			break;
    		}
    		used[grot] = true;
    	}
    	if (!isWayOK) {
        	for (Integer grot: coverage) {
        		if (grot == index) {
        			used[grot] = true;
        			break;
        		}
        		used[grot] = false;
        	}
    		return null;
    	}
    	
    	int minComplexity = -1;
    	for (int i = startIndex + 1; i < k; i++) {
    		int complexity = outerRingComplexity + curWay.getComplexity() - matrix[curWay.getStart()][curWay.getEnd()];
        	List<InnerWay> nextWays = findCoverageSets(i, used);
        	if (nextWays == null) {        		
        		continue;
        	}
        	for (InnerWay iw: nextWays) {
        		complexity += (iw.getComplexity() - matrix[iw.getStart()][iw.getEnd()]);
        	}
        	if (complexity < minComplexity || minComplexity == -1) {
        		minComplexity = complexity;
        		ways.clear();
        		ways.add(curWay);
        		ways.addAll(nextWays);
        	}
    	}
    	for (Integer grot: coverage) {
    		used[grot] = false;
    	}
    	if (minComplexity == -1) {
    		return null;
    	}
    	return ways;
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