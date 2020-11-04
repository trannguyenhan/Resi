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
	// Event dai dien cho su kien loai (H): mot Switch bao cho hang xom cua no rang
	// ENB cua no da trong

	public HNotificationEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem) {
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = null;
	}

	@Override
	public void actions() {
		// if(getElement() instanceof EntranceBuffer)
		{
			EntranceBuffer entranceBuffer = (EntranceBuffer) element;

			UnidirectionalWay unidirectionalWay = entranceBuffer.physicalLayer.links
					.get(entranceBuffer.getConnectNode().getId()).getWayToOtherNode(entranceBuffer.getConnectNode());

			if (unidirectionalWay.getState() instanceof W0 || unidirectionalWay.getState() instanceof W2) {
				ExitBuffer sendExitBuffer = entranceBuffer.getConnectNode().physicalLayer.exitBuffers
						.get(entranceBuffer.physicalLayer.node.getId());

				if (sendExitBuffer.getState().type == Type.X00) {
					// sendExitBuffer.setState(new X01(sendExitBuffer));
					sendExitBuffer.setType(Type.X01);
					sendExitBuffer.getState().act();
				}
				if (sendExitBuffer.getState().type == Type.X10) {
					// sendExitBuffer.setState(new X11(sendExitBuffer));
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
