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
		generateSingleRun(10, "synthetic1D", 3.0);
		// generateChartResults(150, "synthetic2D");
	}

	/**
	 * Execute a single run to compute the offline, online and greedy costs and
	 * their respective competitive ratios.
	 * 
	 * @param numNodes
	 *            Number of nodes/vertices in Set A.
	 * @param dataSource
	 *            Type of data source to be used.
	 * @param constant
	 *            Constant multiplier to be used for better competitive ratio
	 * 
	 *            <pre>
	 * "trip_data_test.csv" - Uber Data Set
	 * 
	 * "synthetic1D" - Random points on a 1D line where distance
	 * between points is cost
	 * 
	 * "synthetic2D" - Random (x,y) points on a 2D unit square space
	 * where distance formula is cost
	 * 
	 * "synthetic2DExample1" - (x,y) integer points 0 <= x,y <
	 * sqrt(numNodes*2), shuffled then divided between taxis and
	 * requests
	 * 
	 * "synthetic2DExample2" - {@link SyntheticData#generateSynthetic2DExample2(int)}
	 * </pre>
	 */
	public static void generateSingleRun(int numNodes, String dataSource,
			double constant) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		// BELLMAN
		BellmanFord bell = new BellmanFord();

		bell.setConstant(constant);
		bell.generateCostMatrix(dataSource, numNodes);

		ArrayList<Integer> destinationOrder = bell
				.permuteDestinations(numNodes);
		System.out.println("Destination Index Order: "
				+ destinationOrder.toString());

		double offlineCost = bell
				.execute(numNodes, "offline", destinationOrder);
		System.out.println("----------------------------------------------");
		double offlineCostD = bell
				.execute(numNodes, "offline-D", destinationOrder);
		double onlineCost = bell.execute(numNodes, "online", destinationOrder);
		double onlineCostD = bell.execute(numNodes, "online-D", destinationOrder);
		double onlineGreedyCost = bell.execute(numNodes, "greedy",
				destinationOrder);

		System.out.println("OFFLINE COST: " + offlineCost);
		System.out.println("ONLINE COST: " + onlineCost);
		System.out.println("OFFLINE COST-D: " + offlineCostD);
		System.out.println("ONLINE COST-D: " + onlineCostD);
		System.out.println("GREEDY COST: " + onlineGreedyCost);

		double onlineCompetetiveRatio = onlineCost / offlineCost;
		double greedyCompetetiveRatio = onlineGreedyCost / offlineCost;
		
		double onlineCompetetiveRatioD = onlineCostD / offlineCostD;
		double greedyCompetetiveRatioD = onlineGreedyCost / offlineCostD;

		System.out.println("Competitive Ratio between online and offline: "
				+ onlineCompetetiveRatio);
		System.out
				.println("Competitive Ratio between greedy online and offline: "
						+ greedyCompetetiveRatio);
		
		System.out.println("Competitive Ratio between online and offline: "
				+ onlineCompetetiveRatioD);
		System.out
				.println("Competitive Ratio between greedy online and offline: "
						+ greedyCompetetiveRatioD);

		// HUNGARIAN
		double hungarianCost = bell.execute(numNodes, "hungarian",
				destinationOrder);
		System.out.println("HUNGARIAN COST: " + hungarianCost);

		cal = Calendar.getInstance();
		System.out.println("End: " + dateFormat.format(cal.getTime()));
	}

	/**
	 * Execute multiple runs (5) for each coefficient in the "coefficients"
	 * array and store the average online and greedy competitive ratios for each
	 * coefficient.
	 * 
	 * @param numNodes
	 *            Number of nodes/vertices in Set A.
	 * @param dataSource
	 *            Type of data source to be used. (Same dataSource values as
	 *            generateSingleRun)
	 * 
	 *            <pre>
	 * output
	 * Each line represents a coefficient
	 * Column 1: online competitive ratio for a coefficient
	 * Column 2: greedy competitive ratio for a coefficient
	 * </pre>
	 */
	public static void generateChartResults(int numNodes, String dataSource) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		Double[] coefficients = { 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 5.0, 10.0,
				25.0 };

		BellmanFord bell = new BellmanFord();

		ArrayList<Integer> destinationOrder = bell
				.permuteDestinations(numNodes);
		bell.generateCostMatrix(dataSource, numNodes);

		double offlineCost = bell
				.execute(numNodes, "offline", destinationOrder);
		double onlineGreedyCost = bell.execute(numNodes, "greedy",
				destinationOrder);

		for (int i = 0; i < coefficients.length; i++) {
			bell.setConstant(coefficients[i]);
			double competetiveRatioOnline = 0;
			double competetiveRatioGreedy = 0;

			for (int j = 0; j < 5; j++) {
				double onlineCost = bell.execute(numNodes, "online",
						destinationOrder);

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
