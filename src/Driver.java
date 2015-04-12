import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Driver {

	public static void main(String[] args) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		BellmanFord bell = new BellmanFord();
		int numNodes = 300;
		String filename = "trip_data_1.csv";
		filename = "synthetic";
		System.out.println("BELLMAN");
		bell.computeOfflineMatching(filename, numNodes);
		System.out.println();
		System.out.println("HUNGARIAN");
		bell.verifyHungarian(filename, numNodes);

		
		cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));
	}
}
