package events;

import infrastructure.element.Element;
import infrastructure.entity.Node;
import infrastructure.event.EventController;
import infrastructure.state.Type;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Host;
import network.entities.Switch;
import network.entities.TypeOfHost;
import network.states.unidirectionalway.W0;
import simulator.DiscreteEventSimulator;

public class CLeavingEXBEvent extends EventController {

	/**
	 * This is the constructor method of CLeavingEXBEvent class extending Event
	 * class. This is the event which represents a type (C) event: packet leaves
	 * exit buffer (EXB)
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
				
				chanegState(exitBuffer, unidirectionalWay);

				Node nextNode = unidirectionalWay.getToNode();
				if (nextNode instanceof Switch) { // if next node is switch, add event D
					addEventD(exitBuffer, unidirectionalWay, sim);
				} else if (nextNode instanceof Host) { 	// if next node is host, add event G
					Host h = (Host) nextNode;
					if (h.type == TypeOfHost.Destination || h.type == TypeOfHost.Mix) {
						addEventG(exitBuffer, unidirectionalWay, sim);
					}
				}
			}
		}
	}
	
	private void chanegState(ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {
		packet.setType(Type.P3);
		
		changeEXBStateX00(exitBuffer); // change EXB state
		changeWayStateW1(unidirectionalWay); // change uniWay state
	}
}
