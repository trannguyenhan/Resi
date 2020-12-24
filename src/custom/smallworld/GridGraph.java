package custom.smallworld;

import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GridGraph extends Graph {
	public static final int HOST_PER_SWITCH = 1;
	private List<Integer> switches;
	private List<Integer> hosts;

	protected final int nRow;
	protected final int nCol;
	protected int nFlat;

	private final String baseType;
	private final int nHost;
	private final int nSwitch;

	public GridGraph(GridGraph g) {
		this.V = g.V;
		this.nRow = g.nRow;
		this.nCol = g.nCol;
		this.baseType = g.baseType;
		this.nHost = g.nHost;
		this.nSwitch = g.nSwitch;
		this.E = g.E;

		adj = new List[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<Integer>();
			// reverse so that adjacency list is in same order as original
			Stack<Integer> reverse = new Stack<>();
			for (int w : g.adj[v]) {
				reverse.push(w);
			}
			for (int w : reverse) {
				adj[v].add(w);
			}
		}
	}

	public GridGraph(int nRow, int nCol, int nFlat, String baseType) {
		this.nRow = nRow;
		this.nCol = nCol;
		this.baseType = baseType;

		this.nFlat = nFlat;
		this.nSwitch = nRow * nCol * nFlat;
		this.nHost = nSwitch * HOST_PER_SWITCH;

		this.V = nHost + nSwitch;
		adj = new List[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<Integer>();
		}

		// Add edges between hosts and switch
		int hostId = nSwitch;
		for (int i = 0; i < nSwitch; i++) {
			for (int j = 0; j < HOST_PER_SWITCH; j++) {
				addEdge(i, hostId);
				hostId++;
			}
		}
	}

	public GridGraph(int nRow, int nCol, String baseType) {
		this.nRow = nRow;
		this.nCol = nCol;
		this.baseType = baseType;
		this.nSwitch = nRow * nCol;
		this.nHost = nSwitch * HOST_PER_SWITCH;

		this.V = nHost + nSwitch;
		adj = new List[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<Integer>();
		}

		// Add edges between switches
		if (baseType.equals("grid")) {
			for (int i = 0; i < nSwitch; i++) {
				int col = i % nCol;
				int row = i / nCol;
				addGridEdges(i, row, col);
			}
		} else if (baseType.equals("torus")) {
			for (int i = 0; i < nSwitch; i++) {
				int col = i % nCol;
				int row = i / nCol;
				addTorusEdges(i, row, col);
			}
		}

		// Add edges between hosts and switch
		int hostId = nSwitch;
		for (int i = 0; i < nSwitch; i++) {
			for (int j = 0; j < HOST_PER_SWITCH; j++) {
				addEdge(i, hostId);
				hostId++;
			}
		}
	}

	@Override
	public List<Integer> hosts() {
		if (hosts != null)
			return hosts;

		hosts = new ArrayList<>();
		for (int i = nSwitch; i < V; i++)
			hosts.add(i);

		return hosts;
	}

	@Override
	public List<Integer> switches() {
		if (switches != null)
			return switches;

		switches = new ArrayList<>();
		for (int i = 0; i < nSwitch; i++)
			switches.add(i);

		return switches;
	}

	@Override
	public boolean isHostVertex(int v) {
		return v >= nSwitch;
	}

	@Override
	public boolean isSwitchVertex(int v) {
		return v < nSwitch;
	}

	@Override
	public double pathCableLength(List<Integer> path) {
		double length = 0;

		for (int i = 0; i < path.size() - 1; i++)
			length += euclidDistance(path.get(i), path.get(i + 1));
		return length;
	}

	public List<Integer> getHostsOfSwitch(int sid) {
		List<Integer> result = new ArrayList<>();
		for (int i : adj(sid)) {
			if (hosts().contains(i))
				result.add(i);
		}
		return result;
	}

	public int vertexIndex(int row, int col) {
		return row * this.nCol + col;
	}

	private void addGridEdges(int curr, int row, int col) {
		if (col < nCol - 1)
			addEdge(curr, vertexIndex(row, col + 1));
		if (row < nRow - 1)
			addEdge(curr, vertexIndex(row + 1, col));
	}

	private void addTorusEdges(int curr, int row, int col) {
		addEdge(curr, vertexIndex(row, (col + 1) % nCol));
		addEdge(curr, vertexIndex((row + 1) % nRow, col));
	}

	public int distance(int u, int v) {
		if (baseType.equals("torus")) {
			int ux = u % nCol;
			int uy = u / nCol;
			int vx = v % nCol;
			int vy = v / nCol;

			int dx = Math.abs(ux - vx) <= nCol / 2 ? Math.abs(ux - vx) : nCol - Math.abs(ux - vx);
			int dy = Math.abs(uy - vy) <= nRow / 2 ? Math.abs(uy - vy) : nRow - Math.abs(uy - vy);

			return dx + dy;
		} else {
			int ux = u % nCol;
			int uy = u / nCol;
			int vx = v % nCol;
			int vy = v / nCol;

			return Math.abs(ux - vx) + Math.abs(uy - vy);
		}
	}

	public int manhattanDistance(int u, int v) {
		int ux = u % nCol;
		int uy = u / nCol;
		int vx = v % nCol;
		int vy = v / nCol;
		return Math.abs(ux - vx) + Math.abs(uy - vy);
	}

	public double euclidDistance(int u, int v) {
		int ux = u % nCol;
		int uy = u / nCol;
		int vx = v % nCol;
		int vy = v / nCol;
		return Math.sqrt(Math.pow(1.0 * ux - vx, 2) + Math.pow(1.0 * uy - vy, 2));
	}

	public int getnCol() {
		return nCol;
	}

	/**
	 * Returns a string representation of this graph.
	 *
	 * @return the number of vertices <em>V</em>, followed by the number of edges
	 *         <em>E</em>, followed by the <em>V</em> adjacency lists
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " vertices, " + E + " edges \n");
		int sumDegree = 0;

		for (int v = 0; v < V; v++) {
			s.append(String.format("%2d:", v));
			s.append(String.format(" degree = %2d -- ", degree(v)));
			sumDegree += degree(v);
			for (int w : adj[v]) {
				s.append(String.format(" %2d", w));
			}
			s.append("\n");
		}
		s.append("\n");
		s.append(String.format("Average degree = %f", 1.0 * sumDegree / V));
		return s.toString();
	}

	public double totalCableLength() {
		double totalLength = 0;
		for (int u : switches())
			for (int v : adj(u))
				if (isSwitchVertex(v) && u < v) {
					totalLength += euclidDistance(u, v);
				}

		return totalLength;
	}

	public void writeFileGeos(String fileName) {
		try {
			File file = new File(fileName);
			// creates the file
			file.createNewFile();

			FileWriter writer = new FileWriter(file);

			// Writes the content to the file
			writer.write(this.switches().size() + "\n");
			for (int i : this.switches()) {
				writer.write(i + " " + i / nCol + " " + i % nCol + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeFileEdges(String fileName) {
		try {
			File file = new File(fileName);
			// creates the file
			file.createNewFile();

			FileWriter writer = new FileWriter(file);

			int nEdge = 0;
			for (int i : this.switches()) {
				for (int j : this.adj(i)) {
					if (isSwitchVertex(j) && i < j)
						nEdge++;
				}
			}
			// Writes the content to the file
			writer.write(this.switches().size() + " " + nEdge + "\n");
			for (int i : this.switches()) {
				for (int j : this.adj(i)) {
					if (isSwitchVertex(j) && i < j)
						writer.write(i + " " + j + "\n");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
