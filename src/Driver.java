import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		int numNodes = 10;
		String filename = "trip_data_test.csv";
		// filename = "synthetic";

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println("Start: " + dateFormat.format(cal.getTime()));

		System.out.println("BELLMAN");
		BellmanFord bell = new BellmanFord();
		bell.computeOfflineMatching(filename, numNodes);

		System.out.println("\nHUNGARIAN");
		bell.verifyHungarian(filename, numNodes);

		cal = Calendar.getInstance();
		System.out.println("End: " + dateFormat.format(cal.getTime()));
	}
}
