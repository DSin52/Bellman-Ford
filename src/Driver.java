import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Driver to execute a single run to compute the offline, online and greedy
 * costs and their respective competitive ratios.
 * 
 * Can also execute multiple runs that prints out an average competitive ratio
 * of online/offline and greedy/offline after running on different coefficients.
 * 
 * @author Sanchit Chadha
 * @author Divit Singh
 */
public class Driver {

	public static void main(String[] args) {
		generateSingleRun(150, "synthetic2D");
		// generateChartResults(150, "synthetic2D");
	}

	/**
	 * Execute a single run to compute the offline, online and greedy costs and
	 * their respective competitive ratios.
	 * 
	 * @param numNodes
	 *            Number of nodes/vertices in Set A.
	 * @param filename
	 *            Name of dataset file. Uncomment line 20 to use synthetic
	 *            dataset.
	 */
	public static void generateSingleRun(int numNodes, String dataSource) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		// BELLMAN
		BellmanFord bell = new BellmanFord();
		bell.setConstant(3.0);
		bell.generateCostMatrix(dataSource, numNodes);

		ArrayList<Integer> destinationOrder = bell.permuteDestinations(numNodes);
		System.out.println("Destination Index Order: " + destinationOrder.toString());

		double offlineCost = bell.execute(numNodes, "offline", destinationOrder);
		double onlineCost = bell.execute(numNodes, "online", destinationOrder);
		double onlineGreedyCost = bell.execute(numNodes, "greedy", destinationOrder);

		System.out.println("OFFLINE COST: " + offlineCost);
		System.out.println("ONLINE COST: " + onlineCost);
		System.out.println("GREEDY COST: " + onlineGreedyCost);

		double onlineCompetetiveRatio = onlineCost / offlineCost;
		double greedyCompetetiveRatio = onlineGreedyCost / offlineCost;

		System.out.println("Competitive Ratio between online and offline: "
				+ onlineCompetetiveRatio);
		System.out.println("Competitive Ratio between greedy online and offline: "
				+ greedyCompetetiveRatio);

		// HUNGARIAN
		// bell.execute(numNodes, "hungarian", destinationOrder);

		cal = Calendar.getInstance();
		System.out.println("End: " + dateFormat.format(cal.getTime()));
	}

	public static void generateChartResults(int numNodes, String dataSource) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		Double[] coefficients = { 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 5.0, 10.0, 25.0 };

		BellmanFord bell = new BellmanFord();

		ArrayList<Integer> destinationOrder = bell.permuteDestinations(numNodes);
		bell.generateCostMatrix(dataSource, numNodes);

		double offlineCost = bell.execute(numNodes, "offline", destinationOrder);
		double onlineGreedyCost = bell.execute(numNodes, "greedy", destinationOrder);

		for (int i = 0; i < coefficients.length; i++) {
			bell.setConstant(coefficients[i]);
			double competetiveRatioOnline = 0;
			double competetiveRatioGreedy = 0;

			for (int j = 0; j < 5; j++) {
				double onlineCost = bell.execute(numNodes, "online", destinationOrder);

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
}
