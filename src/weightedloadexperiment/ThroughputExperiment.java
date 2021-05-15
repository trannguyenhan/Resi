package weightedloadexperiment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Method;

import common.StdOut;
import config.Constant;
import custom.fattree.FatTreeFlowClassifier;
import custom.fattree.FatTreeFlowScheduler;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import events.layers.RearrangementEvent;
import infrastructure.event.Event;
import network.Topology;
import network.entities.Host;
import network.entities.Switch;
import network.entities.TypeOfHost;
import simulator.DiscreteEventSimulator;
import weightedloadexperiment.pairstrategies.InterPodIncoming;
import weightedloadexperiment.pairstrategies.IPIBacktracking;
import weightedloadexperiment.pairstrategies.IPIBacktrackingRandom;
import weightedloadexperiment.pairstrategies.IPIBacktrackingRandomImprove;
import weightedloadexperiment.pairstrategies.IPIHalfCoreSwitchRandom;
import weightedloadexperiment.pairstrategies.PairGenerator;
import weightedloadexperiment.pairstrategies.SameIDOutgoing;
import weightedloadexperiment.pairstrategies.SelfIncoming;
import weightedloadexperiment.pairstrategies.StaggeredProb;

public class ThroughputExperiment {
	public static final boolean IS_FLOW_CLASSIFICATION = false;
	
	private Topology topology;

	public ThroughputExperiment(Topology network) {
		this.topology = network;
	}

	/**
	 * This method is used to start a process of simulating discrete events
	 * 
	 * @param simulator
	 * @param trafficPattern
	 */
	private void startSimulator(DiscreteEventSimulator simulator, Map<Integer, Integer> trafficPattern) {

		System.out.println("Start:");

		topology.clear(); // clear all the data, queue, ... in switches, hosts
		topology.setSimulator(simulator);

		int count = 0;
		for (Integer source : trafficPattern.keySet()) {
			Integer destination = trafficPattern.get(source);
			count++;
			((Host) topology.getHostById(source)).generatePacket(destination);
		}
		
		// add event rearrangement call function rearrangement sort flow in port
		if(IS_FLOW_CLASSIFICATION) {
			Event event = new RearrangementEvent(simulator, count, count, topology.getSwitches().get(0).getNetworkLayer());
			event.register();
		}
		
		simulator.start();
	}
	
	/**
	 * This method is used to calculate the value of though-put and end the
	 * simulator process
	 * 
	 * @param nPoint
	 * @param points
	 * @param simulator
	 * @param trafficPattern
	 */
	private void calculateThroughput(long start, int nPoint, double[][] points, DiscreteEventSimulator simulator,
			Map<Integer, Integer> trafficPattern) {

		double interval = 1e7;
		double throughput = 0;
		List<Double> scores = new ArrayList<>();
		for (int i = 0; i < nPoint; i++) {
			points[1][i] = 100 * points[1][i] * Constant.PACKET_SIZE
					/ (trafficPattern.size() * Constant.LINK_BANDWIDTH * interval / 1e9);
		}
		for (int i = 0; i < nPoint; i++) {
			scores.add(points[1][i]);
		}
		throughput = points[1][nPoint - 1];

		StdOut.printf("Throughput : %.2f\n", throughput);

		double rawThroughput = throughput * Constant.LINK_BANDWIDTH / 100 / 1e9;

		double alternativeRawThroughput = simulator.numReceived * Constant.PACKET_SIZE / (trafficPattern.size());
		alternativeRawThroughput = (double)alternativeRawThroughput / (nPoint * interval);

		long end = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
		GraphPanel.createAndShowGui(scores);
	}

	public double[][] showThroughput(Map<Integer, Integer> trafficPattern, boolean verbose) {

		long start = System.currentTimeMillis();

		DiscreteEventSimulator.Initialize(true, Constant.MAX_TIME, verbose);
		DiscreteEventSimulator simulator = DiscreteEventSimulator.getInstance();
		startSimulator(simulator, trafficPattern);

		double interval = 1e7;
		int nPoint = (int) (simulator.getTimeLimit() / interval + 1);
		double[][] points = new double[2][nPoint];
		for (int i = 0; i < nPoint; i++) {
			// convert to ms
			points[0][i] = i * interval;
			points[1][i] = simulator.receivedPacketPerUnit[i];
		}

		calculateThroughput(start, nPoint, points, simulator, trafficPattern); // Calculate the value of though-put

		return points;
	}
	
	/**
	 * This method is used to calculate the capacity for switches to flow to other nodes
	 */
	private void calFlowCapacity() {		
		int rxPacket = 0;
		double thp = 0;
		double privateThp = 0;

		for (int i = 0; i < topology.getHosts().size(); i++) {
			Host host = topology.getHosts().get(i);
			if (host.type == TypeOfHost.Destination || host.type == TypeOfHost.Mix) {
				Host destinationNode = host;
				if (destinationNode.getReceivedPacketInNode() != 0) {
					rxPacket += destinationNode.getReceivedPacketInNode();
					privateThp = destinationNode.getReceivedPacketInNode() * Constant.PACKET_SIZE
							/ (destinationNode.getLastRx() - destinationNode.getFirstTx());
					thp += privateThp;

				}
			}
		}
		for (int i = 0; i < topology.getSwitches().size(); i++) {
			Switch nodeSwitch = topology.getSwitches().get(i);
			System.out.print("\nSwitch has id: " + nodeSwitch.getId() + " \n");

			if (nodeSwitch.getNetworkLayer().routingAlgorithm instanceof FatTreeFlowClassifier) {
				FatTreeFlowClassifier ftfc = (FatTreeFlowClassifier) nodeSwitch.getNetworkLayer().routingAlgorithm;
				Map<Integer, Long> outgoingTraffic = ftfc.outgoingTraffic;

				for (Integer key : outgoingTraffic.keySet()) {
					System.out.println("\tFlow to node: " + key + " has capacity: " + outgoingTraffic.get(key));
				}
			}
		}
	}

	public static void main(String[] args) {
		FatTreeGraph graph = new FatTreeGraph(4);
		FatTreeRoutingAlgorithm ra = //new FatTreeRoutingAlgorithm(G, false);
				new FatTreeFlowClassifier(graph, false); 
				//new FatTreeFlowScheduler(graph, false);
		PairGenerator pairGenerator = //new StrideIndex(8);
		//new InterPodIncoming(ra, graph);
		// new ForcePair(ra, G, 13);
		// new MinimalCoreSwitches(ra, G);
		new SameIDOutgoing(graph, ra);
		//new IPIBacktracking(ra, graph);
		//new IPIBacktrackingRandom(ra, graph);
		//new IPIHalfCoreSwitchRandom(ra, graph);
		//new StaggeredProb(ra, graph, 1.0, 0.0);
		//new IPIBacktrackingRandomImprove(ra, graph);
		//new SelfIncoming(ra, graph);
		
		Topology topology = new Topology(graph, ra, pairGenerator);
		// new StaggeredProb(hosts, 4, 1, 0);
		// new InterPodIncoming(hosts, k, ra, G);
		ThroughputExperiment experiment = new ThroughputExperiment(topology);
		Map<Integer, Integer> traffic = new HashMap<>();
		List<Integer> sourceNodeIDs = topology.getSourceNodeIDs();
		List<Integer> destinationNodeIDs = topology.getDestinationNodeIDs();

		int sizeOfFlow = // 1;
				sourceNodeIDs.size();
		for (int i = 0; i < sizeOfFlow; i++) {
			traffic.put(sourceNodeIDs.get(i), destinationNodeIDs.get(i));
		}
		experiment.showThroughput(traffic, false); // show the value of through-put which has been calculated
		experiment.calFlowCapacity(); // Calculate the capacity for flowing from switches to nodes
	}
}