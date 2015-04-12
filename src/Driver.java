
public class Driver {

	public static void main(String[] args) {

		BellmanFord bell = new BellmanFord();
		int numNodes = 300;
		String filename = "trip_data_test_1.csv";
		System.out.println("BELLMAN");
		bell.computeOfflineMatching(filename, numNodes);
		System.out.println();
		System.out.println("HUNGARIAN");
		bell.verifyHungarian(filename, numNodes);
	}
}
