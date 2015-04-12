
public class Driver {

	public static void main(String[] args) {

		BellmanFord bell = new BellmanFord();
		bell.computeOfflineMatching("trip_data_test.csv", 2);
	}

}
