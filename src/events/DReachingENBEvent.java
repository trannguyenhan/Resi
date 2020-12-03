package events;

import infrastructure.element.Element;
import infrastructure.event.EventController;
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

public class DReachingENBEvent extends EventController {
	public TypeD type = TypeD.D;

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
			type = TypeD.D2; // ENB full
			changeENBStateN1(entranceBuffer); // change state of ENB
			changeWayStateW2(unidirectionalWay); // change state of way
		} else {
			type = TypeD.D1; // ENB not full

			if (exitBuffer.getState().type == Type.X00) {
				changeEXBStateX01(exitBuffer); // change EXB state
			}
			if (exitBuffer.getState().type == Type.X10) {
				changeEXBStateX11(exitBuffer); // change EXB state
			}
		}
		changeWayStateW0(unidirectionalWay); // change state of way
	}
}
