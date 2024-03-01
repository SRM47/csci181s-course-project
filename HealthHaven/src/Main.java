
public class Main {

	public static int fib(int n) {
		if (n <= 0) {
			return -1;
		}
		int[] fib = new int[n + 1];
		fib[0] = 1;
		fib[1] = 1;
		for (int i = 2; i <= n; i++) {
			fib[i] = fib[i - 1] + fib[i - 2];
		}
		return fib[n];
	}

	public static void main(String[] args) {
		System.out.println("This is secure!\n");
		
		int num = fib(47);
		System.out.print("This is the 47th fibbonacci number: " + num);
	}

}
