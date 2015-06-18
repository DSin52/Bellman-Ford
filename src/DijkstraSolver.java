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

	//testing only
	private static final Graph.Edge[] GRAPH1 = { new Graph.Edge(1, 2, 7),
			new Graph.Edge(1, 3, 9), new Graph.Edge(1, 6, 14),
			new Graph.Edge(2, 3, 10), new Graph.Edge(2, 4, 15),
			new Graph.Edge(3, 4, 11), new Graph.Edge(3, 6, 2),
			new Graph.Edge(4, 5, 6), new Graph.Edge(5, 6, 9), };

	public static Graph.Edge[] graph;

	public static ArrayList<ArrayList<DirectedEdge>> execD(Graph.Edge[] curGraph,
			int source) {
		graph = curGraph;
		Graph g = new Graph(graph);
		g.dijkstra(source);
		return g.printAllPaths();
	}

	public static void main(String args[]) {
		// testing only
		Graph g = new Graph(GRAPH1);
		g.dijkstra(1);
		ArrayList<ArrayList<DirectedEdge>> temp = g.printAllPaths();
		System.out.println(temp.size());
		for (int i = 0; i < temp.size(); i++) {
			System.out.println(temp.get(i).size());
			System.out.println(temp.get(i).toString());
		}
	}
}
