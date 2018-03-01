import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

class Grot {
	private int value = 0;
	private int neib1 = 0, neib2 = 0, neib3 = 0;
	private boolean hard1, hard2, hard3;
	
	public Grot() {
		this(0);
	}
	
	public Grot(int value) {
		this.value = value;
	}
	
	public void setNextNeibourgh(int val, boolean hard) {
		if (neib1 == 0) {
			neib1 = val;
			hard1 = hard;
		}
		else if (neib2 == 0) {
			neib2 = val;
			hard2 = hard;
		}
		else if (neib3 == 0) {
			neib3 = val;
			hard3 = hard;
		}
	}
	
	@Override
	public String toString() {
		return "value: " + value + " (" + neib1 + " " + hard1 + ", " + neib2 + " " + hard2 + ", " + neib3 + " " + hard3 + ")";
	}
	
}

class Junction {
	public int start, end;
	public boolean hard;
	
	public Junction(int start, int end, boolean hard) {
		this.start = start;
		this.end = end;
		this.hard = hard;
	}
	
	@Override
	public String toString() {
		return start + " - " + end + " (" + (hard ? "hard" : "easy") + ")";
	}
}

public class Main {

	private static final String INPUT_FILE_NAME = "input.in";
	private static final String OUTPUT_FILE_NAME = "output.out";
	private static int n, k, junctionsNum;
	private static Grot[] grots;

	public static void main(String[] args) {
		initData();
		testData();
		processData();
		outputData();
	}
	
	private static void initData() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(INPUT_FILE_NAME));
        }
        catch (Exception e) {}
        n = scanner.nextInt();
        k = scanner.nextInt();
        junctionsNum = 3 * n / 2;
        grots = new Grot[n + 1];
        int start, end;
        boolean hard;
        for (int i = 0; i < junctionsNum; i++) {
        	start = scanner.nextInt();
        	end = scanner.nextInt();
        	hard = scanner.nextInt() == 1 ? true : false;
        	processOneGrot(start, end, hard);
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
		
	}
	
	private static void testData() {
		System.out.println("n = " + n + ", k = " + k);
		System.out.println("grots:");
		for (Grot grot: grots) {
			if (grot != null) {
				System.out.println(grot);				
			}
		}
	}

}
