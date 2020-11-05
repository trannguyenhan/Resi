package custom.fattree;

import java.util.HashMap;
import java.util.Map;

import config.Constant;
import infrastructure.entity.Node;
import javatuples.Pair;
import javatuples.Triplet;
import network.elements.Packet;
import network.layers.DataLinkLayer;
import routing.RoutingAlgorithm;

public class FatTreeFlowClassifier extends FatTreeRoutingAlgorithm {

	public Map<Pair<Integer, Integer>, Long> flowSizesPerDuration = new HashMap<>();
	public Map<Integer, Long> outgoingTraffic = new HashMap<Integer, Long>();
	public Map<Pair<Integer, Integer>, Long> flowTable = new HashMap<>();
	private int currentNode;

	public int getCurrentNode() {
		return currentNode;
	}

	public FatTreeFlowClassifier(FatTreeGraph G, boolean precomputed) {
		super(G, precomputed);

	}

	private int time = 0;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public int next(int source, int current, int destination) {
		if (G.isHostVertex(current)) {
			return G.adj(current).get(0);
		} else if (G.adj(current).contains(destination)) {
			return destination;
		} else {
			int type = G.switchType(current);
			if (type == FatTreeGraph.CORE) {

				return super.next(source, current, destination);

			} else if (type == FatTreeGraph.AGG) {
				Address address = G.getAddress(destination);

				Triplet<Integer, Integer, Integer> prefix = new Triplet<>(address._1, address._2, address._3);
				int suffix = address._4;

				Map<Triplet<Integer, Integer, Integer>, Integer> prefixTable = getPrefixTables().get(current);
				Map<Integer, Integer> suffixTable = suffixTables.get(current);

				if (prefixTable.containsKey(prefix)) {
					System.out.println(prefixTable.get(prefix));
					return prefixTable.get(prefix);
				} else {
					System.out.println(suffixTable.get(suffix));
					return suffixTable.get(suffix);
				}
			} else { // Edge switch
				Address address = G.getAddress(destination);
				int suffix = address._4;
				Map<Integer, Integer> suffixTable = suffixTables.get(current);
				System.out.println(suffixTable.get(suffix));
				return suffixTable.get(suffix);
			}

		}

	}

	@Override
	public int next(Packet packet, Node node) {
		int current = node.getId();
		int destination = packet.getDestination();
		int source = packet.getSource();

		if (G.isHostVertex(current)) {
			return G.adj(current).get(0);
		} else if (G.adj(current).contains(destination)) {
			return destination;
		} else {
			int type = G.switchType(current);
			if (type == FatTreeGraph.CORE) {
				return super.next(source, current, destination);
			} else {
				if (flowTable.isEmpty()) {

				}
				if (type == FatTreeGraph.AGG) {

					Address address = G.getAddress(destination);

					Triplet<Integer, Integer, Integer> prefix = new Triplet<>(address._1, address._2, address._3);
					int suffix = address._4;

					Map<Triplet<Integer, Integer, Integer>, Integer> prefixTable = getPrefixTables().get(current);
					Map<Integer, Integer> suffixTable = suffixTables.get(current);

					if (prefixTable.containsKey(prefix)) {
						return prefixTable.get(prefix);
					} else {
						return suffixTable.get(suffix);
					}
				} else { // Edge switch
					Address address = G.getAddress(destination);
					int suffix = address._4;

					Map<Integer, Integer> suffixTable = suffixTables.get(current);
					return suffixTable.get(suffix);
				}
			}

		}
	}

	@Override
	public RoutingAlgorithm build(Node node) throws CloneNotSupportedException {
		currentNode = node.getId();
		RoutingAlgorithm ra = super.build(node);
		if (ra instanceof FatTreeFlowClassifier) {
			FatTreeFlowClassifier ftfc = (FatTreeFlowClassifier) ra;
			ftfc.outgoingTraffic = new HashMap<Integer, Long>();
			ftfc.flowSizesPerDuration = new HashMap<Pair<Integer, Integer>, Long>();
			ftfc.flowTable = new HashMap<Pair<Integer, Integer>, Long>();
			return ftfc;
		}
		return ra;
	}

	@Override
	public void update(Packet p, Node node) {
		int src = p.getSource();
		int dst = p.getDestination();
		int currentTime = (int) node.physicalLayer.simulator.time();
		if (currentTime - time >= Constant.TIME_REARRANGE) {
			time = currentTime;
			// Update the result of routing table here
			flowSizesPerDuration = new HashMap<Pair<Integer, Integer>, Long>();
		} else {
			Pair<Integer, Integer> flow = new Pair<>(src, dst);
			long value = p.getSize();
			if (flowSizesPerDuration.containsKey(flow)) {
				value += flowSizesPerDuration.get(flow);
			}
			flowSizesPerDuration.put(flow, value);
			value = p.getSize();
			int idNextNode = node.getId();
			if (outgoingTraffic.containsKey(idNextNode)) {
				value += outgoingTraffic.get(idNextNode);
			}
			outgoingTraffic.put(idNextNode, value);
		}
	}
}
