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

	public double execute(String filename, int nodesToRead, String type) {
		double result = 0.0;

		switch (filename) {
		case "synthetic":
			costMatrix = new SyntheticData().generateSynthetic(nodesToRead);
			break;
		case "example":
			costMatrix = new SyntheticData().generateExample();
			break;
		default:
			parseData(filename, nodesToRead);
			computeCostMatrix();
			break;
		}

		switch (type) {
		case "hungarian":
			result = verifyHungarian(filename, nodesToRead);
			break;
		case "offline":
			result = computeOfflineMatching(filename, nodesToRead);
			break;
		case "online":
			result = computeOnlineMatching(filename, nodesToRead);
			break;
		case "greedy":
			result = computeGreedyOnlineMatching(filename, nodesToRead);
			break;
		}
		return result;

	}

	/**
	 * Computes the smallest cost matching using the Hungarian algorithm. For
	 * verifying purposes.
	 * 
	 * @param filename
	 *            Name of dataset file.
	 * @param nodesToRead
	 *            Number of nodes/vertices in Set A.
	 */
	public double verifyHungarian(String filename, int nodesToRead) {
		HungarianAlgorithm test = new HungarianAlgorithm(costMatrix);

		int[] tester = test.execute();
		double totalCost = 0;
		for (int i = 0; i < tester.length; i++) {
			totalCost += costMatrix[i][tester[i]];
		}
		System.out.println("COST: " + totalCost);

		return totalCost;
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
	public double computeOfflineMatching(String filename, int numSetA) {
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

		System.out.println("Offline Matching Digraph: "
				+ offlineMatching.toString());

		double totalCost = calculateTotalCost(offlineMatching);
		System.out.println("TOTAL OFFLINE COST: " + totalCost);

		return totalCost;
	}

	public double computeOnlineMatching(String filename, int numSetA) {
		// Final online matching ArrayList of directed edges
		ArrayList<DirectedEdge> onlineMatching = new ArrayList<DirectedEdge>();

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

		ArrayList<Integer> sourceIndices = new ArrayList<Integer>();
		for (int i = 0; i < numSetA; i++) {
			sourceIndices.add(i);
		}
		// sourceIndices.add(2);
		// sourceIndices.add(1);
		// sourceIndices.add(0);

		double[][] tempMatrix = generateInitialMatrix();

		EdgeWeightedDigraph original = null;

		// Randomizes the source node indices chosen for augmentation
		Collections.shuffle(sourceIndices);

		System.out.println("Source Order: " + sourceIndices.toString());

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

			tempMatrix = normalizeMatrix(index, tempMatrix);

			// if (index == 0) {
			original = constructDigraphFromMatrix(tempMatrix);
			// } else {
			// combine digraph with temp matrix to get the next point accounted
			// for
			// originalMatrix = constructMatrixFromDiGraph(original);

			// }

			// Construct a DiGraph from the original costmatrix

			int source = sourceIndices.get(index);
			int destination = -1;
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
						destination = v;
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
					numSetA + costMatrix[index].length);

			// Update matchings and new DiGraph
			for (DirectedEdge e : original.edges()) {
				// If this edge is part of the matching
				if (containsEdge(bestPath, e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(),
							-1.0 * e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0) {
						if (!matching.contains(e.to())) {
							matching.add(e.to());
							onlineMatching.add(new DirectedEdge(source,
									destination, e.weight()));
						}
					}
				} else {
					DirectedEdge constantEdge = null;
					if (e.weight() >= 0.0) {
						constantEdge = new DirectedEdge(e.from(), e.to(),
								3.0 * e.weight());
					} else {
						constantEdge = new DirectedEdge(e.from(), e.to(),
								e.weight());
					}
					// constantEdge = new DirectedEdge(e.from(),
					// e.to(), e.weight());
					nextIterationGraph.addEdge(constantEdge);
				}
			}

			// Get all the modified points from next digraph and update matrix
			// values accordingly
			for (DirectedEdge e : nextIterationGraph.edges()) {
				int x = e.from();
				int y = e.to();
				if (e.weight() < 0) {
					tempMatrix[y][x - numSetA] = e.weight();
				} else {
					tempMatrix[x][y - numSetA] = e.weight();
				}
			}

			// Increment index if not processing negative cycles, else set index
			// to last source node
			if (!runNegativeCycleIndices) {
				index++;
			} else {
				index = numSetA;
			}
		}

		System.out.println("Online Matching Digraph: "
				+ onlineMatching.toString());

		double totalCost = calculateTotalCost(onlineMatching);
		System.out.println("TOTAL ONLINE NET COST: " + totalCost);

		return totalCost;
	}

	public double computeGreedyOnlineMatching(String filename, int numSetA) {
		// Final online matching ArrayList of directed edges
		ArrayList<DirectedEdge> onlineMatching = new ArrayList<DirectedEdge>();

		// Temporary matching ArrayList of Set B values for internal management
		ArrayList<Integer> matching = new ArrayList<Integer>();

		// Index of the current source node being processed
		int index = 0;

		ArrayList<Integer> sourceIndices = new ArrayList<Integer>();
		for (int i = 0; i < numSetA; i++) {
			sourceIndices.add(i);
		}
		ArrayList<Integer> destinationIndices = new ArrayList<Integer>();
		for (int i = numSetA; i < numSetA * 2; i++) {
			destinationIndices.add(i);
		}

		Collections.shuffle(destinationIndices);

		double[][] initialMatrix = generateInitialMatrix();

		// Construct a DiGraph from the original costmatrix
		EdgeWeightedDigraph original = constructDigraphFromMatrix(costMatrix);


		System.out.println("Destination Order: "
				+ destinationIndices.toString());

		/*
		 * Core of the algorithm
		 */
		while (matching.size() < numSetA) {

			if (index == numSetA) { // Last source index to process?
				break; // All processing complete
			}

			int destinationIndex = destinationIndices.get(index);

			Iterator<DirectedEdge> iter = null;

			double[][] tempMatrix = normalizeMatrix(destinationIndex - numSetA,
					initialMatrix);

			ArrayList<DirectedEdge> taxiPath = new ArrayList<DirectedEdge>();
			ArrayList<Double> taxiDistances = new ArrayList<Double>();

			for (int source : sourceIndices) {

				if (matching.contains(source)) {
					continue;
				}

				BellmanFordSP sp = new BellmanFordSP(
						constructDigraphFromMatrix(tempMatrix), source);

				if (sp.hasPathTo(destinationIndex)) {
					iter = sp.pathTo(destinationIndex).iterator();
					taxiPath.add(iter.next());
					taxiDistances.add(sp.distTo(destinationIndex));
				}
			}

			double minCost = Double.MAX_VALUE;
			int minIndex = 0;

			for (int i = 0; i < taxiDistances.size(); i++) {
				double d = taxiDistances.get(i);
				if (d < minCost) {
					minCost = d;
					minIndex = i;
				}
			}
			int source = -1;
			int destination = -1;

			DirectedEdge bestPath = taxiPath.get(minIndex);
			source = bestPath.from();
			destination = bestPath.to();

			// New iteration DiGraph that will have updated edges
			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(
					numSetA + costMatrix[0].length);

			// Update matchings and new DiGraph
			for (DirectedEdge e : original.edges()) {
				if (edgeEquals(bestPath, e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(),
							-1.0 * e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0) {
						if (!matching.contains(e.from())) {
							matching.add(e.from());
							onlineMatching.add(new DirectedEdge(source,
									destination, e.weight()));
						}
					}
				} else {
					DirectedEdge constantEdge = new DirectedEdge(e.from(),
							e.to(), e.weight());
					nextIterationGraph.addEdge(constantEdge);
				}
			}

			original = nextIterationGraph;

			// Increment index if not processing negative cycles, else set index
			// to last source node
			index++;
		}

		System.out.println("Online Greedy Digraph: "
				+ onlineMatching.toString());

		double totalCost = calculateTotalCost(onlineMatching);
		System.out.println("TOTAL GREEDY ONLINE NET COST: " + totalCost);

		return totalCost;

	}

	public boolean edgeEquals(DirectedEdge e, DirectedEdge edge) {

		if (e.to() == edge.to() && e.from() == edge.from()
				&& e.weight() == edge.weight()) {
			return true;
		}

		return false;
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

	private double[][] generateInitialMatrix() {
		double[][] tempMatrix = new double[costMatrix.length][costMatrix.length];

		for (int i = 0; i < tempMatrix.length; i++) {
			for (int j = 0; j < tempMatrix[i].length; j++) {
				tempMatrix[i][j] = 100000.0;
			}
		}
		return tempMatrix;
	}

	private double[][] normalizeMatrix(int column, double[][] tempMatrix) {
		for (int i = 0; i < tempMatrix.length; i++) {
			for (int j = 0; j < tempMatrix[i].length; j++) {
				if (/* tempMatrix[i][j] == 100000.0 && */j == column) {
					tempMatrix[i][j] = costMatrix[i][j];
				}
			}
		}
		return tempMatrix;
	}

	public boolean containsEdge(ArrayList<DirectedEdge> list, DirectedEdge edge) {
		for (DirectedEdge e : list) {
			if (e.to() == edge.to() && e.from() == edge.from()
					&& e.weight() == edge.weight()) {
				return true;
			}
		}

		return false;
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
			DirectedEdge edge = null;
			if (ele.getWeight() < 0) {
				edge = new DirectedEdge(ele.getY(), ele.getX(), ele.getWeight());
			} else {
				edge = new DirectedEdge(ele.getX(), ele.getY(), ele.getWeight());
			}
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
