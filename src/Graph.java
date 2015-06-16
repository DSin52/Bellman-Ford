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
	private final Map<String, Vertex> graph; // mapping of vertex names to
												// Vertex objects, built from a
												// set of Edges

	/** One edge of the graph (only used by Graph constructor) */
	public static class Edge {
		public final String v1, v2;
		public final int dist;

		public Edge(String v1, String v2, int dist) {
			this.v1 = v1;
			this.v2 = v2;
			this.dist = dist;
		}
	}

	/** One vertex of the graph, complete with mappings to neighbouring vertices */
	public static class Vertex implements Comparable<Vertex> {
		public final String name;
		public int dist = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
		public Vertex previous = null;
		public final Map<Vertex, Integer> neighbours = new HashMap<>();

		public Vertex(String name) {
			this.name = name;
		}

		private ArrayList<String> printPath() {
			ArrayList<String> curPath = new ArrayList<String>();
			Vertex v = this;
			while (v != v.previous) {
				curPath.add(v.name + "(" + v.dist + ")");
				v = v.previous;
			}
			curPath.add(this.name);
			Collections.reverse(curPath);
			return curPath;
		}

		public int compareTo(Vertex other) {
			return Integer.compare(dist, other.dist);
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
			// graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also
			// do this for an undirected graph
		}
	}

	/** Runs dijkstra using a specified source vertex */
	public void dijkstra(String startName) {
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
			for (Map.Entry<Vertex, Integer> a : u.neighbours.entrySet()) {
				v = a.getKey(); // the neighbour in this iteration

				final int alternateDist = u.dist + a.getValue();
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
	 * Prints a path from the source to the specified vertex
	 * 
	 * @return
	 * @return
	 */
	public ArrayList<String> printPath(String endName) {
		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n",
					endName);
			return null;
		}

		return (graph.get(endName).printPath());

	}

	/**
	 * Prints the path from the source to every vertex (output order is not
	 * guaranteed)
	 */
	public ArrayList<ArrayList<String>> printAllPaths() {
		ArrayList<ArrayList<String>> allPaths = new ArrayList<ArrayList<String>>();
		for (Vertex v : graph.values()) {
			allPaths.add(v.printPath());
		}
		for (int i = 0; i < allPaths.size(); i++) {
			System.out.println(allPaths.get(i).toString());
		}
		return allPaths;
	}
}