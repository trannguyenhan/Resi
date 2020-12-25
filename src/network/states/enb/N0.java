package network.states.enb;

import config.Constant;
import events.DReachingENBEvent;
import events.HNotificationEvent;
import infrastructure.event.Event;
import infrastructure.state.State;
import network.elements.EntranceBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;

public class N0 extends State {
	// � State N0: ENB is not full.
	public N0(EntranceBuffer entranceBuffer) {
		this.element = entranceBuffer;
	}

	@Override
	public void act() {
		// add event H
		EntranceBuffer entranceBuffer = (EntranceBuffer) element;
		long time = (long) entranceBuffer.physicalLayer.simulator.time();
		Event event = new HNotificationEvent(entranceBuffer.physicalLayer.simulator, time, time + Constant.CREDIT_DELAY,
				entranceBuffer);
		event.register(); // add a new event

		UnidirectionalWay unidirectionalWay = entranceBuffer.physicalLayer.links
				.get(entranceBuffer.getConnectNode().getId()).getWayToOtherNode(entranceBuffer.getConnectNode());
		Packet packet = unidirectionalWay.getPacket();

		if (packet != null && !unidirectionalWay.hasEventOfPacket(packet)) // when adding a new event, we have to create
																			// a new state for the packet, because this
																			// state is associated with the event
			// add event D
			time = (long) entranceBuffer.physicalLayer.simulator.time();
		event = new DReachingENBEvent(entranceBuffer.physicalLayer.simulator, time,
				time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay, packet);
		event.register(); // add a new event
	}
}
