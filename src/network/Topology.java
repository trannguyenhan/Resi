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

/**
 * Created by Dandoh on 6/27/17.
 */
public class Topology {
	private Graph graph;
	private List<Host> hosts;
	private List<Switch> switches;
	private Map<Integer, Host> hostById;
	private Map<Integer, Switch> switchById;
	private List<Integer> sourceNodes;
	private List<Integer> destinationNodes;
	private long bandwithToHost = 0;
	// ThanhNT 14/10 new property
	public Map<Integer, String> cordOfNodes;
	// Endof ThanhNT 14/10 new property

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

		// ThanhNT 14/10 add new statements to init property
		cordOfNodes = new HashMap<>();
		// Endof ThanhNT 14/10 add new statements to init property

		// NetworkLayer networkLayer = new NetworkLayer(routingAlgorithm);
		// khoi tao switch va them vao list
		for (int sid : graph.switches()) {
			Switch sw = new Switch(sid);
			switches.add(sw);
			switchById.put(sid, sw);

			// ThanhNT 14/10 add new statements to add new ID of switch
			cordOfNodes.put(sid, "");
			// Endof ThanhNT 14/10 add new statements to add new ID of switch

			sw.physicalLayer = new PhysicalLayer(sw, graph.getK());
			// sw.networkLayer = //networkLayer;
			// new NetworkLayer(routingAlgorithm);
			sw.setNetworkLayer(routingAlgorithm, sw);
		}

		// link from switch to switch
		Coordination C = new Coordination(graph);
		for (Switch sw : switches) {
			for (int nextNodeID : graph.adj(sw.getId())) {
				if (graph.isSwitchVertex(nextNodeID)) {
					Switch otherSwitch = switchById.get(nextNodeID);
					// => ThanhNT set comment to THE following line
					if (!otherSwitch.physicalLayer.links.containsKey(sw.getId())) {
						// create new link
						double distance = C.distanceBetween(sw.getId(), otherSwitch.getId());
						// System.out.println("Chieu dai switch = " + distance + " from: " + sw.getId()
						// + " to: " + otherSwitch.getId());
						// double x = 5;

						EntranceBuffer entranceBuffer;
						ExitBuffer exitBuffer;
						Link link = new Link(sw, otherSwitch, distance);
						sw.physicalLayer.links.put(otherSwitch.getId(), link);
						otherSwitch.physicalLayer.links.put(sw.getId(), link);

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

						// ThanhNT 14/10 add new statements to insert coord of switch
						cordOfNodes.put(sw.getId(), C.getCoordOfSwitch(sw.getId()));
						cordOfNodes.put(otherSwitch.getId(), C.getCoordOfSwitch(otherSwitch.getId()));
						// Endof ThanhNT 14/10 add new statements to insert coord of switch
					}
				}
			}
		}

		this.pairGenerator = pair;

		// khoi tao host va dua vao list
		Integer[] hostIDList = graph.hosts().toArray(new Integer[0]);
		pair.setAllHosts(hostIDList);

		pairGenerator.pairHosts();
		pairGenerator.checkValid();

		List<Integer> sourceNodeIDs = new ArrayList<>();
		// = topology.getSourceNodeIDs();
		List<Integer> destinationNodeIDs = new ArrayList<>();
		// = topology.getDestinationNodeIDs();

		sourceNodeIDs = pairGenerator.getSources();
		destinationNodeIDs = pairGenerator.getDestinations();
		// Knuth.shuffle(hostIDList);

//        hostIDList = new Integer[]{ 17,24,18,11,2,3,19,8,26,0,27,1,10,16,9,25 };

		sourceNodes.addAll(sourceNodeIDs// .subList(0, hostIDList.length / 2)
		);
		// sourceNodes.add(0);

		for (int sourceNodeID : sourceNodes) {
			Host sourceNode = new Host(sourceNodeID);
			sourceNode.type = TypeOfHost.Source;
			sourceNode.physicalLayer = new PhysicalLayer(sourceNode);
			// sourceNode.networkLayer = //networkLayer;
			// new NetworkLayer(routingAlgorithm);
			sourceNode.setNetworkLayer(routingAlgorithm, sourceNode);
			hosts.add(sourceNode);
			hostById.put(sourceNodeID, sourceNode);

			// ThanhNT 14/10 add new statements to add new ID of HOST
			cordOfNodes.put(sourceNodeID, "");
			// Endof ThanhNT 14/10 add new statements to add new ID of HOST

		}

		destinationNodes.addAll(// Arrays.asList(hostIDList)//.subList(hostIDList.length / 2, hostIDList.length)
				destinationNodeIDs);
		// destinationNodes.add(1);

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
				// destinationNode.networkLayer = //networkLayer;
				// new NetworkLayer(routingAlgorithm);
				destinationNode.setNetworkLayer(routingAlgorithm, destinationNode);
			}

			// ThanhNT 14/10 add new statements to add new ID of HOST
			cordOfNodes.put(destinationNodeID, "");
			// Endof ThanhNT 14/10 add new statements to add new ID of HOST

		}

		// todo them phan add link tu switch den host kieu nhu o tren
		// link from switch to host
		for (Host host : hosts) {
			// get switch
			int switchID = graph.adj(host.getId()).get(0);
			Switch sw = switchById.get(switchID);

			// create new link
			Link link = new Link(host, sw, Constant.HOST_TO_SWITCH_LENGTH);

			host.physicalLayer.links.put(host.getId(), link);// rieng link host luu id la id cua host
			sw.physicalLayer.links.put(host.getId(), link);

			// initiate property in Physical Layer
			if (host.isSourceNode()) {
				// exb of host
				ExitBuffer exitBuffer = new ExitBuffer(host, sw, Constant.QUEUE_SIZE);
				exitBuffer.physicalLayer = host.physicalLayer;
				host.physicalLayer.exitBuffers.put(sw.getId(), exitBuffer);

				// enb of switch to host
				EntranceBuffer entranceBuffer = new EntranceBuffer(sw, host, Constant.QUEUE_SIZE);
				entranceBuffer.physicalLayer = sw.physicalLayer;
				sw.physicalLayer.entranceBuffers.put(host.getId(), entranceBuffer);
			}

			// tao exitBuffer cho switch noi toi desNode
			// exb of switch to desNode
			if (host.isDestinationNode()) {
				ExitBuffer exitBuffer = new ExitBuffer(sw, host, Constant.QUEUE_SIZE);
				exitBuffer.physicalLayer = sw.physicalLayer;
				sw.physicalLayer.exitBuffers.put(host.getId(), exitBuffer);
			}

			// ThanhNT 14/10 add new statements to insert coord of HOST
			cordOfNodes.put(host.getId(), C.getCoordOfHost(sw.getId(), Constant.HOST_TO_SWITCH_LENGTH));
			// Endof ThanhNT 14/10 add new statements to insert coord of HOST
		}

		pairGenerator.setUpBandwidth(this);
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
