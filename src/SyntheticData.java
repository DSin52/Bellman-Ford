import java.util.ArrayList;
import java.util.Random;

public class SyntheticData {


	public double[][] generateSynthetic() {

		Random randSetA = new Random(2);
		Random randSetB = new Random(4);

		ArrayList<Integer> setA = new ArrayList<Integer>();
		ArrayList<Integer> setB = new ArrayList<Integer>();

		for (int i = 0; i < 500; i++) {
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
		
		double[][] costMatrix = new double[500][500];
		
		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[i].length; j++) {
				costMatrix[i][j] = Math.abs(setA.get(i) - setB.get(j));
			}
		}
		return costMatrix;
	}

}
