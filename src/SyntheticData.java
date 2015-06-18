import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generates random synthetic 1D and 2D data and the associated cost matrix.
 */
public class SyntheticData {
	
	public static int[][] coefficientMapping;
	
	/**
	 * Generates 1D synthetic data and creates a cost matrix. Picks a random
	 * integer between 0 and 10000 and adds it to setA (no repeats). Same thing
	 * for setB. Generates the absolute value distance between each node of setA
	 * and setB and creates a cost matrix.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @return Cost matrix of the data
	 */
	public double[][] generateSynthetic1D(int numSetA) {
		
		coefficientMapping = new int[numSetA][numSetA];

		Random randSetA = new Random();
		Random randSetB = new Random();

		ArrayList<Integer> setA = new ArrayList<Integer>();
		ArrayList<Integer> setB = new ArrayList<Integer>();

		for (int i = 0; i < numSetA; i++) {
			while (true) {
				int test = randSetA.nextInt(10000);

				if (!setA.contains(test)) {
					setA.add(test);
				} else {
					continue;
				}
				break;
			}
			while (true) {
				int test2 = randSetB.nextInt(10000);

				if (!setB.contains(test2)) {
					setB.add(test2);
				} else {
					continue;
				}
				break;
			}
		}
		
		for (int i = 0; i < numSetA; i++) {
			for (int j = 0; j < numSetA; j++) {
				coefficientMapping[i][j] = setA.get(i) < setB.get(j) ? 3 : 6;
			}
		}

		double[][] costMatrix = new double[numSetA][numSetA];

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				costMatrix[i][j] = Math.abs(setA.get(i) - setB.get(j));
			}
		}
		return costMatrix;
	}

	/**
	 * Generates 2D synthetic data and creates a cost matrix. Picks a random
	 * number between 0 and 1. Creates (X1,Y1), (X2,Y2) based coordinates and
	 * adds each (X1,Y1) to taxis and each (X2,Y2) to requests. Generates a cost
	 * matrix by finding the distance using the distance formula between each
	 * node from each set.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @return Cost matrix of the data
	 */
	public double[][] generateSynthetic2D(int numSetA) {

		Random randSetA = new Random();
		Random randSetB = new Random();

		double[][] costMatrix = new double[numSetA][numSetA];

		ArrayList<Double> Xtaxi = new ArrayList<Double>();
		ArrayList<Double> Ytaxi = new ArrayList<Double>();
		ArrayList<Double> Xrequest = new ArrayList<Double>();
		ArrayList<Double> Yrequest = new ArrayList<Double>();

		for (int i = 0; i < numSetA; i++) {
			Xtaxi.add(randSetA.nextDouble());
			Ytaxi.add(randSetA.nextDouble());
			Xrequest.add(randSetB.nextDouble());
			Yrequest.add(randSetB.nextDouble());
		}

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				double X1 = Xtaxi.get(i);
				double Y1 = Ytaxi.get(i);

				double X2 = Xrequest.get(j);
				double Y2 = Yrequest.get(j);

				costMatrix[i][j] = Math.sqrt(Math.pow((X2 - X1), 2.0) + Math.pow((Y2 - Y1), 2.0));
			}
		}

		return costMatrix;
	}

	/**
	 * Generates 2D synthetic data set based on this example from Dr.
	 * Raghvendra:
	 * 
	 * Take numSetA * 2 points (numSetA of each type). These numSetA * 2 points
	 * have integer coordinates that lie between 0 and sqrt(numSetA * 2). For
	 * every integer 0 <= i,j <= sqrt(numSetA * 2), we have a point (i,j) in the
	 * set.
	 * 
	 * Randomly permute all the points. The first half points in the random
	 * permutation are taxi locations and the next half are request locations.
	 * 
	 * Generates a cost matrix based on distance provided by the manhattan
	 * distance.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @return Cost matrix of the data
	 */
	public double[][] generateSynthetic2DExample(int numSetA) {
		ArrayList<UberObject> taxis = new ArrayList<UberObject>();
		ArrayList<UberObject> requests = new ArrayList<UberObject>();
		ArrayList<UberObject> allPoints = new ArrayList<UberObject>();

		double[][] costMatrix = new double[numSetA][numSetA];
		UberObject object = null;
		double ending = Math.sqrt(numSetA * 2);
		for (int i = 0; i < ending; i++) {
			for (int j = 0; j < ending; j++) {
				object = new UberObject(String.valueOf(i), String.valueOf(j));
				allPoints.add(object);
			}
		}

		Collections.shuffle(allPoints);

		for (int i = 0; i < allPoints.size(); i++) {
			if (i < numSetA) {
				taxis.add(allPoints.get(i));
			} else {
				requests.add(allPoints.get(i));
			}
		}

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				int X1 = Integer.valueOf(taxis.get(i).getLatitude());
				int Y1 = Integer.valueOf(taxis.get(i).getLongitude());
				int X2 = Integer.valueOf(requests.get(j).getLatitude());
				int Y2 = Integer.valueOf(requests.get(j).getLongitude());

				// Round it off to the 100's place if you are using distance
				// formula
				// costMatrix[i][j] = Math.sqrt(Math.pow((X2-X1), 2.0) +
				// Math.pow((Y2-Y1), 2.0));
				costMatrix[i][j] = Math.abs(X1 - X2) + Math.abs(Y1 - Y2);
			}
		}

		return costMatrix;
	}

	/**
	 * Generates 2D synthetic data set based on this example from Dr.
	 * Raghvendra:
	 * 
	 * Generate 150 taxi locations randomly on a unit square. Next, choose half
	 * of these 150 taxi locations to be request locations as well (that gives
	 * you 75 request points identical to taxi locations). The remaining 75
	 * request locations can be chosen randomly from the unit square.
	 * 
	 * Generates a cost matrix based on distance provided by the distance
	 * formula
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @return Cost matrix of the data
	 */
	public double[][] generateSynthetic2DExample2(int nodeSize) {
		Random randSetA = new Random();
		Random randSetB = new Random();

		double[][] costMatrix = new double[nodeSize][nodeSize];

		ArrayList<Double> Xtaxi = new ArrayList<Double>();
		ArrayList<Double> Ytaxi = new ArrayList<Double>();
		ArrayList<Double> Xrequest = new ArrayList<Double>();
		ArrayList<Double> Yrequest = new ArrayList<Double>();

		for (int i = 0; i < nodeSize; i++) {
			Xtaxi.add(randSetA.nextDouble());
			Ytaxi.add(randSetA.nextDouble());
		}

		for (int i = 0; i < nodeSize / 2; i++) {
			Xrequest.add(Xtaxi.get(i));
			Yrequest.add(Ytaxi.get(i));
		}

		for (int i = nodeSize / 2; i < nodeSize; i++) {
			Xrequest.add(randSetB.nextDouble());
			Yrequest.add(randSetB.nextDouble());
		}

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				double X1 = Xtaxi.get(i);
				double Y1 = Ytaxi.get(i);

				double X2 = Xrequest.get(j);
				double Y2 = Yrequest.get(j);

				costMatrix[i][j] = Math.sqrt(Math.pow((X2 - X1), 2.0) + Math.pow((Y2 - Y1), 2.0));
			}
		}

		return costMatrix;
	}
}
