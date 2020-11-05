package network;

import config.Constant;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import graph.Coordination;
import graph.Graph;
import network.entities.Host;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.entities.*;
import network.layers.PhysicalLayer;
import simulator.DiscreteEventSimulator;
import weightedloadexperiment.pairstrategies.PairGenerator;
import java.util.*;

public class Topology {
	private Graph graph;
	private List<Host> hosts;
	private List<Switch> switches;
	private Map<Integer, Host> hostById;
	private Map<Integer, Switch> switchById;
	private List<Integer> sourceNodes;
	private List<Integer> destinationNodes;
	// private long bandwithToHost = 0;
	// ThanhNT 14/10 new property
	public Map<Integer, String> cordOfNodes;
	// End of ThanhNT 14/10 new property

	public PairGenerator pairGenerator;

	public Topology(FatTreeGraph graph, FatTreeRoutingAlgorithm routingAlgorithm, PairGenerator pair) {
		this.graph = graph;
		// construct hosts, switches and links and routing algorithm
		hosts = new ArrayList<>();
		switches = new ArrayList<>();
		hostById = new HashMap<>();
		switchById = new HashMap<>();
		sourceNodes = new ArrayList<>();
		destinationNodes = new ArrayList<>();
		cordOfNodes = new HashMap<>();// ThanhNT 14/10 add new statements to init property

		initSwitch(graph, routingAlgorithm); // Initialize switch and add this to a list
		Coordination C = new Coordination(graph);
		linkSwitch(C); // Link from switch to switch
		this.pairGenerator = pair;
		initHost(routingAlgorithm, pair); // Initialize host and add this to a list
		linkSwitchHost(C, graph); // link from switch to host

		pairGenerator.setUpBandwidth(this);
	}

	/**
	 * This method is used to initialize switch and add this to a list
	 * 
	 * @param graph
	 * @param routingAlgorithm
	 */
	private void initSwitch(FatTreeGraph graph, FatTreeRoutingAlgorithm routingAlgorithm) {

		for (int sid : graph.switches()) {
			Switch sw = new Switch(sid);
			switches.add(sw);
			switchById.put(sid, sw);

			// ThanhNT 14/10 add new statements to add new ID of switch
			cordOfNodes.put(sid, "");
			// End of ThanhNT 14/10 add new statements to add new ID of switch

			sw.physicalLayer = new PhysicalLayer(sw, graph.getK());
			sw.setNetworkLayer(routingAlgorithm, sw);
		}
	}

	/**
	 * This method is used link from switch to switch
	 * 
	 * @param C
	 */
	private void linkSwitch(Coordination C) {

		for (Switch sw : switches) {
			for (int nextNodeID : graph.adj(sw.getId())) {
				if (graph.isSwitchVertex(nextNodeID)) {
					Switch otherSwitch = switchById.get(nextNodeID);
					if (!otherSwitch.physicalLayer.links.containsKey(sw.getId())) {
						// create new link
						double distance = C.distanceBetween(sw.getId(), otherSwitch.getId());
						Link link = new Link(sw, otherSwitch, distance);
						sw.physicalLayer.links.put(otherSwitch.getId(), link);
						otherSwitch.physicalLayer.links.put(sw.getId(), link);

						createBuffer(sw, otherSwitch); // Create entrance buffer and exit buffer of switch and otherSwitch

						// ThanhNT 14/10 add new statements to insert coord of switch
						cordOfNodes.put(sw.getId(), C.getCoordOfSwitch(sw.getId()));
						cordOfNodes.put(otherSwitch.getId(), C.getCoordOfSwitch(otherSwitch.getId()));
					}
				}
			}
		}
	}

	/**
	 * This method is used to create entrance buffer and exit buffer of switch and otherSwitch
	 * @param sw
	 * @param otherSwitch
	 */
	private void createBuffer(Switch sw, Switch otherSwitch) {

		EntranceBuffer entranceBuffer;
		ExitBuffer exitBuffer;

		// exb and enb of switch
		entranceBuffer = new EntranceBuffer(sw, otherSwitch, Constant.QUEUE_SIZE);
		exitBuffer = new ExitBuffer(sw, otherSwitch, Constant.QUEUE_SIZE);
		entranceBuffer.physicalLayer = sw.physicalLayer;
		exitBuffer.physicalLayer = sw.physicalLayer;
		sw.physicalLayer.entranceBuffers.put(otherSwitch.getId(), entranceBuffer);
		sw.physicalLayer.exitBuffers.put(otherSwitch.getId(), exitBuffer);

		// exb and enb of Otherswitch
		entranceBuffer = new EntranceBuffer(otherSwitch, sw, Constant.QUEUE_SIZE);
		exitBuffer = new ExitBuffer(otherSwitch, sw, Constant.QUEUE_SIZE);
		entranceBuffer.physicalLayer = otherSwitch.physicalLayer;
		exitBuffer.physicalLayer = otherSwitch.physicalLayer;
		otherSwitch.physicalLayer.entranceBuffers.put(sw.getId(), entranceBuffer);
		otherSwitch.physicalLayer.exitBuffers.put(sw.getId(), exitBuffer);
	}

	/**
	 * This method is used to initialize host and add this to a list
	 * 
	 * @param routingAlgorithm
	 * @param pair
	 */
	private void initHost(FatTreeRoutingAlgorithm routingAlgorithm, PairGenerator pair) {

		Integer[] hostIDList = graph.hosts().toArray(new Integer[0]);
		pair.setAllHosts(hostIDList);

		pairGenerator.pairHosts();
		pairGenerator.checkValid();

		List<Integer> sourceNodeIDs = new ArrayList<>();
		List<Integer> destinationNodeIDs = new ArrayList<>();

		sourceNodeIDs = pairGenerator.getSources();
		destinationNodeIDs = pairGenerator.getDestinations();

		addSourceNodeID(sourceNodeIDs, routingAlgorithm);
		addDesNodeID(destinationNodeIDs, routingAlgorithm);
	}

	private void addSourceNodeID(List<Integer> sourceNodeIDs, FatTreeRoutingAlgorithm routingAlgorithm) {

		sourceNodes.addAll(sourceNodeIDs);

		for (int sourceNodeID : sourceNodes) {
			Host sourceNode = new Host(sourceNodeID);
			sourceNode.type = TypeOfHost.Source;
			sourceNode.physicalLayer = new PhysicalLayer(sourceNode);
			sourceNode.setNetworkLayer(routingAlgorithm, sourceNode);
			hosts.add(sourceNode);
			hostById.put(sourceNodeID, sourceNode);

			// ThanhNT 14/10 add new statements to add new ID of HOST
			cordOfNodes.put(sourceNodeID, "");
			// End of ThanhNT 14/10 add new statements to add new ID of HOST
		}
	}

	private void addDesNodeID(List<Integer> destinationNodeIDs, FatTreeRoutingAlgorithm routingAlgorithm) {

		destinationNodes.addAll(destinationNodeIDs);

		for (int destinationNodeID : destinationNodes) {
			Host destinationNode = null;
			if (hostById.containsKey(destinationNodeID)) {
				destinationNode = hostById.get(destinationNodeID);
				destinationNode.type = TypeOfHost.Mix;
			} else {

				destinationNode = new Host(destinationNodeID);
				destinationNode.type = TypeOfHost.Destination;
				hosts.add(destinationNode);
				hostById.put(destinationNodeID, destinationNode);
				destinationNode.physicalLayer = new PhysicalLayer(destinationNode);
				destinationNode.setNetworkLayer(routingAlgorithm, destinationNode);
			}

			cordOfNodes.put(destinationNodeID, ""); // ThanhNT 14/10 add new statements to add new ID of HOST
		}
	}

	/**
	 * This method is used to link from switch to host
	 * 
	 * @param C
	 * @param graph
	 */
	private void linkSwitchHost(Coordination C, FatTreeGraph graph) {
		for (Host host : hosts) {
			// get switch
			int switchID = graph.adj(host.getId()).get(0);
			Switch sw = switchById.get(switchID);
			Link link = new Link(host, sw, Constant.HOST_TO_SWITCH_LENGTH); // create new link

			host.physicalLayer.links.put(host.getId(), link); // only host link has id which is the id of host
			sw.physicalLayer.links.put(host.getId(), link);
			// initiate property in Physical Layer
			if (host.isSourceNode()) {
				// exit buffer of host
				ExitBuffer exitBuffer = new ExitBuffer(host, sw, Constant.QUEUE_SIZE);
				exitBuffer.physicalLayer = host.physicalLayer;
				host.physicalLayer.exitBuffers.put(sw.getId(), exitBuffer);
				// entrance buffer of switch to host
				EntranceBuffer entranceBuffer = new EntranceBuffer(sw, host, Constant.QUEUE_SIZE);
				entranceBuffer.physicalLayer = sw.physicalLayer;
				sw.physicalLayer.entranceBuffers.put(host.getId(), entranceBuffer);
			}
			// create exitBuffer for switch to link with desNode
			if (host.isDestinationNode()) {
				ExitBuffer exitBuffer = new ExitBuffer(sw, host, Constant.QUEUE_SIZE);
				exitBuffer.physicalLayer = sw.physicalLayer;
				sw.physicalLayer.exitBuffers.put(host.getId(), exitBuffer);
			}
			// ThanhNT 14/10 add new statements to insert coord of HOST
			cordOfNodes.put(host.getId(), C.getCoordOfHost(sw.getId(), Constant.HOST_TO_SWITCH_LENGTH));
		}
	}

	public List<Integer> getSourceNodeIDs() {
		return sourceNodes;
	}

	public List<Integer> getDestinationNodeIDs() {
		return destinationNodes;
	}

	public Graph getGraph() {
		return graph;
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public List<Switch> getSwitches() {
		return switches;
	}

	public Host getHostById(int id) {
		return hostById.get(id);
	}

	public void clear() {
		for (Host host : hosts) {
			host.clear();
		}

		for (Switch sw : switches) {
			sw.clear();
		}
	}

	public void setSimulator(DiscreteEventSimulator sim) {
		for (Host host : hosts) {
			host.physicalLayer.simulator = sim;
		}

		for (Switch sw : switches) {
			sw.physicalLayer.simulator = sim;
		}
		sim.topology = this;
	}

	public boolean checkDeadlock() {
		return false;
	}
}
