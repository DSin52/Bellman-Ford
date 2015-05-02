import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generates random synthetic 1-D data and the associated cost matrix.
 */
public class SyntheticData {
	
	public double[][] generateSynthetic2D(int nodeSize) {

		Random randSetA = new Random();
		Random randSetB = new Random();
		

		double[][] costMatrix = new double[nodeSize][nodeSize];

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				double X1 = randSetA.nextDouble();
				double Y1 = randSetA.nextDouble();
				double X2 = randSetB.nextDouble();
				double Y2 = randSetB.nextDouble();
				
				costMatrix[i][j] = Math.sqrt(Math.pow((X2-X1), 2.0) + Math.pow((Y2-Y1), 2.0));
			}
		}

		return costMatrix;
	}
	
	public double[][] generateSynthetic2DExample(int nodeSize) {
		ArrayList<UberObject> taxis = new ArrayList<UberObject>();
		ArrayList<UberObject> requests = new ArrayList<UberObject>();
		ArrayList<UberObject> allPoints = new ArrayList<UberObject>();
		
		
		double[][] costMatrix = new double[nodeSize][nodeSize];
		UberObject object = null;
		double ending = Math.sqrt(nodeSize * 2);
		for (int i = 0; i < ending; i++) {
			for (int j = 0; j < ending; j++) {
				object = new UberObject(String.valueOf(i), String.valueOf(j));
				allPoints.add(object);
//				if ((i+j) % 2 == 0) {
//					taxis.add(object);
//				} else {
//					requests.add(object);
//				}
			}
		}
		Collections.shuffle(allPoints);
		
		for (int i = 0; i < allPoints.size(); i++) {
			if (i < nodeSize) {
				taxis.add(allPoints.get(i));
				System.out.print(allPoints.get(i).getLatitude() + " ");
				System.out.println();
				System.out.print(allPoints.get(i).getLongitude() + " ");
			} else {
				requests.add(allPoints.get(i));
				System.out.print(allPoints.get(i).getLatitude() + " ");
				System.out.println();
				System.out.print(allPoints.get(i).getLongitude() + " ");
			}
		}
		
		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				int X1 = Integer.valueOf(taxis.get(i).getLatitude());
				int Y1 = Integer.valueOf(taxis.get(i).getLongitude());
				int X2 = Integer.valueOf(requests.get(j).getLatitude());
				int Y2 = Integer.valueOf(requests.get(j).getLongitude());

				// Round it off to the 100's place if you are using distance formula
//				costMatrix[i][j] = Math.sqrt(Math.pow((X2-X1), 2.0) + Math.pow((Y2-Y1), 2.0));
				costMatrix[i][j] = Math.abs(X1 - X2) + Math.abs(Y1 - Y2);
			}
		}
		
		return costMatrix;
	}

	/**
	 * Generates the synthetic data and creates a cost matrix.
	 * 
	 * @param nodeSize
	 *            Number of total vertices in the synthetic data.
	 * @return Cost matrix of the data
	 */
	public double[][] generateSynthetic(int nodeSize) {

		Random randSetA = new Random(2);
		Random randSetB = new Random(4);

		ArrayList<Integer> setA = new ArrayList<Integer>();
		ArrayList<Integer> setB = new ArrayList<Integer>();

		for (int i = 0; i < nodeSize; i++) {
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

		double[][] costMatrix = new double[nodeSize][nodeSize];

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				costMatrix[i][j] = Math.abs(setA.get(i) - setB.get(j));
			}
		}
		return costMatrix;
	}

	public double[][] generateExample() {
		return new double[][]{{5, 1, 7}, {13, 1, 2}, {6, 1, 9}};
	}
}
