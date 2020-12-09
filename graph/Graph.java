package graph;

import common.StdOut;
import java.util.*;

public abstract class Graph {
	protected int V;
	protected int E;
	protected List<Integer>[] adj;

	public void addEdge(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		E++;
		adj[v].add(w);
		adj[w].add(v);
	}

	public void removeEdge(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		E--;
		adj[v].remove((Object) w);
		adj[w].remove((Object) v);
	}

	private void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
	}

	public boolean hasEdge(int u, int v) {
		return adj[u].contains(v);
	}

	public int V() {
		return V;
	}

	public List<Integer> adj(int v) {
		return adj[v];
	}

	public int degree(int u) {
		return adj[u].size();
	}

	public abstract List<Integer> hosts();

	public abstract List<Integer> switches();

	public abstract boolean isHostVertex(int v);

	public abstract boolean isSwitchVertex(int v);

	public double pathCableLength(List<Integer> path) {
		return 0;
	}
	
	public List<Integer> shortestPath(int u, int v) {
		Queue<Integer> queue = new LinkedList<Integer>();
		List<Integer> path = new ArrayList<>();
		boolean[] visited = new boolean[this.V];
		int[] trace = new int[this.V];
		queue.add(u);
		visited[u] = true;
		trace[u] = -1;
		while (!queue.isEmpty()) {
			int uNode = queue.remove();
			if (uNode == v) {
				addNodeToPath(path, v, trace);
				break;
			}
			addVNodeToQueue(visited, uNode, trace, queue);
		}
		return path;
	}
	
	/**
	 * If vNode is not visited and it is a vertex switch, add it to queue
	 * 
	 * @param visited
	 * @param uNode
	 * @param trace
	 * @param queue
	 */
	private void addVNodeToQueue(boolean[] visited, int uNode, int[] trace, Queue<Integer> queue) {
		
		for (int vNode : this.adj(uNode)) {
			if (!visited[vNode] && isSwitchVertex(vNode)) {
				visited[vNode] = true;
				trace[vNode] = uNode;
				queue.add(vNode);
			}
		}
	}
	
	/**
	 * This method is used to add a node to path
	 * 
	 * @param path This is the shortest path to send packet from a node to another node 
	 * @param v 
	 * @param trace If v is visited, trace[v] = -1
	 */
	private void addNodeToPath(List<Integer> path, int v, int[] trace) {
		path.add(v);
		
		while (trace[v] != -1) {
			v = trace[v];
			path.add(v);
		}
		
		Collections.reverse(path);
	}

	public Map<Integer, List<Integer>> shortestPaths(int u) {
		Queue<Integer> queue = new LinkedList<Integer>();
		boolean[] visited = new boolean[this.V];
		int[] trace = new int[this.V];
		queue.add(u);
		visited[u] = true;
		trace[u] = -1;
		while (!queue.isEmpty()) {
			int uNode = queue.remove();
			addVNodeToQueue(visited, uNode, trace, queue);
		}

		Map<Integer, List<Integer>> paths = new HashMap<>();
		for (int node : this.switches()) {
			List<Integer> path = new ArrayList<>();
			int v = node;
			addNodeToPath(path, v, trace);
			paths.put(node, path);
		}
		return paths;
	}

	public Map<Integer, Map<Integer, List<Integer>>> allShortestPaths() {
		Map<Integer, Map<Integer, List<Integer>>> paths = new HashMap<>();

		Queue<Integer> queue = new LinkedList<Integer>();
		boolean[] visited = new boolean[this.V];
		int[] trace = new int[this.V];

		for (int u : switches()) {
			queue.clear();
			Arrays.fill(visited, false);
			queue.add(u);
			visited[u] = true;
			trace[u] = -1;
			while (!queue.isEmpty()) {
				int uNode = queue.remove();
				addVNodeToQueue(visited, uNode, trace, queue);
			}
			paths.put(u, new HashMap<>());
			for (int node : this.switches()) {
				List<Integer> path = new ArrayList<>();
				int v = node;
				addNodeToPath(path, v, trace);
				paths.get(u).put(node, path);
			}
			StdOut.printf("Done for %d\n", u);
		}
		return paths;
	}

}
