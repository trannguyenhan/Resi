package custom.fattree;

import java.util.HashMap;
import java.util.Map;
import config.Constant;
import infrastructure.entity.Node;
import javatuples.Pair;
import network.elements.Packet;
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

	/**
	 * This method is used to find the next node from the current node in the path
	 * 
	 * @param source      This is the source node
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 */
	@Override
	public int next(int source, int current, int destination) {

		if (G.isHostVertex(current)) {
			return G.adj(current).get(0);
		} else if (G.adj(current).contains(destination)) {
			return destination;
		} else {
			int type = G.switchType(current);

			if (type == FatTreeGraph.CORE) {
				return super.next(source, current,
						destination); /*
										 * Find the next node in the path when the current switch is core switch
										 */
			} else if (type == FatTreeGraph.AGG) {
				return aggType(current,
						destination); /*
										 * Find the next node in the path when the current switch is aggregation switch
										 */
			} else {
				return edgeType(current,
						destination); /*
										 * Find the next node in the path when the current switch is edge switch
										 */
			}
		}
	}

	/**
	 * This method is used to find the next node to send the packet
	 * 
	 * @param packet This is the packet which needs to be sent
	 * @param node   This is the current node where the packet stays
	 */
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
				return super.next(source, current,
						destination); /*
										 * Find the next node in the path when the current switch is core switch
										 */
			} else {
				if (flowTable.isEmpty()) {
				}
				if (type == FatTreeGraph.AGG) {
					return aggType(current, destination); /*
															 * Find the next node in the path when the current switch is
															 * aggregation switch
															 */
				} else {
					return edgeType(current,
							destination); /*
											 * Find the next node in the path when the current switch is edge switch
											 */
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

	/**
	 * This method is used to update the result of routing table
	 * 
	 * @param p    This is the packet which needs to be sent
	 * @param node This is the current node where the packet stays
	 */
	@Override
	public void update(Packet p, Node node) {
		int src = p.getSource();
		int dst = p.getDestination();
		int currentTime = (int) node.physicalLayer.simulator.time();
		if (currentTime - time >= Constant.TIME_REARRANGE) {
			time = currentTime;
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
