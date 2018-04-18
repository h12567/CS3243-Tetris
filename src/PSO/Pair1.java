public class Pair1 {
	public int first;
	public double second;

	Pair1(int a, double b) {
		this.first = a;
		this.second = b;
	}

	public static void main(String[] args) {
		Pair1 a = new Pair1(1, 2.0);
		a.first = 10;
		System.out.println(a.first);
	}
}