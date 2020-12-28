package network;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
	private Map<Integer, Integer> table;

	public RoutingTable() {
		table = new HashMap<>();
	}

	public Map<Integer, Integer> getTable() {
		return table;
	}

	public void addRoute(int destination, int nextHop) {
		table.put(destination, nextHop);
	}

	public int getNextNode(int u) {
		return table.get(u);
	}

	public int size() {
		return table.size();
	}
}
