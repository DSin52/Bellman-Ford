
public class Driver {

	public static void main(String[] args) {

		BellmanFord bell = new BellmanFord();
		int numNodes = 10;
		System.out.println("BELLMAN");
		bell.computeOfflineMatching("trip_data_test.csv", numNodes);
		System.out.println();
		System.out.println("HUNGARIAN");
		bell.verifyHungarian("trip_data_test.csv", numNodes);
	}
}
