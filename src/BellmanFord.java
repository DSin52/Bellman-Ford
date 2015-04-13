import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Computes the smallest cost matching of a bipartite graph using the Bellman
 * Ford algorithm. Also can be used to verify solution with Hungarian algorithm.
 * 
 */
public class BellmanFord {

	private UberObject[] taxis; // Set A
	private UberObject[] customers; // Set B
	private double[][] costMatrix; // Cost matrix where each element is distance

	/**
	 * Computes the smallest cost matching using the Hungarian algorithm. For
	 * verifying purposes.
	 * 
	 * @param filename
	 *            Name of dataset file.
	 * @param nodesToRead
	 *            Number of nodes/vertices in Set A.
	 */
	public void verifyHungarian(String filename, int nodesToRead) {
		if (filename.equals("synthetic")) {
			costMatrix = new SyntheticData().generateSynthetic(nodesToRead);
		} else {
			parseData(filename, nodesToRead);

			computeCostMatrix();
		}

		HungarianAlgorithm test = new HungarianAlgorithm(costMatrix);

		int[] tester = test.execute();
		double totalCost = 0;
		for (int i = 0; i < tester.length; i++) {
			totalCost += costMatrix[i][tester[i]];
		}
		System.out.println("COST: " + totalCost);
	}

	/**
	 * Computes the smallest cost matching using the Bellman ford algorithm in
	 * the offline setting.
	 * 
	 * @param filename
	 *            Name of dataset file.
	 * @param nodesToRead
	 *            Number of nodes/vertices in Set A.
	 */
	public void computeOfflineMatching(String filename, int numSetA) {
		// Final offline matching ArrayList of directed edges
		ArrayList<DirectedEdge> offlineMatching = new ArrayList<DirectedEdge>();

		// Temporary matching ArrayList of Set B values for internal management
		ArrayList<Integer> matching = new ArrayList<Integer>();

		// ArrayList that stores all negative cycle indices for repeated
		// processing
		ArrayList<Integer> negativeCycleIndex = new ArrayList<Integer>();

		// Index of the current source node being processed
		int index = 0;

		// double totalCost = 0;

		// Boolean value that is set to true
		boolean runNegativeCycleIndices = false;

		if (filename.equals("synthetic")) {
			costMatrix = new SyntheticData().generateSynthetic(numSetA);
		} else {
			parseData(filename, numSetA);

			computeCostMatrix();
		}

		// Construct a DiGraph from the original costmatrix
		EdgeWeightedDigraph original = constructDigraphFromMatrix(costMatrix);

		ArrayList<Integer> sourceIndices = new ArrayList<Integer>();
		for (int i = 0; i < numSetA; i++) {
			sourceIndices.add(i);
		}

		// Randomizes the source node indices chosen for augmentation
		Collections.shuffle(sourceIndices);

		/*
		 * Core of the algorithm
		 * 
		 * Algorithm repeats until matching is perfect. Source node index is
		 * picked and BellmanFord is ran on that node. This computes all the
		 * paths and their costs from the source node to every target node. All
		 * paths are processed to choose the minimum cost and to also check for
		 * negative cycles. If a negative cycle occurs on the node, then add it
		 * to the negativeCycleIndex ArrayList and move on to the next source
		 * node. Else, the minimum cost path is chosen. The best path is added
		 * to an ArrayList bestPath and a new DiGraph is constructed with the
		 * best cost matching and the previous matchings. This new edge matching
		 * is added to the matchings ArrayList and the original DiGraph is
		 * updated with this new DiGraph. The process is repeated.
		 * 
		 * If negative cycles occurred during the processing, an additional
		 * iteration over the negativeCycleIndex ArrayList is ran to process
		 * these indices and find their proper matchings.
		 */
		while (matching.size() < numSetA) {

			if (index == numSetA) { // Last source index to process?
				if (negativeCycleIndex.size() > 0) { // Negative cycles to
														// process?
					index = negativeCycleIndex.remove(0);
					runNegativeCycleIndices = true;
				} else {
					break; // All processing complete
				}
			}

			int source = sourceIndices.get(index);

			// Run BellmanFord algorithm on source index
			BellmanFordSP sp = new BellmanFordSP(original, source);

			Iterator<DirectedEdge> iter = null;
			ArrayList<DirectedEdge> bestPath = new ArrayList<DirectedEdge>();

			double minPath = Double.MAX_VALUE;

			// Obtain minimum cost path
			for (int v = numSetA; v < original.V(); v++) {

				if (matching.contains(v)) {
					continue;
				}

				if (sp.hasNegativeCycle()) {
					negativeCycleIndex.add(index);
					index++;
					break;
				}

				if (sp.hasPathTo(v)) {
					if (sp.distTo(v) < minPath) {
						minPath = sp.distTo(v);
						iter = sp.pathTo(v).iterator();
					}
				}
			}

			// Negative cycle detected, don't process current index
			if (iter == null) {
				if (runNegativeCycleIndices) {
					index = numSetA;
				}
				continue;
			}

			while (iter.hasNext()) {
				bestPath.add(iter.next());
			}

			// New iteration DiGraph that will have updated edges
			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(
					numSetA + costMatrix[0].length);

			// Update matchings and new DiGraph
			for (DirectedEdge e : original.edges()) {
				if (bestPath.contains(e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(),
							-1.0 * e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0) {
						if (!matching.contains(e.to())) {
							matching.add(e.to());
						}
					}
					// totalCost += e.weight();

				} else {
					nextIterationGraph.addEdge(e);
				}
			}

			original = nextIterationGraph;

			// Increment index if not processing negative cycles, else set index
			// to last source node
			if (!runNegativeCycleIndices) {
				index++;
			} else {
				index = numSetA;
			}
		}

		// Add all matched edges to offline matching with proper weight
		for (DirectedEdge edge : original.edges()) {
			if (edge.weight() < 0) {
				offlineMatching.add(new DirectedEdge(edge.to(), edge.from(),
						-1.0 * edge.weight()));
			}
		}

		// System.out.println("Digraph: " + offlineMatching.toString());

		// System.out.println("LENGTH: " + matching.size());
		// System.out.println("TOTAL NET COST: " + totalCost);
		System.out.println("TOTAL TEST COST: "
				+ calculateTotalCost(offlineMatching));
	}

	/**
	 * Calculates the total net cost of the matching computed by Bellman Ford
	 * 
	 * @param matching
	 *            The edges in the matching from Bellman Ford
	 * @return Total net cost of the matching
	 */
	public double calculateTotalCost(ArrayList<DirectedEdge> matching) {
		double testCost = 0;
		for (DirectedEdge test : matching) {
			testCost += test.weight();
		}
		return testCost;
	}

	/**
	 * Builds the Element ArrayList when given a cost matrix
	 * 
	 * @param costMatrix
	 *            A simple 2-D array representing the costs between edges
	 * @return An ArrayList of Elements used to create DirectedEdges
	 */
	public static ArrayList<Element> buildElementMatrix(double[][] costMatrix) {

		ArrayList<Element> elementList = new ArrayList<Element>();

		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[0].length; j++) {
				Element element = new Element(i, j + costMatrix.length,
						costMatrix[i][j]);
				elementList.add(element);
			}
		}
		return elementList;
	}

	/**
	 * Builds the EdgeWeightedDigraph when given a cost matrix
	 * 
	 * @param costMatrix
	 *            A simple 2-D array representing the costs between edges
	 * @return The EdgeWeightedDigraph; simply a bipartite graph with all
	 *         vertices, edges and weights associated in one data structure
	 */
	public static EdgeWeightedDigraph constructDigraphFromMatrix(
			double[][] costMatrix) {
		EdgeWeightedDigraph diGraph = new EdgeWeightedDigraph(costMatrix.length
				+ costMatrix[0].length);
		ArrayList<Element> elements = buildElementMatrix(costMatrix);

		for (Element ele : elements) {
			DirectedEdge edge = new DirectedEdge(ele.getX(), ele.getY(),
					ele.getWeight());
			diGraph.addEdge(edge);
		}
		return diGraph;
	}

	/**
	 * Computes the cost matrix by processing the taxis (Set A) and customers
	 * (Set B) ArrayLists and calculating the haversine distance between their
	 * locations.
	 */
	private void computeCostMatrix() {
		for (int i = 0; i < taxis.length; i++) {
			for (int j = 0; j < customers.length; j++) {
				costMatrix[i][j] = Distance.haversine(taxis[i].getLatitude(),
						taxis[i].getLongitude(), customers[j].getLatitude(),
						customers[j].getLongitude());
			}
		}
	}

	/**
	 * Parses the data set with the given filename. Reads each line and creates
	 * a new UberObject for a taxi and customer, containing the latitude and
	 * longitude for each.
	 * 
	 * @param filename
	 *            Name of the data set csv file.
	 * @param maxLines
	 *            Number of lines to parse.
	 */
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
