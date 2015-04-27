import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random synthetic 1-D data and the associated cost matrix.
 */
public class SyntheticData {
	
	private UberObject[] setA;
	private UberObject[] setB;
	
//	public double[][] generateSynthetic2D(int nodeSize) {
//
//		Random randSetA = new Random(2);
//		Random randSetB = new Random(4);
//
//		setA = new UberObject[nodeSize];
//		setB = new UberObject[nodeSize];
//		
//		
////		ArrayList<Integer> setA = new ArrayList<Integer>();
////		ArrayList<Integer> setB = new ArrayList<Integer>();
//
//		for (int i = 0; i < nodeSize; i++) {
//			while (true) {
//				int test = randSetA.nextInt(10000);
//
//				if (!setA.contains(test)) {
//					setA.add(test);
//				} else {
//					continue;
//				}
//				break;
//			}
//			while (true) {
//				int test2 = randSetB.nextInt(10000);
//
//				if (!setB.contains(test2)) {
//					setB.add(test2);
//				} else {
//					continue;
//				}
//				break;
//			}
//		}
//
//		double[][] costMatrix = new double[nodeSize][nodeSize];
//
//		for (int i = 0; i < costMatrix.length; i++) {
//			for (int j = 0; j < costMatrix[i].length; j++) {
//				costMatrix[i][j] = Math.abs(setA.get(i) - setB.get(j));
//			}
//		}
//		return costMatrix;
//	}

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
