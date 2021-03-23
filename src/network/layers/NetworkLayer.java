package network.layers;

import events.IEventGenerator;
import infrastructure.entity.Node;
import infrastructure.state.State;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.layers.flowcontroller.DefaultController;
import network.layers.flowcontroller.FlowClassification;
import routing.RoutingAlgorithm;

public class NetworkLayer extends Layer implements IEventGenerator {

	protected State state;
	protected DefaultController defaultController 
		= new DefaultController();
		//= new FlowClassification();
		
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public NetworkLayer(RoutingAlgorithm ra, Node node) {
		RoutingAlgorithm routingAlgorithm = null;
		try {
			routingAlgorithm = ra.build(node);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.routingAlgorithm = routingAlgorithm;
	}

	public void controlFlow(ExitBuffer exitBuffer) {
		defaultController.controlFlow(exitBuffer);
	}
	
	public void route(EntranceBuffer entranceBuffer) {
		if (entranceBuffer.getNextNodeId() == -1) {
			Packet packet = entranceBuffer.getPeekPacket();
			if ((packet == null)) {
				System.out.println("ERROR: 2");
			}

			int nextNodeID = routingAlgorithm.next(packet, entranceBuffer.getNode());

			entranceBuffer.setNextNode(nextNodeID);

			ExitBuffer exitBuffer = entranceBuffer.physicalLayer.exitBuffers.get(nextNodeID);
			exitBuffer.addToRequestList(entranceBuffer);
			controlFlow(exitBuffer);
		} else {
			ExitBuffer exitBuffer = entranceBuffer.physicalLayer.exitBuffers.get(entranceBuffer.getNextNodeId());
			controlFlow(exitBuffer);
		}
	}

	public long getDurrationTime() {
		return (long)1000 * 1000 * 1000;
	}
}
