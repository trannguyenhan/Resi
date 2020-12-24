package infrastructure.entity;

import network.layers.*;
import routing.RoutingAlgorithm;
import simulator.DiscreteEventSimulator;

public abstract class Node extends Device {

	private NetworkLayer networkLayer;
	public PhysicalLayer physicalLayer;
	public DataLinkLayer dataLinkLayer;

	public void setNetworkLayer(RoutingAlgorithm ra, Node node) {
		this.networkLayer = new NetworkLayer(ra, node);
	}

	public NetworkLayer getNetworkLayer() {
		return networkLayer;
	}

	public void setNetworkLayer(NetworkLayer networkLayer) {
		this.networkLayer = networkLayer;
	}

	protected Node(int id) {
		super(id);
	}

	public void setSimulator(DiscreteEventSimulator sim) {
		physicalLayer.simulator = sim;
	}

	public boolean isDestinationNode() {
		return false;
	}

	public boolean isSourceNode() {
		return false;
	}
}
