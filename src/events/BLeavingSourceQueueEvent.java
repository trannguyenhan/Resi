package events;

import infrastructure.element.Element;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.SourceQueue;
import network.states.sourcequeue.Sq1;
import network.states.sourcequeue.Sq2;
import simulator.DiscreteEventSimulator;

enum TypeB {
	B, B1, B2, B3, B4
}

public class BLeavingSourceQueueEvent extends Event {
	protected TypeB type = TypeB.B;

	/**
	 * This is the constructor method of BLeavingSourceQueueEvent class extending
	 * Event class. This is the event which represents a type (B) event: packet
	 * leaves Source Queue
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public BLeavingSourceQueueEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	public TypeB getType() {
		return type;
	}

	public void setType(TypeB type) {
		this.type = type;
	}

	@Override
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		{
			SourceQueue sourceQueue = (SourceQueue) getElement();

			int connectedNodeID = sourceQueue.physicalLayer.links.get(sourceQueue.getId())
					.getOtherNode(sourceQueue.physicalLayer.node).getId();

			ExitBuffer exitBuffer = sourceQueue.physicalLayer.exitBuffers.get(connectedNodeID);
			if (((exitBuffer.getState().type == Type.X00) || (exitBuffer.getState().type == Type.X01))
					&& (sourceQueue.getState() instanceof Sq2 && sourceQueue.isPeekPacket(packet))) {
				// change state source queue, type B1
				if (sourceQueue.hasOnlyOnePacket()) {
					sourceQueue.setState(new Sq1(sourceQueue));
				}

				sourceQueue.removePacket();
				exitBuffer.insertPacket(packet);

				{
					packet.setType(Type.P2);
				}
				// change state EXB, type B4
				if (exitBuffer.isFull()) {
					if (exitBuffer.getState().type == Type.X00) {
						exitBuffer.setType(Type.X10);
					}
					if (exitBuffer.getState().type == Type.X01) {
						exitBuffer.setType(Type.X11);
						exitBuffer.getState().act();
					}
				}

				// add event C
				long time = (long) sourceQueue.physicalLayer.simulator.time();
				Event event = new CLeavingEXBEvent(sim, time, time, exitBuffer, packet);
				event.register();// add a new event
			}
		}
	}
}
