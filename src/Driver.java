import java.util.ArrayList;

/**
 * Driver to run Bellman Ford or Hungarian algorithms.
 */
public class Driver {

	/**
	 * Computes the smallest cost matching of a bipartite graph using the
	 * Bellman Ford and Hungarian algorithms.
	 * 
	 * @param numNodes
	 *            Number of nodes/vertices in Set A.
	 * @param filename
	 *            Name of dataset file. Uncomment line 20 to use synthetic
	 *            dataset.
	 */
	public static void main(String[] args) {
		int numNodes = 350;
		String filename = "trip_data_test.csv";
//		filename = "synthetic2D";
		// filename = "example";

		// DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		// Calendar cal = Calendar.getInstance();
		// System.out.println("Start: " + dateFormat.format(cal.getTime()));

		// System.out.println("BELLMAN");
		BellmanFord bell = new BellmanFord();
		ArrayList<Integer> destinationOrder = bell.permuateDestinations(
				filename, numNodes);

		double offlineCost = bell.execute(filename, numNodes, "offline",
				destinationOrder);
		double onlineCost = bell.execute(filename, numNodes, "online",
				destinationOrder);
		double onlineGreedyCost = bell.execute(filename, numNodes, "greedy",
				destinationOrder);
		double competetiveRatio = onlineCost / offlineCost;
		double competetiveRatio1 = onlineGreedyCost / offlineCost;
		System.out.println("Competitive Ratio between online and offline: "
				+ competetiveRatio);
		System.out
				.println("Competitive Ratio between greedy online and offline: "
						+ competetiveRatio1);

		// System.out.println("\nHUNGARIAN");
		// bell.execute(filename, numNodes, "hungarian");

		// cal = Calendar.getInstance();
		// System.out.println("End: " + dateFormat.format(cal.getTime()));
	}
}
