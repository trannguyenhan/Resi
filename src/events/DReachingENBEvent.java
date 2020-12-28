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
import network.states.unidirectionalway.W1;
import simulator.DiscreteEventSimulator;

enum TypeD {
	D, D1, D2
}

public class DReachingENBEvent extends Event {
	public static TypeD type = TypeD.D;

	/**
	 * This is the constructor method of DReachingENBEvent class extending Event
	 * class. This is the event which represents a type (D) event: packet reaches
	 * entrance buffer (ENB) of the next node
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public DReachingENBEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	@Override
	public void actions() {
		UnidirectionalWay unidirectionalWay = (UnidirectionalWay) element;
		EntranceBuffer entranceBuffer = unidirectionalWay.getToNode().physicalLayer.entranceBuffers
				.get(unidirectionalWay.getFromNode().getId());
		ExitBuffer exitBuffer = unidirectionalWay.getFromNode().physicalLayer.exitBuffers
				.get(unidirectionalWay.getToNode().getId());

		if (packet.getState().type == Type.P3 && unidirectionalWay.getState() instanceof W1
				&& unidirectionalWay.getToNode() instanceof Switch && entranceBuffer.getState() instanceof N0
				&& unidirectionalWay.getPacket() == packet) {
			unidirectionalWay.removePacket();
			entranceBuffer.insertPacket(packet);
			packet.setType(Type.P4);
			changeState(entranceBuffer, exitBuffer, unidirectionalWay);
		}
		entranceBuffer.getNode().getNetworkLayer().route(entranceBuffer);
	}

	/**
	 * This method is used to change the state of entrance buffer, exit buffer and
	 * unidirectional way
	 */
	@Override
	public void changeState(EntranceBuffer entranceBuffer, ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {
		if (entranceBuffer.isFull()) {
			type = TypeD.D2; // ENB is full
			changeENBState(entranceBuffer, "N1"); // change the state of entrance buffer to State N1
			changeWayState(unidirectionalWay, "W2"); // change the state of unidirectional way to State W2
		} else {
			type = TypeD.D1; // ENB is not full
			if (exitBuffer.getState().type == Type.X00) {
				changeEXBState(exitBuffer, "X01"); // change EXB state to X01
			}
			if (exitBuffer.getState().type == Type.X10) {
				changeEXBState(exitBuffer, "X11"); // change EXB state to X11
			}
		}
		changeWayState(unidirectionalWay, "W0"); // change the state of unidirectional way to State W0
	}
}
