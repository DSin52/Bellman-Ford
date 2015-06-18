import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

/*
 * Original code from http://rosettacode.org/wiki/Dijkstra's_algorithm#Java
 * Taken from wikipedia algorithm and converted to full java source
 * 
 * My changes include modifying the solver to be used as an object
 * and returning all shortest paths from current node to all
 * other nodes
 */
public class Graph {
	private final Map<Integer, Vertex> graph; // mapping of vertex names to
												// Vertex objects, built from a
												// set of Edges

	/** One edge of the graph (only used by Graph constructor) */
	public static class Edge {
		public final int v1, v2;
		public final double dist;

		public Edge(int v1, int v2, double dist) {
			this.v1 = v1;
			this.v2 = v2;
			this.dist = dist;
		}
	}

	/** One vertex of the graph, complete with mappings to neighbouring vertices */
	public static class Vertex implements Comparable<Vertex> {
		public final int name;
		public double dist = Double.MAX_VALUE; // MAX_VALUE assumed to be infinity
		public Vertex previous = null;
		public final Map<Vertex, Double> neighbours = new HashMap<>();

		public Vertex(int name) {
			this.name = name;
		}

		private ArrayList<DirectedEdge> printPath() {
			ArrayList<DirectedEdge> curPath = new ArrayList<DirectedEdge>();
			Vertex v = this;
			while (v != v.previous) {
				DirectedEdge e = new DirectedEdge(v.previous.name, v.name, v.dist);
				curPath.add(e);
				v = v.previous;
			}
			Collections.reverse(curPath);
			return curPath;
		}

		public int compareTo(Vertex other) {
			return Double.compare(dist, other.dist);
		}
	}

	/** Builds a graph from a set of edges */
	public Graph(Edge[] edges) {
		graph = new HashMap<>(edges.length);

		// one pass to find all vertices
		for (Edge e : edges) {
			if (!graph.containsKey(e.v1))
				graph.put(e.v1, new Vertex(e.v1));
			if (!graph.containsKey(e.v2))
				graph.put(e.v2, new Vertex(e.v2));
		}

		// another pass to set neighbouring vertices
		for (Edge e : edges) {
			graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
		}
	}

	/** Runs dijkstra using a specified source vertex */
	public void dijkstra(int startName) {
		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n",
					startName);
			return;
		}
		final Vertex source = graph.get(startName);
		NavigableSet<Vertex> q = new TreeSet<>();

		// set-up vertices
		for (Vertex v : graph.values()) {
			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Integer.MAX_VALUE;
			q.add(v);
		}

		dijkstra(q);
	}

	/** Implementation of dijkstra's algorithm using a binary heap. */
	private void dijkstra(final NavigableSet<Vertex> q) {
		Vertex u, v;
		while (!q.isEmpty()) {

			u = q.pollFirst(); // vertex with shortest distance (first iteration
								// will return source)
			if (u.dist == Integer.MAX_VALUE)
				break; // we can ignore u (and any other remaining vertices)
						// since they are unreachable

			// look at distances to each neighbour
			for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
				v = a.getKey(); // the neighbour in this iteration

				final double alternateDist = u.dist + a.getValue();
				if (alternateDist < v.dist) { // shorter path to neighbour found
					q.remove(v);
					v.dist = alternateDist;
					v.previous = u;
					q.add(v);
				}
			}
		}
	}


	/**
	 * Prints the path from the source to every vertex (output order is not
	 * guaranteed)
	 */
	public ArrayList<ArrayList<DirectedEdge>> printAllPaths() {
		ArrayList<ArrayList<DirectedEdge>> allPaths = new ArrayList<ArrayList<DirectedEdge>>();
		for (Vertex v : graph.values()) {
			allPaths.add(v.printPath());
		}
		return allPaths;
	}
}