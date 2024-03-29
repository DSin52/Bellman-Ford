import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Computes the smallest cost matching (online, offline and greedy) of a
 * bipartite graph using the Bellman Ford algorithm. Also can be used to verify
 * solution with Hungarian algorithm.
 * 
 * @author Sanchit Chadha
 * @author Divit Singh
 */
public class BellmanFord {

	// Constant multiplier to improve competitive ratio
	private double constant = 1.0;

	private UberObject[] taxis; // Set A
	private UberObject[] customers; // Set B
	private double[][] costMatrix; // Cost matrix where each element is distance
	
	//Heuristic flags for evaluation
	private static final boolean HEURISTIC_COEFFICIENT_LINE = true; 
	

	/**
	 * Sets the constant multiplier that improves competitive ratio
	 * 
	 * @param constant
	 *            The constant value to be set
	 */
	public void setConstant(double constant) {
		this.constant = constant;
	}

	/**
	 * Generates a randomized list of request or destination indices for the
	 * online algorithm to utilize.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @return A randomized ArrayList of destination indices: numSetA <= i <
	 *         numSetA*2
	 */
	public ArrayList<Integer> permuteDestinations(int numSetA) {

		ArrayList<Integer> destinationIndices = new ArrayList<Integer>();

		for (int i = numSetA; i < numSetA * 2; i++) {
			destinationIndices.add(i);
		}

		Collections.shuffle(destinationIndices);

		return destinationIndices;
	}

	/**
	 * Generates a cost matrix based on what data source type is given.
	 * 
	 * @param dataSource
	 *            The type of data source. ("trip_data_test.csv", "synthetic1D",
	 *            "synthetic2D", "synthetic2DExample", "synthetic2DExample2")
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 */
	public void generateCostMatrix(String dataSource, int numSetA) {
		switch (dataSource) {
		case "synthetic1D":
			costMatrix = new SyntheticData().generateSynthetic1D(numSetA);
			break;
		case "synthetic2D":
			costMatrix = new SyntheticData().generateSynthetic2D(numSetA);
			break;
		case "synthetic2DExample1":
			costMatrix = new SyntheticData().generateSynthetic2DExample(numSetA);
			break;
		case "synthetic2DExample2":
			costMatrix = new SyntheticData().generateSynthetic2DExample2(numSetA);
			break;
		default:
			parseData(dataSource, numSetA);
			computeCostMatrix();
			break;
		}
	}

	/**
	 * Executes an algorithm based on the type specified.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @param type
	 *            The type of algorithm to execute. ("hungarian", "offline",
	 *            "online" or "greedy")
	 * @param destinationIndices
	 *            The ArrayList of randomized destination indices
	 * @return The total net cost of the best matching based on the type of
	 *         algorithm executed.
	 */
	public double execute(int numSetA, String type, ArrayList<Integer> destinationIndices) {
		double result = 0.0;

		switch (type) {
		case "hungarian":
			result = verifyHungarian();
			break;
		case "offline":
			result = computeOfflineMatching(numSetA);
			break;
		case "online":
			result = computeOnlineMatching(numSetA, destinationIndices);
			break;
		case "greedy":
			result = computeGreedyMatching(numSetA, destinationIndices);
			break;
		}
		return result;

	}

	/**
	 * Computes the smallest cost matching using the Hungarian algorithm. For
	 * verification purposes.
	 * 
	 * @return The net total cost of the optimal matching found by executing
	 *         Hungarian algorithm.
	 */
	public double verifyHungarian() {
		HungarianAlgorithm test = new HungarianAlgorithm(costMatrix);

		int[] tester = test.execute();
		double totalCost = 0;
		for (int i = 0; i < tester.length; i++) {
			totalCost += costMatrix[i][tester[i]];
		}

		return totalCost;
	}

	/**
	 * Computes the smallest cost matching using the Bellman ford algorithm in
	 * the offline setting.
	 * 
	 * @param filename
	 *            Name of dataset file.
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * 
	 * @return The total net cost of the best offline matching
	 */
	public double computeOfflineMatching(int numSetA) {
		// Final offline matching ArrayList of directed edges
		ArrayList<DirectedEdge> offlineMatching = new ArrayList<DirectedEdge>();

		// Temporary matching ArrayList of Set B values for internal management
		ArrayList<Integer> matching = new ArrayList<Integer>();

		// ArrayList that stores all negative cycle indices for repeated
		// processing
		ArrayList<Integer> negativeCycleIndex = new ArrayList<Integer>();

		// Index of the current source node being processed
		int index = 0;

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
		 * node. Else, the minimum cost path is chosen. The best path
		 * (augmenting or direct path) is added to an ArrayList bestPath and a
		 * new DiGraph is constructed with the best cost matching and the
		 * previous matchings. This new edge matching is added to the matchings
		 * ArrayList and the original DiGraph is updated with this new DiGraph.
		 * The process is repeated.
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

				// If vertex is already in the matching, skip it
				if (matching.contains(v)) {
					continue;
				}

				// Check if vertex causes a negative cycle
				if (sp.hasNegativeCycle()) {
					negativeCycleIndex.add(index);
					index++;
					break;
				}

				// Check if a path exists from source vertex to destination
				// vertex v
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
			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(numSetA
					+ costMatrix[0].length);

			// Update matchings and new DiGraph
			for (DirectedEdge e : original.edges()) {
				if (bestPath.contains(e)) {
					DirectedEdge edgeToAdd = new DirectedEdge(e.to(), e.from(), -1.0 * e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0) {
						if (!matching.contains(e.to())) {
							matching.add(e.to());
						}
					}
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
				offlineMatching.add(new DirectedEdge(edge.to(), edge.from(), -1.0 * edge.weight()));
			}
		}

		double totalCost = calculateTotalCost(offlineMatching);

		return totalCost;
	}

	/**
	 * Computes the smallest cost matching using the Bellman ford algorithm in
	 * the online setting.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @param destinationIndices
	 *            The ArrayList of randomized destination indices
	 * 
	 * @return The total net cost of the best offline matching
	 */
	public double computeOnlineMatching(int numSetA, ArrayList<Integer> destinationIndices) {
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

		double[][] tempMatrix = generateInitialMatrix();

		// Construct a DiGraph from the original costmatrix
		EdgeWeightedDigraph original = null;

		/*
		 * Core of the algorithm
		 * 
		 * Algorithm repeats until matching is perfect. BellmanFord is ran from
		 * all nodes in setA to the single incoming node decided by the
		 * destinationIndices ArrayList. The shortest path from a node in setA
		 * to the incoming node in setB is chosen and a new DiGraph is
		 * constructed with that matching. This path can be a direct path or an
		 * augmenting path which causes the cost of previously matched edges to
		 * be positive and the cost of the new matched edges to be negative.
		 * This allows BellmanFord to calculate the legitimate best augmented
		 * path. The online matching ArrayList has the source and destination
		 * index and the original cost of that source/destination edge stored in
		 * it instead of the actual cost of the augmented path. Negative cycles
		 * are not processed for the online setting.
		 */
		while (matching.size() < numSetA) {

			// The index of the incoming request
			int destinationIndex = destinationIndices.get(index);

			// Normalized matrix where all the costs of the incoming request to
			// each taxi are revealed
			tempMatrix = normalizeMatrix(destinationIndex - numSetA, tempMatrix);

			// DiGraph of the normalized matrix
			original = constructDigraphFromMatrix(tempMatrix);

			ArrayList<DirectedEdge> bestPath = new ArrayList<DirectedEdge>();
			Iterator<DirectedEdge> iter = null;

			double minPath = Double.MAX_VALUE;

			// Checks each source index to the incoming destination index for a
			// path and stores the minimum path.
			for (int source : sourceIndices) {

				if (matching.contains(source)) {
					continue;
				}

				BellmanFordSP sp = new BellmanFordSP(original, source);

				if (sp.hasPathTo(destinationIndex)) {
					double distance = sp.distTo(destinationIndex);

					if (distance < minPath) {
						minPath = distance;
						iter = sp.pathTo(destinationIndex).iterator();
					}
				}
			}

			while (iter.hasNext()) {
				bestPath.add(iter.next());
			}

			// The source and destination index from the best path
			int source = bestPath.get(0).from();
			int destination = bestPath.get(bestPath.size() - 1).to();

			// New iteration DiGraph that will have updated edges
			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(numSetA
					+ costMatrix[index].length);

			// Update matchings and new DiGraph. Checks each edge from the
			// original digraph
			for (DirectedEdge e : original.edges()) {
				DirectedEdge edgeToAdd = null;
				// If this edge is part of the matching
				if (containsEdge(bestPath, e)) {

					// If the edge is already matched, make the weight positive
					// and multiple by constant to get the weighted cost back
					if (e.weight() < 0.0) {
						edgeToAdd = new DirectedEdge(e.to(), e.from(), -1.0 * constant * e.weight());
					} else {
						// Else negate the weight and divide by the constant so
						// Bellman Ford can calculate the correct cost
						edgeToAdd = new DirectedEdge(e.to(), e.from(), (-1.0 * e.weight())
								/ constant);
					}

					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0.0) {
						if (!matching.contains(e.from())) {
							// Add only new matched edges to the online
							// matching. No duplicates.
							matching.add(e.from());

							onlineMatching.add(new DirectedEdge(destination, source,
									costMatrix[source][destination - costMatrix[index].length]));
						}
					}
				} else {
					// Add all updated edges to the next iteration graph
					edgeToAdd = new DirectedEdge(e.from(), e.to(), e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
				}

				// Update temp matrix values according to the matching changes
				// made above
				int x = edgeToAdd.from();
				int y = edgeToAdd.to();

				if (x >= numSetA) {
					tempMatrix[y][x - numSetA] = edgeToAdd.weight();
				} else {
					tempMatrix[x][y - numSetA] = edgeToAdd.weight();
				}

			}
			// Increments index to get the next destination node
			index++;
		}

		double totalCost = calculateTotalCost(onlineMatching);

		return totalCost;

	}

	/**
	 * Computes the greedy matching for a given cost matrix. Simply goes down
	 * the column of the cost matrix based on the order dictated by the
	 * destinationIndices ArrayList and finds the lowest cost element that is
	 * not already in the matching. Adds that edge to the matching and
	 * continues.
	 * 
	 * @param numSetA
	 *            The number of taxis (nodes in set A)
	 * @param destinationIndices
	 *            The ArrayList of randomized destination indices
	 * @return The total net cost of the greedy matching
	 */
	public double computeGreedyMatching(int numSetA, ArrayList<Integer> destinationIndices) {
		// Final greedy matching
		ArrayList<DirectedEdge> matching = new ArrayList<DirectedEdge>();

		// Store set A node indices that are in the matching
		ArrayList<Integer> fromMatchings = new ArrayList<Integer>();

		// Index of the current source node being processed
		int index = 0;

		while (matching.size() < numSetA) {
			// Get row of smallest element in column of destination index
			double min = Double.MAX_VALUE;
			DirectedEdge minEdge = null;

			for (int i = 0; i < costMatrix.length; i++) {
				int destinationIndex = destinationIndices.get(index) - numSetA;
				double cost = costMatrix[i][destinationIndex];
				DirectedEdge edge = new DirectedEdge(i, destinationIndices.get(index), cost);

				if (!fromMatchings.contains(edge.from()) && edge.weight() < min) {
					min = cost;
					minEdge = edge;
				}
			}
			matching.add(minEdge);
			fromMatchings.add(minEdge.from());
			index++;
		}

		double totalCost = calculateTotalCost(matching);

		return totalCost;
	}

	/**
	 * Helper method that checks if two DirectedEdges are equal to each other.
	 * To, from and weight attributes must equal in both objects.
	 * 
	 * @param edge1
	 *            First directed edge
	 * @param edge2
	 *            Second directed edge
	 * @return True if all attributes are equal in both edges. False if either
	 *         edge is null or attributes don't match.
	 */
	public boolean edgeEquals(DirectedEdge edge1, DirectedEdge edge2) {

		if (edge1.equals(null) || edge2.equals(null)) {
			return false;
		}

		if (edge1.to() == edge2.to() && edge1.from() == edge2.from()
				&& edge1.weight() == edge2.weight()) {
			return true;
		}

		return false;
	}

	/**
	 * Helper method that checks if an ArrayList of DirectedEdges contains an
	 * edge.
	 * 
	 * @param list
	 *            The list of DirectedEdges that may or may not contain the edge
	 * @param edge
	 *            The edge to check if it is contained in the list
	 * @return True if the list contains edge, false otherwise.
	 */
	public boolean containsEdge(ArrayList<DirectedEdge> list, DirectedEdge edge) {
		for (DirectedEdge e : list) {
			if (edgeEquals(e, edge)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculates the total net cost of the matching computed by the selected
	 * algorithm
	 * 
	 * @param matching
	 *            The edges in the matching from running the selected algorithm
	 * @return Total net cost of the matching
	 */
	public double calculateTotalCost(ArrayList<DirectedEdge> matching) {
		double testCost = 0;
		for (DirectedEdge test : matching) {
			testCost += Math.abs(test.weight());
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
				Element element = new Element(i, j + costMatrix.length, costMatrix[i][j]);
				elementList.add(element);
			}
		}
		return elementList;
	}

	/**
	 * Generates a 2D array of doubles with all elements initialized to a large
	 * number (100000.0). This is to ensure that the online algorithm only takes
	 * the incoming request costs in consideration and none of the other nodes.
	 * 
	 * @return The initialized 2D array of doubles
	 */
	private double[][] generateInitialMatrix() {
		double[][] tempMatrix = new double[costMatrix.length][costMatrix.length];

		for (int i = 0; i < tempMatrix.length; i++) {
			for (int j = 0; j < tempMatrix[i].length; j++) {
				tempMatrix[i][j] = 100000.0;
			}
		}
		return tempMatrix;
	}

	/**
	 * Normalizes the matrix. Updates the given column to match the same values
	 * as the ones found in the original cost matrix. This replicates the
	 * behavior of an incoming request and the costs associated between the
	 * request and each available taxi. This method also multiplies the initial
	 * cost by a constant. So all incoming requests have a constant multiplied
	 * cost, but when an edge gets matched, the cost associated with that edge
	 * is divided by that constant. This is a modification that Dr. Raghvendra
	 * came up with to improve the competitive ratio in online settings.
	 * 
	 * @param column
	 *            The index of the column that represents an incoming request.
	 * @param tempMatrix
	 *            The temporary matrix that gets updated based on an incoming
	 *            request.
	 * @return The updated matrix with an incoming request and its costs
	 *         processed.
	 */
	private double[][] normalizeMatrix(int column, double[][] tempMatrix) {
		for (int i = 0; i < tempMatrix.length; i++) {
			tempMatrix[i][column] = !HEURISTIC_COEFFICIENT_LINE ? constant * costMatrix[i][column] : SyntheticData.coefficientMapping[i][column] * costMatrix[i][column];
		}
		return tempMatrix;
	}

	/**
	 * Builds the EdgeWeightedDigraph when given a cost matrix
	 * 
	 * @param costMatrix
	 *            A simple 2-D array representing the costs between edges
	 * @return The EdgeWeightedDigraph; simply a bipartite graph with all
	 *         vertices, edges and weights associated in one data structure
	 */
	public static EdgeWeightedDigraph constructDigraphFromMatrix(double[][] costMatrix) {
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
