import java.io.*;
import java.util.*;

/*
 * Original code from http://rosettacode.org/wiki/Dijkstra's_algorithm#Java
 * Taken from wikipedia algorithm and converted to full java source
 * 
 * My changes include modifying the solver to be used as an object
 * and returning all shortest paths from current node to all
 * other nodes
 */
public class DijkstraSolver {
	/*
	 * private static final Graph.Edge[] GRAPH1 = { new Graph.Edge("a", "b", 7),
	 * new Graph.Edge("a", "c", 9), new Graph.Edge("a", "f", 14), new
	 * Graph.Edge("b", "c", 10), new Graph.Edge("b", "d", 15), new
	 * Graph.Edge("c", "d", 11), new Graph.Edge("c", "f", 2), new
	 * Graph.Edge("d", "e", 6), new Graph.Edge("e", "f", 9), };
	 */

	public static Graph.Edge[] graph;

	public static ArrayList<ArrayList<String>> execD(Graph.Edge[] curGraph,
			String source) {
		graph = curGraph;
		Graph g = new Graph(graph);
		g.dijkstra(source);
		return g.printAllPaths();
	}
}

