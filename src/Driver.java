import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class Driver {
	public static ArrayList<Element> original;

	static ArrayList<Integer> matching;
	static ArrayList<DirectedEdge> finalMatching;
	
	private static UberObject[] taxis;
	private static UberObject[] customers;
	private static double[][] costMatrix;
	
	public static void main(String[] args) {

		matching = new ArrayList<Integer>();
		finalMatching = new ArrayList<DirectedEdge>();

		parseData("trip_data_test.csv", 2);

		computeCostMatrix();

//		int[][] costMatrix = { { 29, 57, 66, 27, 71, 33 },
//				{ 55, 67, 80, 65, 14, 11 }, { 42, 51, 20, 94, 75, 28 },
//				{ 82, 6, 49, 15, 22, 11 }, { 54, 46, 15, 25, 56, 89 },
//				{ 94, 60, 99, 43, 81, 13 } };

		double totalCost = 0;

		EdgeWeightedDigraph original = constructDigraphFromMatrix(costMatrix);
		int source = 0;

		while (matching.size() < costMatrix.length) {
			BellmanFordSP sp = new BellmanFordSP(original, source);

			Iterator<DirectedEdge> iter = null;
			ArrayList<DirectedEdge> bestPath = new ArrayList<DirectedEdge>();

			double minPath = Integer.MAX_VALUE;

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

			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(
					costMatrix.length + costMatrix[0].length);

			for (DirectedEdge e : original.edges()) {
				if (bestPath.contains(e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(),
							-1 * e.weight());
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
				finalMatching.add(new DirectedEdge(edge.to(), edge.from(), -1
						* edge.weight()));
			}
		}
		
		System.out.println("Original: " + original.toString());
		System.out.println("Digraph: " + finalMatching.toString());
		System.out.println("TOTAL NET COST: " + totalCost);

	}

	public static ArrayList<Element> buildElementMatrix(double[][] costMatrix2) {

		ArrayList<Element> elementList = new ArrayList<Element>();

		for (int i = 0; i < costMatrix2.length; i++) {
			for (int j = 0; j < costMatrix2[0].length; j++) {
				Element element = new Element(i, j + costMatrix2.length,
						costMatrix2[i][j]);
				elementList.add(element);
			}
		}
		return elementList;
	}

	public static EdgeWeightedDigraph constructDigraphFromMatrix(
			double[][] costMatrix2) {
		EdgeWeightedDigraph diGraph = new EdgeWeightedDigraph(costMatrix2.length
				+ costMatrix2[0].length);
		ArrayList<Element> elements = buildElementMatrix(costMatrix2);

		for (Element ele : elements) {
			DirectedEdge edge = new DirectedEdge(ele.getX(), ele.getY(),
					ele.getWeight());
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
	private static void computeCostMatrix() {
		for (int i = 0; i < taxis.length; i++) {
			for (int j = 0; j < customers.length; j++) {
				costMatrix[i][j] = Distance.haversine(taxis[i].getLatitude(),
						taxis[i].getLongitude(), customers[j].getLatitude(),
						customers[j].getLongitude());
			}
		}
	}

	private static void parseData(String filename, int maxLines) {

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
