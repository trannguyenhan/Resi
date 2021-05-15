package custom.fattree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infrastructure.entity.Node;
import network.elements.Packet;

public class FatTreeFlowScheduler extends FatTreeRoutingAlgorithm {
	private FatTreeRoutingAlgorithm ra;
	private List<Integer> flows;					// record flow in fat-tree
	private List<Integer> paths;					// one path record by source, core switch and destination
	private List<Integer> listCoreSwitch;			// list core switch of fat-tree
	private Map<Integer, Integer> coreWithFlows; 	// register flow with core switch 
	private Map<Integer, Integer> nextPorts;		// register next node ( next port) with current node
	
	public FatTreeFlowScheduler(FatTreeGraph graph, boolean precomputed) {
		super(graph, precomputed);
		ra = new FatTreeFlowClassifier(g, false);
		flows = new ArrayList<Integer>();
		paths = new ArrayList<Integer>(); 
		listCoreSwitch = getListCoreSwitch();
		coreWithFlows = new HashMap<Integer, Integer>();
		nextPorts = new HashMap<Integer, Integer>();
	}

	@Override
	public int next(int source, int current, int destination) {
		int flow_id = hash(source, destination, -1);			// flow featured by source and destination
		int located_id = hash(source, current, destination);		// localted_id is located of current node featured by source, current and destination
		
		if(flows.contains(flow_id) && nextPorts.containsKey(located_id)) {
			//System.out.println("first");
			return nextPorts.get(located_id);
		} else {
			flows.add(flow_id);
		}
		
		if(g.isHostVertex(current)) { // start send packet
			// register current node with next node
			int next_node_id = g.adj(source).get(0);		// source node only adjacent 1 edge
			nextPorts.put(located_id, next_node_id);
			return next_node_id;
			
		} else if(g.adj(source).contains(current)) {		// edge switch	
			// if NEW flow
			// choose core not register by other flow
			// if not exist, choose core with lowest traffic
			for(int core : listCoreSwitch) {
				int path_id = hash(getPodOfHost(source), core, getPodOfHost(destination));
				if(!paths.contains(path_id)) {
					paths.add(path_id);
					coreWithFlows.put(flow_id, core); 
					break;
				}
			}
			
			int core = coreWithFlows.get(flow_id);
			for(int i_node : g.adj(current)) {
				if(g.adj(i_node).contains(core)) {
					int next_node_id = i_node;
					
					nextPorts.put(located_id, next_node_id);
					return next_node_id;
				}
			}
			
		} else { // sending... packet
			int type = g.switchType(current);
			
			// we no classifier with CORE switch or EDGE and AGG switch in Pod with destination
			// because there is only one path to destination
			if(getPodOfHost(current) == getPodOfHost(destination)) {
				if(type == FatTreeGraph.AGG || type == FatTreeGraph.EDGE) {
					int next_node_id = defaultNext(source, current, destination);
					nextPorts.put(located_id, next_node_id);
					
					return next_node_id;
				}
			} else if(type == FatTreeGraph.CORE) {
				int next_node_id = defaultNext(source, current, destination);
				nextPorts.put(located_id, next_node_id);
				
				return next_node_id;
			}
			
			int core = coreWithFlows.get(flow_id);
			if(type == FatTreeGraph.AGG) {
				nextPorts.put(located_id, core);
				
				return core;
			} 
			
		}
		
		
		return super.next(source, current, destination);
	}
	
	// function next with other parameter
	@Override
	public int next(Packet packet, Node node) {
		int current = node.getId();
		int source = packet.getSource();
		int destination = packet.getDestination();
		
		return next(source,  current, destination);
	}
	
	/**
	 * Default out-going port with two level table
	 */
	public int defaultNext(int source, int current, int destination) {
		return ra.next(source, current, destination);
	}
	
	// hash function, with c = -1 mean is we only hash 2 parameter a and b
	public int hash(int a, int b, int c) {
		if(c == -1) {
			return a + b* 3313;
		} else {
			return a + b * 3313 + c * 131;
		}
	}
	
	// get pod of host
	public int getPodOfHost(int a) {
		int k = g.getK(); 
		int each_devices_in_hostpod = (k*k)/4+k; // number of devices in pod
		
		return a / each_devices_in_hostpod;
	}
	
	// get list of core switch with index, example : with k = 4, we have core switch is 32, 33, 34, 35
	public List<Integer> getListCoreSwitch(){
		int k = g.getK();
		int total_devices = k*k*k/4 + 5*k*k/4;
		
		List<Integer> list = new ArrayList<Integer>();
		for(int i=total_devices - k; i<total_devices; i++) {
			list.add(i);
		}
		
		return list;
	}
}
