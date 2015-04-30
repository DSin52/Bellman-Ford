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

	private double constant = 1.5;

	private UberObject[] taxis; // Set A
	private UberObject[] customers; // Set B
	private double[][] costMatrix; // Cost matrix where each element is distance
	private double temp = 0.0;
	
	public void setConstant(double constant) {
		this.constant = constant;
	}

	public ArrayList<Integer> permuateDestinations(String filename,
			int nodesToRead) {
		
		ArrayList<Integer> destinationIndices = new ArrayList<Integer>();

//		int[] tester = {573, 579, 441, 663, 575, 682, 429, 675, 483, 411, 698, 680, 529, 477, 386, 536, 493, 492, 419, 554, 504, 628, 396, 456, 359, 410, 686, 542, 672, 642, 673, 426, 609, 374, 484, 679, 643, 631, 565, 413, 403, 394, 626, 610, 384, 599, 437, 522, 351, 507, 670, 586, 455, 543, 691, 696, 674, 373, 648, 595, 537, 475, 629, 547, 513, 414, 465, 539, 355, 623, 568, 452, 489, 398, 350, 367, 376, 651, 450, 589, 641, 514, 433, 502, 692, 541, 371, 448, 650, 678, 498, 644, 511, 601, 572, 431, 497, 632, 361, 480, 518, 531, 406, 379, 519, 481, 451, 467, 463, 453, 509, 619, 491, 499, 597, 569, 583, 405, 490, 576, 404, 556, 487, 368, 423, 591, 634, 618, 363, 581, 625, 566, 594, 604, 571, 562, 558, 472, 505, 535, 540, 369, 549, 577, 422, 546, 587, 360, 697, 649, 694, 377, 488, 653, 354, 606, 445, 449, 533, 409, 485, 515, 667, 525, 427, 446, 612, 567, 390, 665, 614, 527, 474, 464, 468, 689, 600, 570, 401, 454, 557, 688, 430, 544, 365, 561, 476, 657, 681, 555, 646, 495, 652, 530, 375, 399, 501, 478, 580, 603, 560, 605, 479, 664, 459, 442, 520, 389, 462, 516, 592, 496, 598, 659, 352, 640, 457, 438, 658, 552, 526, 627, 588, 417, 638, 470, 387, 473, 415, 684, 602, 666, 624, 503, 613, 388, 444, 385, 391, 420, 524, 421, 615, 532, 440, 381, 383, 447, 622, 590, 668, 461, 596, 380, 408, 506, 460, 645, 521, 550, 676, 630, 402, 655, 639, 378, 662, 593, 564, 356, 687, 392, 538, 424, 466, 635, 677, 397, 436, 654, 407, 517, 551, 366, 699, 510, 393, 585, 416, 607, 690, 661, 685, 523, 443, 534, 548, 469, 372, 621, 647, 357, 358, 382, 545, 364, 528, 500, 434, 370, 486, 435, 400, 494, 574, 608, 637, 584, 428, 439, 617, 656, 482, 582, 559, 611, 508, 671, 395, 669, 616, 471, 660, 683, 620, 695, 353, 362, 412, 512, 563, 458, 432, 425, 693, 418, 636, 553, 633, 578};
//		for (int i = 0; i < tester.length; i++) {
//			destinationIndices.add(tester[i]);
//		}
		
		for (int i = nodesToRead; i < nodesToRead * 2; i++) {
			destinationIndices.add(i);
		}

		Collections.shuffle(destinationIndices);
		
		return destinationIndices;
	}

	public double execute(String filename, int nodesToRead, String type,
			ArrayList<Integer> destinationIndices) {
		double result = 0.0;

		switch (filename) {
		case "synthetic":
			costMatrix = new SyntheticData().generateSynthetic(nodesToRead);
			break;
		case "synthetic2D":
			costMatrix = new SyntheticData().generateSynthetic2D(nodesToRead);
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
			result = computeOnlineMatching(filename, nodesToRead,
					destinationIndices);
			break;
		case "greedy":
			result = computeGreedyMatching(filename, nodesToRead,
					destinationIndices);
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

//		System.out.println("Offline Matching Digraph: "
//				+ offlineMatching.toString());

		double totalCost = calculateTotalCost(offlineMatching);
//		System.out.println("TOTAL OFFLINE COST: " + totalCost);

		return totalCost;
	}

	// TODO
	public double computeOnlineMatching(String filename, int numSetA,
			ArrayList<Integer> destinationIndices) {
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

//		System.out.println("Destination Order: "
//				+ destinationIndices.toString());

		/*
		 * Core of the algorithm
		 */
		while (matching.size() < numSetA) {

			int destinationIndex = destinationIndices.get(index);

			tempMatrix = normalizeMatrix(destinationIndex - numSetA,
					tempMatrix, true);

			original = constructDigraphFromMatrix(tempMatrix);

			ArrayList<DirectedEdge> bestPath = new ArrayList<DirectedEdge>();
			Iterator<DirectedEdge> iter = null;
			
			double minPath = Double.MAX_VALUE;

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

			int source = bestPath.get(0).from();
			int destination = bestPath.get(bestPath.size() - 1).to();

			// New iteration DiGraph that will have updated edges
			EdgeWeightedDigraph nextIterationGraph = new EdgeWeightedDigraph(
					numSetA + costMatrix[index].length);

			// Update matchings and new DiGraph
			for (DirectedEdge e : original.edges()) {
				DirectedEdge edgeToAdd = null;
				// If this edge is part of the matching
				if (containsEdge(bestPath, e)) {

					if (e.weight() < 0.0) {
						edgeToAdd = new DirectedEdge(e.to(), e.from(), -1.0
								* constant * e.weight());
					} else {
						edgeToAdd = new DirectedEdge(e.to(), e.from(),
								(-1.0 * e.weight()) / constant);
					}

					nextIterationGraph.addEdge(edgeToAdd);
					if (edgeToAdd.weight() <= 0.0) {
						if (!matching.contains(e.from())) {
							matching.add(e.from());

							onlineMatching.add(new DirectedEdge(destination,
									source, costMatrix[source][destination
											- costMatrix[index].length]));
						}
					}
				} else {
					edgeToAdd = new DirectedEdge(e.from(), e.to(),
							e.weight());
					nextIterationGraph.addEdge(edgeToAdd);
				}
				
				// Update matrix values accordingly
				int x = edgeToAdd.from();
				int y = edgeToAdd.to();
				
				if (x >= numSetA) {
					tempMatrix[y][x - numSetA] = edgeToAdd.weight();
				} else {
					tempMatrix[x][y - numSetA] = edgeToAdd.weight();
				}
				
			}

			// Increment index if not processing negative cycles, else set index
			// to last source node
			// if (!runNegativeCycleIndices) {
			index++;
			// } else {
			// index = numSetA;
			// }
		}

//		System.out.println("Online Digraph: " + onlineMatching.toString());

		double totalCost = calculateTotalCost(onlineMatching);
		
		int numEdges = 0;
		for (DirectedEdge e : onlineMatching) {
			if (e.weight() > (totalCost/numSetA)*0.2) {
				numEdges++;
			}
		}
		System.out.println("NUMBER EDGES ONLINE > AVERAGE: " + numEdges);
//		System.out.println("TOTAL ONLINE NET COST: " + totalCost);
		
		temp = totalCost;

		return totalCost;

	}
	
	public double computeGreedyMatching(String filename, int numSetA, ArrayList<Integer> destinationIndices) {
		ArrayList<DirectedEdge> matching = new ArrayList<DirectedEdge>();
		ArrayList<Integer> fromMatchings = new ArrayList<Integer>();
		// Index of the current source node being processed
		int index = 0;
		
		while (matching.size() < numSetA) {
			//Get row of smallest element in column of destination index
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
		
		int numEdges = 0;
		double minCost = Double.MAX_VALUE;
		for (DirectedEdge e : matching) {
			if (e.weight() < minCost) {
				minCost = e.weight();
			}
			if (e.weight() > (temp/numSetA)*0.2) {
				numEdges++;
			}
		}
		System.out.println("GREEDY MIN EDGE WEIGHT: " + minCost);
		System.out.println("NUMBER EDGES GREEDY > AVERAGE: " + numEdges);
//		System.out.println("TOTAL GREEDY NET COST: " + totalCost);
		
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

	private double[][] normalizeMatrix(int column, double[][] tempMatrix,
			boolean hasConstant) {
		for (int i = 0; i < tempMatrix.length; i++) {
			for (int j = 0; j < tempMatrix[i].length; j++) {
				if (/* tempMatrix[i][j] == 100000.0 && */j == column) {
					if (hasConstant) {
						tempMatrix[i][j] = constant * costMatrix[i][j];
					} else {
						tempMatrix[i][j] = costMatrix[i][j];
					}

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
