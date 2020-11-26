package events;

import config.Constant;
import infrastructure.element.Element;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.entities.Switch;
import network.states.enb.N0;
import network.states.enb.N1;
import simulator.DiscreteEventSimulator;

enum TypeE {
	E, E1, E2
}

public class EMovingInSwitchEvent extends Event {
	public TypeE type = TypeE.E;

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
		
		if (entranceBuffer.isPeekPacket(packet)
				&& ((exitBuffer.getState().type == Type.X00) || (exitBuffer.getState().type == Type.X01))) {
			entranceBuffer.dropNextNode();
			entranceBuffer.removePacket();
			exitBuffer.insertPacket(packet);
			exitBuffer.removeFromRequestList(entranceBuffer);
			packet.setType(Type.P5);
			if (entranceBuffer.getState() instanceof N1) {
				changeENBStateN0(entranceBuffer); // change ENB state
			}
			if (exitBuffer.isFull()) {
				type = TypeE.E2;
				if (exitBuffer.getState().type == Type.X00) {
					changeEXBStateX10(exitBuffer); // change EXB state
				}
				if (exitBuffer.getState().type == Type.X01) {
					changeEXBStateX11(exitBuffer); // change EXB state
				}
			}
			if (exitBuffer.isPeekPacket(packet)) {
				addEventF(exitBuffer, sim); // add event F
			}
			exitBuffer.getNode().getNetworkLayer().controlFlow(exitBuffer);

			if (!entranceBuffer.isEmpty()) {
				entranceBuffer.getNode().getNetworkLayer().route((entranceBuffer));
			}
		}
	}
}
