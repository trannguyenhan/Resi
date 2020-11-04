package network.states.unidirectionalway;

import config.Constant;
import events.CLeavingEXBEvent;
import events.FLeavingSwitchEvent;
import infrastructure.event.Event;
import infrastructure.state.State;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Host;
import network.entities.Switch;

public class W0 extends State {
	// � State W0: the way has no packet and it is able to transfer one.

	public W0(UnidirectionalWay unidirectionalWay) {
		this.element = unidirectionalWay;
	}

	@Override
	public void act() {
		UnidirectionalWay unidirectionalWay = (UnidirectionalWay) element;
		ExitBuffer exitBuffer = unidirectionalWay.getFromNode().physicalLayer.exitBuffers
				.get(unidirectionalWay.getToNode().getId());
		Packet packet = exitBuffer.getPeekPacket();
		if (packet != null) {
			if (!(exitBuffer.hasEventOfPacket(packet))) {

				if (exitBuffer.getNode() instanceof Host) {
					long time = (long) exitBuffer.physicalLayer.simulator.time();
					Event event = new CLeavingEXBEvent(exitBuffer.physicalLayer.simulator, time, time, exitBuffer,
							packet);
					event.register(); // chen them su kien moi vao
				} else if (exitBuffer.getNode() instanceof Switch) {
					long time = (long) exitBuffer.physicalLayer.simulator.time();
					Event event = new FLeavingSwitchEvent(exitBuffer.physicalLayer.simulator, time,
							time + Constant.SWITCH_CYCLE, exitBuffer, packet);
					event.register(); // chen them su kien moi vao

				}
			}
		}
	}
}
