package events;

import infrastructure.element.Element;
import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.UnidirectionalWay;
import network.states.unidirectionalway.W0;
import network.states.unidirectionalway.W2;
import simulator.DiscreteEventSimulator;

public class HNotificationEvent extends Event {

	/**
	 * This is the constructor method of HNotificationEvent class extending Event
	 * class. This is the event which represents a type (H) event: a switch notifies
	 * nearby switches that its exit buffer (ENB) is empty
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 */
	public HNotificationEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = null;
	}

	@Override
	public void actions() {
		{
			EntranceBuffer entranceBuffer = (EntranceBuffer) element;

			UnidirectionalWay unidirectionalWay = entranceBuffer.physicalLayer.links
					.get(entranceBuffer.getConnectNode().getId()).getWayToOtherNode(entranceBuffer.getConnectNode());

			if (unidirectionalWay.getState() instanceof W0 || unidirectionalWay.getState() instanceof W2) {
				ExitBuffer sendExitBuffer = entranceBuffer.getConnectNode().physicalLayer.exitBuffers
						.get(entranceBuffer.physicalLayer.node.getId());

				if (sendExitBuffer.getState().type == Type.X00) {
					sendExitBuffer.setType(Type.X01);
					sendExitBuffer.getState().act();
				}
				if (sendExitBuffer.getState().type == Type.X10) {
					sendExitBuffer.setType(Type.X11);
					sendExitBuffer.getState().act();
				}
				if (unidirectionalWay.getState() instanceof W2) {
					unidirectionalWay.setState(new W0(unidirectionalWay));
					unidirectionalWay.getState().act();
				}

			}
		}

	}
}
