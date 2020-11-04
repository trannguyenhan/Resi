package custom.fattree;

import graph.Graph;
import java.util.ArrayList;
import java.util.List;

public class FatTreeGraph extends Graph {
	public static final int CORE = 1;
	public static final int AGG = 2;
	public static final int EDGE = 3;
	private final int numServers;
	private final int numPodSwitches;
	private final int numCores;
	private final int k;
	private Address[] address;

	private List<Integer> switches;
	private List<Integer> hosts;

	public FatTreeGraph(int k) {
		if (k % 2 == 1)
			throw new IllegalArgumentException("K must be even");
		if (k > 256)
			throw new IllegalArgumentException("K must less than 256");

		this.k = k;
		this.numServers = k * k * k / 4;
		this.numPodSwitches = k * k;
		this.numCores = k * k / 4;
		this.V = numServers + numPodSwitches + numCores;
		this.E = 0;
		adj = (List<Integer>[]) new List[V];

		for (int v = 0; v < V; v++) {
			adj[v] = new ArrayList<Integer>();
		}

		int numEachPod = k * k / 4 + k; // the number of nodes in a pod, each pod has k^2/4 servers and k switches
		buildEdge(numEachPod);
		buildAddress();
	}

	/**
	 * This method is used to connect edge switches to servers. In each pod, there
	 * are k/2 edge switches, each switch is directly connected to k/2 servers
	 * 
	 * @param offset
	 */
	private void connectEdgeServer(int offset) {
		// between edge and server
		for (int e = 0; e < k / 2; e++) {
			int edgeSwitch = offset + k * k / 4 + e;
			for (int s = 0; s < k / 2; s++) {
				int server = offset + e * k / 2 + s;
				addEdge(edgeSwitch, server);
			}
		}
	}

	/**
	 * This method is used to connect edge switches to aggregation switches. In each
	 * pod, there are k/2 edge switches, each switch is directly connected to k/2
	 * aggregation switches.
	 * 
	 * @param offset
	 */
	private void connectEdgeAgg(int offset) {

		for (int e = 0; e < k / 2; e++) {
			int edgeSwitch = offset + k * k / 4 + e;
			for (int a = k / 2; a < k; a++) {
				int aggSwitch = offset + k * k / 4 + a;
				addEdge(edgeSwitch, aggSwitch);
			}
		}
	}

	/**
	 * This method is used to connect aggregation switches to core switches. There
	 * are (k/2)^2 core switches. Consecutive ports in the aggregation layer of each
	 * pod switch are connected to core switches on (k/2) strides
	 * 
	 * @param offset
	 */
	private void connectAggCore(int offset) {

		for (int a = 0; a < k / 2; a++) {
			int aggSwitch = offset + k * k / 4 + k / 2 + a;
			for (int c = 0; c < k / 2; c++) {
				int coreSwitch = a * k / 2 + c + numPodSwitches + numServers;
				addEdge(aggSwitch, coreSwitch);
			}
		}
	}

	/**
	 * This method is used to build connection among nodes in the network
	 * 
	 * @param numEachPod This is the number of nodes in a pod
	 */
	private void buildEdge(int numEachPod) {
		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			connectEdgeServer(offset); // Connect edge switches to servers.
			connectEdgeAgg(offset); // Connect aggregation switches to edge switches
			connectAggCore(offset); // Connect core switches to aggregation switches
		}
	}

	/**
	 * This method is used to give addresses for core switches The core switches are
	 * given addresses of the form "10.k.j.i", where 'k' denotes the k-ary fat-tree
	 * topology, and 'j', 'i' denote that switch's coordinates in the (k/2)^2 core
	 * switch grid (each in [1,(k/2)], starting from top-left)
	 */
	private void buildCoreAddress() {

		for (int j = 1; j <= k / 2; j++) {
			for (int i = 1; i <= k / 2; i++) {
				int offset = numPodSwitches + numServers;
				int switchId = offset + (j - 1) * k / 2 + i - 1;
				address[switchId] = new Address(10, k, j, i);
			}
		}
	}

	/**
	 * This method is used to give addresses for pod switches The pod switches are
	 * given addresses of the form "10.p.s.1", where 'p' denotes the pod number (in
	 * [0, k - 1]), and 's' denotes the position of that switch in the pod (in [0, k
	 * - 1], starting from the left to the right, bottom to top)
	 * 
	 * @param numEachPod This is the number of nodes in a pod
	 */
	private void buildPodAddress(int numEachPod) {

		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int s = 0; s < k; s++) {
				int switchId = offset + k * k / 4 + s;
				address[switchId] = new Address(10, p, s, 1);
			}
		}
	}

	/**
	 * This method is used to give addresses for hosts The address of a host follows
	 * from the pod switch to which it is connected Hosts are given addresses of the
	 * form "10.p.e.h", where 'p' denotes the pod number (in [0, k - 1]), and 'e'
	 * denotes the position of edge switch in the pod (in [0, k/2 - 1]), and 'h'
	 * denotes the host's position in that subnet (in [2, k/2 + 1]), starting from
	 * left to right
	 * 
	 * @param numEachPod This is the number of nodes in a pod
	 */
	private void buildHostAddress(int numEachPod) {

		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int e = 0; e < k / 2; e++) {
				for (int h = 2; h <= k / 2 + 1; h++) {
					int serverId = offset + e * k / 2 + h - 2;
					address[serverId] = new Address(10, p, e, h);
				}
			}
		}
	}

	/**
	 * This method is used to give addresses for all nodes in the network, including
	 * core switches, pop switches (aggregation switches, edge switches) and hosts
	 */
	private void buildAddress() {
		this.address = new Address[V];
		int numEachPod = k * k / 4 + k; // the number of nodes in a pod

		buildCoreAddress(); // give addresses for core switches
		buildPodAddress(numEachPod); // give addresses for pod switches
		buildHostAddress(numEachPod); // give addresses for hosts
	}

	public int getK() {
		return k;
	}

	public Address getAddress(int u) {
		return address[u];
	}

	@Override
	public List<Integer> hosts() {
		if (hosts != null)
			return hosts;

		hosts = new ArrayList<>();

		int numEachPod = k * k / 4 + k;
		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int e = 0; e < k / 2; e++) {
				for (int h = 2; h <= k / 2 + 1; h++) {
					int serverId = offset + e * k / 2 + h - 2;
					hosts.add(serverId);
				}
			}
		}

		return hosts;
	}

	@Override
	public List<Integer> switches() {
		if (switches != null)
			return switches;
		switches = new ArrayList<>();

		// add pod's switches
		int numEachPod = k * k / 4 + k;
		for (int p = 0; p < k; p++) {
			int offset = numEachPod * p;
			for (int s = 0; s < k; s++) {
				int switchId = offset + k * k / 4 + s;
				switches.add(switchId);
			}
		}

		// add core switches
		for (int j = 1; j <= k / 2; j++) {
			for (int i = 1; i <= k / 2; i++) {
				int offset = numPodSwitches + numServers;
				int switchId = offset + (j - 1) * k / 2 + i - 1;
				switches.add(switchId);
			}
		}

		return switches;
	}

	public boolean isHostVertex(int u) {
		if (u >= numServers + numPodSwitches)
			return false;
		int offset = u % (k * k / 4 + k);
		return offset < k * k / 4;
	}

	public boolean isSwitchVertex(int u) {
		return !isHostVertex(u);
	}

	public int switchType(int u) {
		int numEachPod = k * k / 4 + k;
		if (u >= k * numEachPod)
			return CORE;
		else {
			int os = u % numEachPod;
			if (os >= k * k / 4 + k / 2)
				return AGG;
			else
				return EDGE;
		}
	}
}
