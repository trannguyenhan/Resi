package events;

import infrastructure.element.Element;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Switch;
import network.states.enb.N0;
import network.states.enb.N1;
import simulator.DiscreteEventSimulator;

enum TypeE {
	E, E1, E2
}

public class EMovingInSwitchEvent extends Event {
	public static final TypeE type = TypeE.E;

	/**
	 * This is the constructor method of EMovingInSwitchEvent class extending Event
	 * class. This is the event which represents a type (E) event: packet moves from
	 * switch's entrance buffer (ENB) to switch's exit buffer (EXB)
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public EMovingInSwitchEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	@Override
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		EntranceBuffer entranceBuffer = (EntranceBuffer) element;
		Switch sw = (Switch) entranceBuffer.physicalLayer.node;
		int nextNodeID = entranceBuffer.getNextNodeId();
		ExitBuffer exitBuffer = sw.physicalLayer.exitBuffers.get(nextNodeID);
		UnidirectionalWay unidirectionalWay = null;

		if (entranceBuffer.isPeekPacket(packet)
				&& ((exitBuffer.getState().type == Type.X00) || (exitBuffer.getState().type == Type.X01))) {
			changeState(entranceBuffer, exitBuffer, unidirectionalWay);

			if (exitBuffer.isPeekPacket(packet)) {
				createEvent(exitBuffer, unidirectionalWay, sim, 'F'); // add event F
			}
			exitBuffer.getNode().getNetworkLayer().controlFlow(exitBuffer);
			if (!entranceBuffer.isEmpty()) {
				entranceBuffer.getNode().getNetworkLayer().route((entranceBuffer));
			}
		}
	}

	/**
	 * This method is used to change the state of entrance buffer and exit buffer
	 */
	@Override
	public void changeState(EntranceBuffer entranceBuffer, ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {
		entranceBuffer.dropNextNode();
		entranceBuffer.removePacket();
		exitBuffer.insertPacket(packet);
		exitBuffer.removeFromRequestList(entranceBuffer);
		packet.setType(Type.P5);
		if (entranceBuffer.getState() instanceof N1) {
			changeENBState(entranceBuffer, "N0"); // change the state of entrance buffer to State N0
		}
		if (exitBuffer.isFull()) {
			type = TypeE.E2;
			if (exitBuffer.getState().type == Type.X00) {
				changeEXBState(exitBuffer, "X10"); // change EXB state to X10
			}
			if (exitBuffer.getState().type == Type.X01) {
				changeEXBState(exitBuffer, "X11"); // change EXB state to X11
			}
		}
	}
}
