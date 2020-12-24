package events;

import infrastructure.element.Element;
import infrastructure.entity.Node;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Host;
import network.entities.Switch;
import network.entities.TypeOfHost;
import network.states.unidirectionalway.W0;
import network.states.unidirectionalway.W1;
import simulator.DiscreteEventSimulator;

public class FLeavingSwitchEvent extends Event {

	/**
	 * This is the constructor method of FLeavingSwitchEvent class extending Event
	 * class. This is the event which represents a type (F) event: packet leaves
	 * switch's entrance buffer (ENB) to go up to LINK
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public FLeavingSwitchEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	@Override
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		EntranceBuffer entranceBuffer = null;
		ExitBuffer exitBuffer = (ExitBuffer) element;
		UnidirectionalWay unidirectionalWay = exitBuffer.physicalLayer.links.get(exitBuffer.getConnectNode().getId())
				.getWayToOtherNode(exitBuffer.physicalLayer.node);

		if (exitBuffer.isPeekPacket(packet) && unidirectionalWay.getState() instanceof W0
				&& ((exitBuffer.getState().type == Type.X11) || (exitBuffer.getState().type == Type.X01))) {
			changeState(entranceBuffer, exitBuffer, unidirectionalWay);
			Node nextNode = exitBuffer.getConnectNode();
			exitBuffer.physicalLayer.node.getNetworkLayer().routingAlgorithm.update(packet, nextNode);
			if (nextNode instanceof Host) {
				Host h = (Host) nextNode;
				if (h.type == TypeOfHost.Destination || h.type == TypeOfHost.Mix) {
					createEvent(exitBuffer, unidirectionalWay, sim, 'G'); // add event G
				}
			} else if (nextNode instanceof Switch) {
				createEvent(exitBuffer, unidirectionalWay, sim, 'D'); // add event D
			}
		}
	}

	/**
	 * This method is used to change the state of exit buffer and unidirectional way
	 */
	@Override
	public void changeState(EntranceBuffer entranceBuffer, ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {
		unidirectionalWay.addPacket(exitBuffer.removePacket());

		if (packet.getState().type == Type.P5) {
			packet.setType(Type.P3); // change Packet state
		}
		changeEXBState(exitBuffer, "X00"); // change EXB state to X00

		changeWayState(unidirectionalWay, "W1"); // change the state of unidirectional way to State W1
	}
}