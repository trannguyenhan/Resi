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

	// Event dai dien cho su kien loai (B): goi tin roi khoi Source Queue
	public BLeavingSourceQueueEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		// countSubEvent++;
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
					// sourceQueue.getState().act();
				}

				sourceQueue.removePacket();
				exitBuffer.insertPacket(packet);

				// change Packet state
				// if (packet.getState() instanceof StateP1)
				{
					// packet.setState(new StateP2(exitBuffer, packet, this));
					packet.setType(Type.P2);
					// packet.getState().act();
				}
				// change state EXB, type b4
				if (exitBuffer.isFull()) {
					if (exitBuffer.getState().type == Type.X00) {
						// exitBuffer.setState(new X10(exitBuffer));
						exitBuffer.setType(Type.X10);
						// exitBuffer.getState().act();
					}
					if (exitBuffer.getState().type == Type.X01) {
						// exitBuffer.setState(new X11(exitBuffer));
						exitBuffer.setType(Type.X11);
						exitBuffer.getState().act();
					}
				}

//				// add event C
				long time = (long) sourceQueue.physicalLayer.simulator.time();
				Event event = new CLeavingEXBEvent(sim, time, time, exitBuffer, packet);
				event.register();// chen them su kien moi vao
			}
		}
	}
}
