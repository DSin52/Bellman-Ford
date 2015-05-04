import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
		runTests();
//		int numNodes = 150;
//		String filename = "trip_data_test.csv";
//		filename = "synthetic2DExample2";
////		filename = "example";
//
//		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//		Calendar cal = Calendar.getInstance();
//		System.out.println("Start: " + dateFormat.format(cal.getTime()));
//
//		// System.out.println("BELLMAN");
//		BellmanFord bell = new BellmanFord();
//		bell.setConstant(3.0);
//		bell.generateCostMatrix(filename, numNodes);
//		
//		ArrayList<Integer> destinationOrder = bell.permuteDestinations(filename, numNodes);
//		System.out.println("Destination Index Order: " + destinationOrder.toString());
//		
//		double offlineCost = bell.execute(filename, numNodes, "offline", destinationOrder);
//		double onlineCost = bell.execute(filename, numNodes, "online", destinationOrder);
//		double onlineGreedyCost = bell.execute(filename, numNodes, "greedy", destinationOrder);
//		System.out.println("OFFLINE COST: " + offlineCost);
//		System.out.println("ONLINE COST: " + onlineCost);
//		System.out.println("GREEDY COST: " + onlineGreedyCost);
//		double competetiveRatio = onlineCost / offlineCost;
//		double competetiveRatio1 = onlineGreedyCost / offlineCost;
//		System.out.println("Competitive Ratio between online and offline: " + competetiveRatio);
//		System.out.println("Competitive Ratio between greedy online and offline: " + competetiveRatio1);
//
//		// System.out.println("\nHUNGARIAN");
//		// bell.execute(filename, numNodes, "hungarian");
//
//		cal = Calendar.getInstance();
//		System.out.println("End: " + dateFormat.format(cal.getTime()));
	}

	public static void runTests() {
		int numNodes = 150;
		String filename = "trip_data_test.csv";
		filename = "synthetic2DExample2";
		// filename = "example";
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		Double[] coefficients = { 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 5.0, 10.0, 25.0 };
		BellmanFord bell = new BellmanFord();
		ArrayList<Integer> destinationOrder = bell.permuteDestinations(filename, numNodes);
		bell.generateCostMatrix(filename, numNodes);

		double offlineCost = bell.execute(filename, numNodes, "offline", destinationOrder);

		for (int i = 0; i < coefficients.length; i++) {
			bell.setConstant(coefficients[i]);
			double competetiveRatioOnline = 0;
			double competetiveRatioGreedy = 0;

			for (int j = 0; j < 5; j++) {
				double onlineCost = bell.execute(filename, numNodes, "online", destinationOrder);
				double onlineGreedyCost = bell.execute(filename, numNodes, "greedy", destinationOrder);

				competetiveRatioOnline += onlineCost / offlineCost;
				competetiveRatioGreedy += onlineGreedyCost / offlineCost;
			}
			
			System.out.print(competetiveRatioOnline / 5 + "\t");
			System.out.print(competetiveRatioGreedy / 5 + "\t");
			System.out.println();

		}

		cal = Calendar.getInstance();
		System.out.println("End: " + dateFormat.format(cal.getTime()));
	}

	// Run on coefficients with delta of 0.1
	// Run execution for 5 different random point permutations
	// For each random point permutation, run 5 different destination order
	// permutations

	// How many times are we mapping to the nearest neighbor in online
	// algorithm?
	// Cost of offline matching when doing online version should be at most 3 *
	// optimal (pure offline matching)

	// Look at augmenting paths of length 1. All direct augmenting paths of
	// lengths 1
	// Two unmatched points, coefficient is 1.0. Everything else remains the
	// same.

	// Compute cost of online matching after a certain number of edges have been
	// processed (ie. 20/100)
}
