import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Main {

	private static final String INPUT_FILE_NAME = "input.in";
	private static final String OUTPUT_FILE_NAME = "output.out";
	private static int n, k, junctionsNum;
	private static Stack<Integer> way;
	private static Stack<Integer> innerWay = new Stack<>();
	private static int[][] matrix, matrixCopy;
	private static boolean ifEnter = true;
	private static HashMap<Integer, Stack<Integer>> innerWays;
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		/*initData();
		processData();
		outputData();*/
		Random rand = new Random();
		int tmp;
		for (int i = 1; i <= 2500 * 500; i++) tmp = i * 10;
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("time: " + time);
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
        for (int i = 1; i <= n; i++) {
        	for (int j = 1; j <= n; j++) {
        		matrix[i][j] = -1;
        	}
        }
        junctionsNum = 3 * n / 2;
        int start, end, hard;
        for (int i = 0; i < junctionsNum; i++) {
        	start = scanner.nextInt();
        	end = scanner.nextInt();
        	hard = scanner.nextInt();
        	matrix[start][end] = matrix[end][start] = hard;
        }
        innerWays = new HashMap<>();
        for (int i = 1; i <= k; i++) {
        	for (int j = i + 1; j <= k; j++) {
        		if (matrix[i][j] == -1) {
        			continue;
        		}
        		Stack<Integer> innerWay = findInnerWayFromOuterToOuter(i, j); 
        		innerWays.put(i * k + j, innerWay);
        		innerWays.put(j * k + i, innerWay);
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
		int end = 1;
		for (int i = 2; i <= k; i++) {
			if (matrix[1][i] >= 0) {
				end = i;
				break;
			}
		}	
		int[] used = new int[n + 1];
		for (int i = k + 1; i <= n; i++) {
			used[i] = 0;
		}

		way = find(end, 1, 1, n + 1, used);
	}
	
	private static Stack<Integer> find(int from, int start, int end, int num, int[] used) {
		//System.out.println("Start: " + start + ", end: " + end + ", num = " + num);
		int[] used1 = new int[n + 1];
		for (int i = k + 1; i <= n; i++) {
			used1[i] = used[i];
		}
		if (start == end && (start != 1 || !ifEnter)) {
			if (num == 1) {
				Stack<Integer> ret = new Stack<>();
				ret.push(start);
				return ret;				
			}
			else {
				return null;
			}
		}
		
		if (ifEnter) {
			ifEnter = false;
		}
		int nstart = -1;
		for (int i = 1; i <= k; i++) {
			if (matrix[start][i] >= 0 && i != from) {
				nstart = i;
				break;
			}
		}
		Stack<Integer> way1 = null;
		if (nstart != -1) {
			Stack<Integer> tmp = find(start, nstart, end, num - 1, used1);
			if (tmp != null) {
				way1 = new Stack<>();
				way1.push(start);
				way1.addAll(tmp);
			}
		}
		
		Stack<Integer> innerWay = new Stack<>();
		innerWay.addAll((innerWays.get(start * k + nstart)));
		int prev = start;
		boolean isWayOK = true;
		for (Integer grot: innerWay) {
			if (grot > k && used[grot] == 1) {
				isWayOK = false;
				break;
			}
		}
		if (isWayOK) {
			for (Integer grot: innerWay) {
				if (grot > k) {
					used[grot] = 1;
				}
			}			
		}
		
		int[] used2 = new int[n + 1];
		for (int i = k + 1; i <= n; i++) {
			used2[i] = used[i];
		}
		Stack<Integer> way2 = null;
		if (isWayOK) {
			Stack<Integer> tmp = find(start, nstart, end, num - innerWay.size() - 1, used2);
			if (tmp != null) {
				way2 = new Stack<>();
				way2.push(start);
				
				if (matrix[start][innerWay.peek()] < 0) {
					way2.addAll(innerWay);					
				}
				else {
					while(!innerWay.empty()) {
						way2.push(innerWay.pop());
					}
				}
				
				way2.addAll(tmp);
			}
		}
		
		//System.out.println("Start: " + start + ", end: " + end + ", num = " + num);
		//System.out.println("way1: " + way1 + "\nway2: " + way2);
		if (way2 == null && way1 == null) {
			return null;
		}
		if (way1 == null) {
			if (way2.size() == num) {
				return way2;
			}
			else {
				return null;
			}
		}
		else if (way2 == null) {
			if (way1.size() == num) {
				return way1;
			}
			else {
				return null;
			}
		}
		else {
			if (way1.size() != num && way2.size() != num) {
				return null;
			}
			else if (way1.size() != num) {
				return way2;
			}
			else if (way2.size() != num) {
				return way1;
			}
			else {
				int c1 = 0;
				int c2 = 0;
				for (Integer i1: way1) {
					if (i1 != start) {
						c1 += matrix[prev][i1];						
					}
					prev = i1;
				}
				for (Integer i2: way2) {
					if (i2 != start) {
						c2 += matrix[prev][i2];						
					}
					prev = i2;
				}
				//System.out.println("c1: " + c1 + "\nc2: " + c2);
				if (c1 < c2) {
					return way1;
				}
				else {
					return way2;
				}

			}
		}		
	}
	
	private static Stack<Integer> findInnerWayFromOuterToOuter(int start, int end) {
		if (start == end) {
			return new Stack<Integer>();
		}
		matrixCopy = new int[n + 1][n + 1];
		for (int i = 1; i <= n; i++) {
			for (int j = i; j <= n; j++) {
				matrixCopy[i][j] = matrixCopy[j][i] = matrix[i][j];
			}
		}
		Stack<Integer> innerWay = new Stack<>();
		for (int i = k + 1; i <= n; i++) {
			if (matrixCopy[start][i] >= 0) {
				findInnerWay(i, end, innerWay);
				break;
			}
		}
		innerWay.pop();
		return innerWay;
	}
	
	private static boolean findInnerWay(int start, int end, Stack<Integer> innerWay) {
		innerWay.push(start);
		if (matrixCopy[start][end] >= 0) {
			innerWay.push(end);
			return true;
		}
		for (int i = k + 1; i <= n; i++) {
			if (matrixCopy[start][i] >= 0) {
				int saved = matrixCopy[start][i];
				matrixCopy[start][i] = matrixCopy[i][start] = -1;
				boolean found = findInnerWay(i, end, innerWay);
				if (found) {
					return true;
				}
				else {
					innerWay.pop();
					matrixCopy[start][i] = matrixCopy[i][start] = saved;
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
