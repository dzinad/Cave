import java.io.File;
import java.util.Scanner;
import java.util.Stack;

class Grot {
	public int value = 0;
	public int[] neibs = new int[3];
	private int neibSize = 0;
	public boolean[] hards = new boolean[3];
	
	public Grot() {
		this(0);
	}
	
	public Grot(int value) {
		this.value = value;
	}
	
	public void setNextNeibourgh(int val, boolean hard) {
		int index = 0;
		while (index < neibSize) {
			if (val < neibs[index]) {
				for (int i = neibSize; i > index; i--) {
					neibs[i] = neibs[i - 1];
					hards[i] = hards[i - 1];
				}
				neibs[index] = val;
				hards[index] = hard;
				neibSize++;
				return;
			}
			else {
				index++;
			}
		}
		if (index == neibSize) {
			neibs[index] = val;
			hards[index] = hard;
		}
		neibSize++;
	}
	
	@Override
	public String toString() {
		return "value: " + value + " (" + neibs[0] + " " + hards[0] + ", " + neibs[1] + " " + hards[1] + ", " + neibs[2] + " " + hards[2] + ")";
	}
	
}

public class Main {

	private static final String INPUT_FILE_NAME = "input.in";
	private static final String OUTPUT_FILE_NAME = "output.out";
	private static int n, k, junctionsNum;
	private static Grot[] grots;
	private static boolean[] av; //if grot available
	private static Stack<Integer> way;
	private static Stack<Integer> innerWay = new Stack<>();
	private static int[][] matrix, matrixCopy;
	
	public static void main(String[] args) {
		initData();
		testData();
		processData();
		testData();
		outputData();
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
        grots = new Grot[n + 1];
        av = new boolean[n + 1];
        int start, end, hard;
        for (int i = 0; i < junctionsNum; i++) {
        	start = scanner.nextInt();
        	end = scanner.nextInt();
        	hard = scanner.nextInt();
        	//processOneGrot(start, end, hard);
        	matrix[start][end] = matrix[end][start] = hard;
        }
        for (int i = 1; i <= n; i++) {
        	av[i] = true;
        }
	}
	
	private static void processOneGrot(int start, int end, boolean hard) {
    	if (grots[start] == null) {
    		Grot g = new Grot(start);
    		grots[start] = g;
    	}
    	grots[start].setNextNeibourgh(end, hard);
    	if (grots[end] == null) {
    		Grot g = new Grot(end);
    		grots[end] = g;
    	}
    	grots[end].setNextNeibourgh(start, hard);		
	}
	
	private static void outputData() {
		
	}
	
	private static void processData() {
		int[] data = findInnerWayFromOuterToOuter(1, 3);
		System.out.println("weight = " + data[0] + ", length = " + data[1]);
	}
	
	private static void step(int start, int end, int num) {
	}
	
	private static int[] findInnerWayFromOuterToOuter(int start, int end) {
		matrixCopy = new int[n + 1][n + 1];
		for (int i = 1; i <=n; i++) {
			for (int j = 1; j <= n; j++) {
				matrixCopy[i][j] = matrix[i][j];
			}
		}
		int res = 0;
		Stack<Integer> innerWay = new Stack<>();
		//innerWay.push(start);
		for (int i = k + 1; i <= n; i++) {
			if (matrix[start][i] >= 0) {
				findInnerWay(i, end, innerWay);
				break;
			}
		}
		int prev = start;
		for (Integer grot: innerWay) {
			res += matrix[prev][grot];
			prev = grot;
		}
		return new int[]{res, innerWay.size() + 1};
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
		/*System.out.println("grots:");
		for (Grot grot: grots) {
			if (grot != null) {
				System.out.println(grot);				
			}
		}*/
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= n; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println("");
		}
	}

}
