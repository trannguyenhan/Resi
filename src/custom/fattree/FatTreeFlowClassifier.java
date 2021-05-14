package custom.fattree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import config.Constant;
import infrastructure.entity.Node;
import javatuples.Pair;
import network.elements.Packet;
import routing.RoutingAlgorithm;
import weightedloadexperiment.ThroughputExperiment;

public class FatTreeFlowClassifier extends FatTreeRoutingAlgorithm {

	public Map<Pair<Integer, Integer>, Long> flowSizesPerDuration = new HashMap<>();
	public Map<Integer, Long> outgoingTraffic = new HashMap<>();
	public Map<Pair<Integer, Integer>, Long> flowTable = new HashMap<>();
	private int currentNode;
	
	// if isFlowClassification = true => have use flow classification
	// else use two level table routing each flow
	private boolean isFlowClassification;
	
	public int getCurrentNode() {
		return currentNode;
	}

	public FatTreeFlowClassifier(FatTreeGraph g, boolean precomputed) {
		super(g, precomputed);
		isFlowClassification = ThroughputExperiment.IS_FLOW_CLASSIFICATION;
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
		
		// isFlowClassification = true : have classifier
		// isFlowClassification = false : send package with default port (use two level table)
		int nextNodeID = next(source, current, destination, isFlowClassification);
		
		if(preNodeIDs.containsKey(packet.getId())) {
			int preNodeID = preNodeIDs.get(packet.getId());
			int prePortID = hash(preNodeID, current, -1);
			
			if(countPacketsUsePort.containsKey(prePortID)) {
				countPacketsUsePort.put(prePortID, countPacketsUsePort.get(prePortID) - 1);
			}
			
			listAllPacketsUsePort.get(prePortID).remove(packet); // remove packet in previous port
		}
		
		preNodeIDs.put(packet.getId(), current);
		
		int currentPortID = hash(current, nextNodeID, -1);
		if(listAllPacketsUsePort.containsKey(currentPortID)){ // add packet in current port ( next node ID)
			listAllPacketsUsePort.get(currentPortID).add(packet);
		} else {
			listAllPacketsUsePort.put(currentPortID, new ArrayList<Packet>());
			listAllPacketsUsePort.get(currentPortID).add(packet);
		}
		
		return nextNodeID;
	}

	/* Flow classifier with IncomingPacket and RearrangeFlow */
	public Map<Integer, Integer> seen = new HashMap<Integer, Integer>(); // check a flow with source and destination has been passed
	public Map<Integer, Integer> countPacketsUsePort = new HashMap<Integer, Integer>(); // each line connects 2 points representing 1 port  <--| 
	public Map<Integer, List<Packet>> listAllPacketsUsePort = new HashMap<Integer, List<Packet>>(); // list packet in each port      __________|
	public Map<Integer, Integer> preNodeIDs = new HashMap<Integer, Integer>(); // return previous node id of packet
	
	private int k = g.getK();;
	private int each_devices_in_hostpod = ( k * k ) / 4 + k;
	
	/** 
	 * This method is used to find the next node from the current node in the path with flow classifier if flow is true
	 * @param source      This is the source node
	 * @param current     This is the current node
	 * @param destination This is the destination node
	 * @param flow 	      This is choose use flow classifier
	 */
	public int next(int source, int current, int destination, boolean flow) {
		if(flow) {
			return incomingPacket(source, current, destination);
		} else {
			// call old function no flow classifier
			return next(source, current, destination);
		}
	}

	/* Check that host number a and b are on the same pod
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

		// we no classifier with CORE switch or EDGE and AGG switch in Pod with destination
		// because there is only one path to destination
		if(checkDevicesInPod(current, destination)) {
			if(type == FatTreeGraph.AGG || type == FatTreeGraph.EDGE) {
				return next(source, current, destination);
			}
		} else if(type == FatTreeGraph.CORE) {
			return next(source, current, destination);
		}
		
		// start algorithm flow classifier
		// hash(source, destination)
		int flowID = hash(source, destination, current);
		
		if(seen.containsKey(flowID)) {
			return seen.get(flowID);
		} else {
			List<Integer> listNeiborNodes = getNodeCanSent(g.adj(current), current);
			
			int minFlow = Integer.MAX_VALUE;
			int minIndexFlow = 0;
			for(int i=listNeiborNodes.size()-1; i>=0; i--) {
				int tmpID = listNeiborNodes.get(i);
				
				int portID = hash(current, tmpID, -1);
				if(countPacketsUsePort.containsKey(portID)){ // check port is exits
					int tmp = countPacketsUsePort.get(portID);
					if(tmp < minFlow) {
						minFlow = tmp;
						minIndexFlow = tmpID;
					}
				} else { // if port not exits, add port to usePorts with number of flow use is 0
					countPacketsUsePort.put(portID, 0); 
					if(minFlow >= 0) {
						minFlow = 0;
						minIndexFlow = tmpID;
					}
				}
			}
			
			int portMinID = hash(current, minIndexFlow, -1);
			countPacketsUsePort.put(portMinID, countPacketsUsePort.get(portMinID)+1);
			seen.put(flowID, minIndexFlow);
			
			return minIndexFlow;
		}
	}
	
	// hash function, with c = -1 mean is we only hash 2 parameter a and b
	public int hash(int a, int b, int c) {
		if(c == -1) {
			return a + b* 127;
		} else {
			return a + b * 127 + c * 131;
		}
	}
	
	/* Removes the port that just sent the packet to the current node
	 * */
	public List<Integer> getNodeCanSent(List<Integer> list, int current){
		List<Integer> listResult = new ArrayList<Integer>();
		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) > current) {
				listResult.add(list.get(i));
			}
		}
		
		return listResult;
	}
	
	/**
	 * This method call every t second ( t = 1 in this project)
	 * This method help sort flow with size of packet in flow
	 */
	public void rearrangeFlows() {
		int total_devices = k*k*k/4 + 5*k*k/4; 
		for(int currentID=0; currentID<total_devices; currentID++) {
			int type = g.switchType(currentID); 
			
			if(type == FatTreeGraph.AGG && type == FatTreeGraph.EDGE) { // only rearrangeFlows with AGG and EDGE switch
				List<Integer> listNeiborNodes = getNodeCanSent(g.adj(currentID), currentID); // get neighborhood node of node i-th
				int minSizeNodeID = -1;
				int maxSizeNodeID = -1;
				int minSize = Integer.MAX_VALUE;
				int maxSize = 0;
				
				for(int i=0; i<listNeiborNodes.size(); i++) {
					int nextNodeID = listNeiborNodes.get(i);
					int portID = hash(currentID, nextNodeID, -1);
					
					int sumSize = 0;
					for(int j=0; j<listAllPacketsUsePort.get(portID).size(); j++) {
						sumSize += listAllPacketsUsePort.get(portID).get(j).getSize();
					}
					
					if(sumSize < minSize) {
						minSize = sumSize;
						minSizeNodeID = nextNodeID;
					}
					
					if(sumSize > maxSize) {
						maxSize = sumSize;
						maxSizeNodeID = nextNodeID;
					}
				}
				
				int diffSize = maxSize - minSize;
				
				int portMinID = hash(currentID, minSizeNodeID, -1);
				int portMaxID = hash(currentID, maxSizeNodeID, -1);
				int maxSizePacketID = -1;
				maxSize = 0;
				
				// find packet have size max and less diffSize, location packet found save variable maxSizepacketID
				for(int j=0; j<listAllPacketsUsePort.get(portMaxID).size(); j++) {
					if(listAllPacketsUsePort.get(portMaxID).get(j).getSize() < diffSize
							&& listAllPacketsUsePort.get(portMaxID).get(j).getSize() > maxSize) {
						maxSize = listAllPacketsUsePort.get(portMaxID).get(j).getSize();
						maxSizePacketID = j;
					}
				}
				
				// assign flow f from port-max to port-min
				listAllPacketsUsePort.get(portMinID).add(listAllPacketsUsePort.get(portMaxID)
						.get(maxSizePacketID));  
				listAllPacketsUsePort.get(portMaxID).remove(maxSizePacketID);
				
				// reduce and increase the number of flow in countPacketsUsePort
				countPacketsUsePort.put(portMinID, countPacketsUsePort.get(portMinID)+1);
				countPacketsUsePort.put(portMaxID, countPacketsUsePort.get(portMinID)-1);
			}
		}
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
