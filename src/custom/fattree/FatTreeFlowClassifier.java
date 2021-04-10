package custom.fattree;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import config.Constant;
import infrastructure.entity.Node;
import javatuples.Pair;
import network.elements.Packet;
import routing.RoutingAlgorithm;

public class FatTreeFlowClassifier extends FatTreeRoutingAlgorithm {

	public Map<Pair<Integer, Integer>, Long> flowSizesPerDuration = new HashMap<>();
	public Map<Integer, Long> outgoingTraffic = new HashMap<>();
	public Map<Pair<Integer, Integer>, Long> flowTable = new HashMap<>();
	private int currentNode;

	public int getCurrentNode() {
		return currentNode;
	}

	public FatTreeFlowClassifier(FatTreeGraph g, boolean precomputed) {
		super(g, precomputed);
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

		if (g.isHostVertex(current)) {
			return g.adj(current).get(0);
		} else if (g.adj(current).contains(destination)) {
			return destination;
		} else {
			int type = g.switchType(current);

			if (type == FatTreeGraph.CORE) {
				return super.next(source, current, destination); // Find the next node in the path when the current
																	// switch is core switch

			} else if (type == FatTreeGraph.AGG) {
				return aggType(current, destination); // Find the next node in the path when the current switch is
														// aggregation switch

			} else {
				return edgeType(current, destination); // Find the next node in the path when the current switch is edge
														// switch
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
		
		// flow = true : have classifier, flow = false : send package with default port
		return next(source, current, destination, true);
	}

	/*Add flow classifier - trannguyenhan write */
	public int next(int source, int current, int destination, boolean flow) {
		if(flow) {
			return incomingPacket(source, current, destination);
		} else {
			return next(source, current, destination);
		}
	}
	
	public static Map<Integer, Integer> seen = new HashMap<Integer, Integer>();
	public static Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> usePorts = 
			new HashMap<AbstractMap.SimpleEntry<Integer, Integer>, Integer>(); // each line connects 2 points representing 1 port
	
	private int k = g.getK();
	private int each_devices_in_hostpod = (k*k)/4+k;
	
	/*
	 * Check that host number a and b are on the same pod
	 */
	public boolean checkDevicesInPod(int a, int b) {
		int podA = a / each_devices_in_hostpod;
		int podB = b / each_devices_in_hostpod;

		if (podA == podB)
			return true;
		return false;
	}
	
	/* Flow classifier
	 * */
	public int incomingPacket(int source, int current, int destination) { // flow classifier
		int type = g.switchType(current);
		if(type == FatTreeGraph.CORE) {
			return next(source, current, destination);
		} if((type == FatTreeGraph.AGG || type == FatTreeGraph.EDGE) && checkDevicesInPod(current, destination)) {
			return next(source, current, destination);
		}
		
		// start algorithm flow classifier
		// hash(source, destination)
		int hashID = hash(source, destination, current);
		
		if(seen.containsKey(hashID)) {
			return seen.get(hashID);
		} else {
			List<Integer> listNeiborNodes = getNodeCanGo(g.adj(current), current, source, destination);
			
			int minFlow = Integer.MAX_VALUE;
			int minIndexFlow = 0;
			for(int i=listNeiborNodes.size()-1; i>=0; i--) {
				int tmpID = listNeiborNodes.get(i);
				
				if(usePorts.containsKey(new AbstractMap.SimpleEntry<>(current, tmpID))){ // check port is exits
					int tmp = usePorts.get(new AbstractMap.SimpleEntry<>(current, tmpID));
					if(tmp < minFlow) {
						minFlow = tmp;
						minIndexFlow = tmpID;
					}
				} else { // if port not exits, add port to usePorts with number of flow use is 0
					usePorts.put(new AbstractMap.SimpleEntry<>(current, tmpID), 0); 
					if(minFlow >= 0) {
						minFlow = 0;
						minIndexFlow = tmpID;
					}
				}
			}
			
			usePorts.put(new AbstractMap.SimpleEntry<>(current, minIndexFlow), 
					usePorts.get(new AbstractMap.SimpleEntry<>(current, minIndexFlow))+1);
			seen.put(hashID, minIndexFlow);
			
			return minIndexFlow;
		}
	}
	
	public int hash(int a, int b, int c) {
		return a * 11 + b * 17 + c * 31;
	}
	
	/* Removes the port that just sent the packet to the current node
	 * */
	public List<Integer> getNodeCanGo(List<Integer> list, int current, int source, int destination){
		List<Integer> listResult = new ArrayList<Integer>();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) > current) {
				listResult.add(list.get(i));
			}
		}
		
		return listResult;
	}
	
	/*End algorithm flow classifier*/
	
	@Override
	public RoutingAlgorithm build(Node node) throws CloneNotSupportedException {
		currentNode = node.getId();
		RoutingAlgorithm ra = super.build(node);
		if (ra instanceof FatTreeFlowClassifier) {
			FatTreeFlowClassifier ftfc = (FatTreeFlowClassifier) ra;
			ftfc.outgoingTraffic = new HashMap<>();
			ftfc.flowSizesPerDuration = new HashMap<>();
			ftfc.flowTable = new HashMap<>();
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
			flowSizesPerDuration = new HashMap<>();
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
