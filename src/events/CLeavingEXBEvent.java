package events;

import infrastructure.element.Element;
import infrastructure.entity.Node;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Host;
import network.entities.Switch;
import network.entities.TypeOfHost;
import network.states.unidirectionalway.W0;
import network.states.unidirectionalway.W1;
import simulator.DiscreteEventSimulator;

public class CLeavingEXBEvent extends Event {

	/**
	 * This is the constructor method of CLeavingEXBEvent class extending Event class.
	 * This is the event which represents a type (C) event: packet leaves exit
	 * buffer (EXB)
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public CLeavingEXBEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	@Override
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		{
			ExitBuffer exitBuffer = (ExitBuffer) element;

			UnidirectionalWay unidirectionalWay = exitBuffer.physicalLayer.links
					.get(exitBuffer.physicalLayer.node.getId()) // It is different because it is the host's link
					.getWayToOtherNode(exitBuffer.physicalLayer.node);

			if (unidirectionalWay.getState() instanceof W0 && exitBuffer.isPeekPacket(packet)
					&& ((exitBuffer.getState().type == Type.X11) || (exitBuffer.getState().type == Type.X01))) {
				unidirectionalWay.addPacket(exitBuffer.removePacket());

				{
					packet.setType(Type.P3);
				}
				// change EXB state
				exitBuffer.setType(Type.X00);
				exitBuffer.getState().act();
				// change uniWay state
				unidirectionalWay.setState(new W1(unidirectionalWay));
				unidirectionalWay.getState().act();

				Node nextNode = unidirectionalWay.getToNode();
				// next node is switch so add event D
				if (nextNode instanceof Switch) {
					// add event D
					long time = (long) exitBuffer.physicalLayer.simulator.time();
					Event event = new DReachingENBEvent(sim, time,
							time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay,
							packet);
					event.register(); // add a new event
				} else if (nextNode instanceof Host) {
					Host h = (Host) nextNode;
					if (h.type == TypeOfHost.Destination || h.type == TypeOfHost.Mix) {
						// add event G
						long time = (long) exitBuffer.physicalLayer.simulator.time();
						Event event = new GReachingDestinationEvent(sim, time,
								time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay,
								packet);
						event.register(); // add a new event
					}
				}
			}
		}

	}
}
