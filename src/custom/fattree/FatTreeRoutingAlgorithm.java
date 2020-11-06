package custom.fattree;

import javatuples.*;
import network.elements.Packet;
import network.entities.Host;
import network.entities.Switch;
import routing.RoutingAlgorithm;
import routing.RoutingPath;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import infrastructure.entity.Node;

public class FatTreeRoutingAlgorithm implements RoutingAlgorithm, Cloneable {
	public FatTreeGraph G;
	public Map<Pair<Integer, Integer>, RoutingPath> precomputedPaths = new HashMap<>();
	public Map<Integer, Map<Integer, Integer>> suffixTables = new HashMap<>();

	public Map<Integer, Map<Integer, Integer>> getSuffixTables() {
		return suffixTables;
	}

	public void setSuffixTables(Map<Integer, Map<Integer, Integer>> suffixTables) {
		this.suffixTables = suffixTables;
	}

	private Map<Integer, Map<Triplet<Integer, Integer, Integer>, Integer>> prefixTables = new HashMap<>();

	public void setCorePrefixTables(Map<Integer, Map<Pair<Integer, Integer>, Integer>> corePrefixTables) {
		this.corePrefixTables = corePrefixTables;
	}

	public Map<Integer, Map<Triplet<Integer, Integer, Integer>, Integer>> getPrefixTables() {
		return prefixTables;
	}

	public void setPrefixTables(Map<Integer, Map<Triplet<Integer, Integer, Integer>, Integer>> prefixTables) {
		this.prefixTables = prefixTables;
	}

	private Map<Integer, Map<Pair<Integer, Integer>, Integer>> corePrefixTables = new HashMap<>();

	public FatTreeRoutingAlgorithm(FatTreeGraph G, boolean precomputed) {
		this.G = G;
		buildTables();
		if (precomputed) {
			List<Integer> hosts = G.hosts();
			for (int i = 0; i < hosts.size() - 1; i++) {
				for (int j = i + 1; j < hosts.size(); j++) {
					int source = hosts.get(i);
					int destination = hosts.get(j);
					path(source, destination);
				}
			}
		}
	}

	/**
	 * This method is used to build suffix table for edge switches
	 * 
	 * @param k          This is the parameter to organize a k-ary fat-tree topology
	 * @param numEachPod This is the number of nodes in a pod
	 */

	private void edgeSwitches(int k, int numEachPod) {

		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int e = 0; e < k / 2; e++) {
				int edgeSwitch = offset + k * k / 4 + e;
				// create suffix table
				HashMap<Integer, Integer> suffixTable = new HashMap<>();
				for (int suffix = 2; suffix <= k / 2 + 1; suffix++) {
					int agg = offset + k * k / 4 + (e + suffix - 2) % (k / 2) + (k / 2);
					suffixTable.put(suffix, agg);
				}
				suffixTables.put(edgeSwitch, suffixTable);
			}
		}
	}

	/**
	 * This method is used to build suffix and prefix table for aggregation switches
	 * 
	 * @param k          This is the parameter to organize a k-ary fat-tree topology
	 * @param numEachPod This is the number of nodes in a pod
	 */

	private void aggSwitches(int k, int numEachPod) {

		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int a = 0; a < k / 2; a++) {
				int aggSwitch = offset + k * k / 4 + k / 2 + a;

				// create suffix table
				Map<Integer, Integer> suffixTable = new HashMap<>();
				for (int suffix = 2; suffix <= k / 2 + 1; suffix++) {
					int core = a * k / 2 + (suffix + a - 2) % (k / 2) + numEachPod * k;
					suffixTable.put(suffix, core);
				}
				// inject to the behavior
				suffixTables.put(aggSwitch, suffixTable);

				// create prefix table
				Map<javatuples.Triplet<Integer, Integer, Integer>, Integer> prefixTable = new HashMap<>();

				for (int e = 0; e < k / 2; e++) {
					int edgeSwitch = offset + k * k / 4 + e;
					prefixTable.put(new javatuples.Triplet<>(10, p, e), edgeSwitch);
				}
				prefixTables.put(aggSwitch, prefixTable);

			}
		}
	}

	/**
	 * This method is used to build suffix table for core switches
	 * 
	 * @param k          This is the parameter to organize a k-ary fat-tree topology
	 * @param numEachPod This is the number of nodes in a pod
	 */

	private void coreSwitches(int k, int numEachPod) {

		for (int c = 0; c < k * k / 4; c++) {
			int core = k * k * k / 4 + k * k + c;

			// build core prefix
			HashMap<Pair<Integer, Integer>, Integer> corePrefixTable = new HashMap<>();
			for (int p = 0; p < k; p++) {
				int offset = numEachPod * p;
				int agg = (c / (k / 2)) + k / 2 + k * k / 4 + offset;
				corePrefixTable.put(new Pair<>(10, p), agg);
			}
			corePrefixTables.put(core, corePrefixTable);
		}
		System.out.println();
	}

	/**
	 * This method is used to build prefix - suffix routing table
	 */

	private void buildTables() {

		int k = G.getK(); // This parameter is used organize a k-ary fat-tree
		int numEachPod = k * k / 4 + k; /*
										 * This parameter is the number of nodes in a pod (including (k*k/4)hosts, (k/2)
										 * edge switches and (k/2) aggregation switches)
										 */
		edgeSwitches(k, numEachPod);
		aggSwitches(k, numEachPod);
		coreSwitches(k, numEachPod);

	}

	/**
	 * This method is used to find the next node in the path when the current switch
	 * is core switch
	 * 
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 * @return the prefix in corePrefixTable pointing to the destination node
	 */

	private int coreType(int current, int destination) {
		Address address = G.getAddress(destination);
		Pair<Integer, Integer> prefix = new Pair<>(address._1, address._2);
		Map<Pair<Integer, Integer>, Integer> corePrefixTable = corePrefixTables.get(current);
		return corePrefixTable.get(prefix);
	}

	/**
	 * This method is used to find the next node in the path when the current switch
	 * is aggregation switch
	 * 
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 * @return the prefix or suffix pointing to the destination node
	 */

	protected int aggType(int current, int destination) {
		Address address = G.getAddress(destination);

		Triplet<Integer, Integer, Integer> prefix = new Triplet<>(address._1, address._2, address._3);
		int suffix = address._4;

		Map<Triplet<Integer, Integer, Integer>, Integer> prefixTable = prefixTables.get(current);
		Map<Integer, Integer> suffixTable = suffixTables.get(current);

		if (prefixTable.containsKey(prefix)) {
			return prefixTable.get(prefix);
		} else {
			return suffixTable.get(suffix);
		}
	}

	/**
	 * This method is used to find the next node in the path when the current switch
	 * is edge switch
	 * 
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 * @return the suffix pointing to the destination node
	 */

	protected int edgeType(int current, int destination) {
		Address address = G.getAddress(destination);
		int suffix = address._4;
		Map<Integer, Integer> suffixTable = suffixTables.get(current);
		return suffixTable.get(suffix);
	}

	/**
	 * This method is used to find the next node from the current node in the path
	 * Time complexity: O(1)
	 * 
	 * @param source      This is the source node
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 */
	@Override
	public int next(int source, int current, int destination) {

		if (G.isHostVertex(current)) {
			return G.adj(current).get(0);
		} else if (G.adj(current).contains(destination)) { // If the current node is also the destination node
			return destination;
		} else {
			int type = G.switchType(current); // To find out what type of switch is the current switch

			if (type == FatTreeGraph.CORE) { // If the current switch is core switch
				return coreType(current, destination);
			} else if (type == FatTreeGraph.AGG) { // If the current switch is aggregation switch
				return aggType(current, destination);
			} else { // If the current switch is edge switch
				return edgeType(current, destination);
			}

		}

	}

	@Override
	public RoutingPath path(int source, int destination) {
		return null;
	}

	public int next(Packet packet, Node node) {
		return next(packet.getSource(), node.getId(), packet.getDestination());
	}

	public RoutingAlgorithm build(Node node) throws CloneNotSupportedException {
		RoutingAlgorithm ra = (RoutingAlgorithm) this.clone();
		if (node instanceof Host) {
			((FatTreeRoutingAlgorithm) ra).setCorePrefixTables(null);
			((FatTreeRoutingAlgorithm) ra).setPrefixTables(null);
			((FatTreeRoutingAlgorithm) ra).setSuffixTables(null);
		}
		if (node instanceof Switch) {
			int id = ((Switch) node).getId();
			int type = G.switchType(id);
			if (type == FatTreeGraph.AGG) {
				((FatTreeRoutingAlgorithm) ra).corePrefixTables = null;
			}
			if (type == FatTreeGraph.EDGE) {
				((FatTreeRoutingAlgorithm) ra).prefixTables = null;
				((FatTreeRoutingAlgorithm) ra).corePrefixTables = null;
			}
			if (type == FatTreeGraph.CORE) {
				((FatTreeRoutingAlgorithm) ra).prefixTables = null;
				((FatTreeRoutingAlgorithm) ra).suffixTables = null;
			}
		}
		return ra;
	}

	public void update(Packet p, Node node) {

	}

}
