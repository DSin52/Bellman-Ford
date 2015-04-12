import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class BellmanFord {

	private UberObject[] taxis;
	private UberObject[] customers;
	private double[][] costMatrix;

	public void verifyHungarian(String filename, int nodesToRead) {
		// parseData(filename, nodesToRead);
		//
		// computeCostMatrix();
		double[][] costMatrix = { { 1.2407605944068654, 1.598409646458353, 4.445187312567201, 0.5750987439590294 },
				{ 2.5812115319943256, 2.3367745613022697, 0.695289740587347, 3.5836292438059973 },
				{ 1.946676286724372, 1.6714977603974595, 1.37800878742508, 2.9341351129539426 },
				{ 1.6120755445675552, 1.9684280739081026, 4.801589666570361, 0.8556136958433838 } };
		// double[][] costMatrix = { { 29, 57, 66, 27, 71, 33 },
		// { 55, 67, 80, 65, 14, 11 }, { 42, 51, 20, 94, 75, 28 },
		// { 82, 6, 49, 15, 22, 11 }, { 54, 46, 15, 25, 56, 89 },
		// { 94, 60, 99, 43, 81, 13 } };

		HungarianAlgorithm test = new HungarianAlgorithm(costMatrix);

		int[] tester = test.execute();
		double totalCost = 0;
		for (int i = 0; i < tester.length; i++) {
			// System.out.print("For job " + i + ", use worker " + tester[i] +
			// ".\n");
			totalCost += costMatrix[i][tester[i]];
		}
		System.out.println("COST: " + totalCost);

	}

	public void computeOfflineMatching(String filename, int nodesToRead) {
		ArrayList<DirectedEdge> offlineMatching = new ArrayList<DirectedEdge>();

		ArrayList<Integer> matching = new ArrayList<Integer>();

		// parseData(filename, nodesToRead);
		//
		// computeCostMatrix();

		double[][] costMatrix = { { 1.2407605944068654, 1.598409646458353, 4.445187312567201, 0.5750987439590294 },
				{ 2.5812115319943256, 2.3367745613022697, 0.695289740587347, 3.5836292438059973 },
				{ 1.946676286724372, 1.6714977603974595, 1.37800878742508, 2.9341351129539426 },
				{ 1.6120755445675552, 1.9684280739081026, 4.801589666570361, 0.8556136958433838 } };

		// double[][] costMatrix = { { 29, 57, 66, 27, 71, 33 },
		// { 55, 67, 80, 65, 14, 11 }, { 42, 51, 20, 94, 75, 28 },
		// { 82, 6, 49, 15, 22, 11 }, { 54, 46, 15, 25, 56, 89 },
		// { 94, 60, 99, 43, 81, 13 } };
		// double[][] costMatrix = {{5, 1, 7}, {13, 1, 2}, {6, 1, 9}};

		double totalCost = 0;

		EdgeWeightedDigraph original = constructDigraphFromMatrix(costMatrix);
		int source = 0;

		while (matching.size() < costMatrix.length) {
			BellmanFordSP sp = new BellmanFordSP(original, source);

			Iterator<DirectedEdge> iter = null;
			ArrayList<DirectedEdge> bestPath = new ArrayList<DirectedEdge>();

			double minPath = Double.MAX_VALUE;

			for (int v = 0 + costMatrix.length; v < original.V(); v++) {

				if (matching.contains(v)) {
					continue;
				}

				if (sp.hasPathTo(v)) {
					for (DirectedEdge e : sp.pathTo(v)) {
						if (sp.distTo(v) < minPath) {
							minPath = sp.distTo(v);
							iter = sp.pathTo(v).iterator();
						}
					}
				}
			}

			while (iter.hasNext()) {
				bestPath.add(iter.next());
			}

			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(costMatrix.length + costMatrix[0].length);

			for (DirectedEdge e : original.edges()) {
				if (bestPath.contains(e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(), -1 * e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() < 0) {
						if (!matching.contains(e.to())) {
							matching.add(e.to());
							totalCost += e.weight();
						}
					}

				} else {
					nextIterationGraph.addEdge(e);
				}
			}
			original = nextIterationGraph;
			source += 1;
		}

		for (DirectedEdge edge : original.edges()) {
			if (edge.weight() < 0) {
				offlineMatching.add(new DirectedEdge(edge.to(), edge.from(), -1 * edge.weight()));
			}
		}

		System.out.println("Original: " + original.toString());
		System.out.println("Digraph: " + offlineMatching.toString());
		System.out.println("TOTAL NET COST: " + totalCost);
	}

	public static ArrayList<Element> buildElementMatrix(double[][] costMatrix2) {

		ArrayList<Element> elementList = new ArrayList<Element>();

		for (int i = 0; i < costMatrix2.length; i++) {
			for (int j = 0; j < costMatrix2[0].length; j++) {
				Element element = new Element(i, j + costMatrix2.length, costMatrix2[i][j]);
				elementList.add(element);
			}
		}
		return elementList;
	}

	public static EdgeWeightedDigraph constructDigraphFromMatrix(double[][] costMatrix2) {
		EdgeWeightedDigraph diGraph = new EdgeWeightedDigraph(costMatrix2.length + costMatrix2[0].length);
		ArrayList<Element> elements = buildElementMatrix(costMatrix2);

		for (Element ele : elements) {
			DirectedEdge edge = new DirectedEdge(ele.getX(), ele.getY(), ele.getWeight());
			diGraph.addEdge(edge);
		}
		return diGraph;
	}

	/**
	 * Specify the number of lines to read from the csv file (N). Max: 100 (for
	 * now) Create an NxN matrix. N^2 loop. Read one row of taxi location and
	 * then go through all rows of customer requests, calculating the distance
	 * for each pair. Fill the NxN matrix across with these values Continue with
	 * next row of taxi locations etc etc. This will construct the cost matrix
	 * Compute the Hungarian Algorithm based on this cost matrix. (offline)
	 */
	private void computeCostMatrix() {
		for (int i = 0; i < taxis.length; i++) {
			for (int j = 0; j < customers.length; j++) {
				costMatrix[i][j] = Distance.haversine(taxis[i].getLatitude(), taxis[i].getLongitude(), customers[j].getLatitude(),
						customers[j].getLongitude());
				System.out.print(costMatrix[i][j] + ", ");
			}
			System.out.println();
		}
	}

	private void parseData(String filename, int maxLines) {

		taxis = new UberObject[maxLines];
		customers = new UberObject[maxLines];
		costMatrix = new double[maxLines][maxLines];

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(filename));
			int currentLine = -1;
			while ((line = br.readLine()) != null && currentLine < maxLines) {
				if (currentLine == -1) {
					currentLine++;
					continue;
				}
				// use comma as separator
				String[] data = line.split(cvsSplitBy);
				taxis[currentLine] = new UberObject(data[11], data[10]);
				customers[currentLine] = new UberObject(data[13], data[12]);

				currentLine++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
