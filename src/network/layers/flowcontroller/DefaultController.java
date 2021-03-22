package network.layers.flowcontroller;

import config.Constant;
import events.EMovingInSwitchEvent;
import infrastructure.event.Event;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;

public class DefaultController {
	public void controlFlow(ExitBuffer exitBuffer) {
		// Se thanh 1 PT cua lop DefaultController thay vi la 1 pt cua NetworkLayer
		if (!(exitBuffer.isRequestListEmpty())) {
			int selectedId = Integer.MAX_VALUE;
			EntranceBuffer selectedENB = null;
			Packet p;
			// Get enbs from request lisst of the current exb
			for (EntranceBuffer enb : exitBuffer.getRequestList()) {
				p = enb.getPeekPacket();
				// Choose the Inport whose packet has the smallest ID
				if (p != null && !(enb.hasEventOfPacket(p)) && p.getId() < selectedId) {
					selectedId = p.getId();
					selectedENB = enb;
				}
			}
			if (selectedENB != null) {
				long time = (long) selectedENB.physicalLayer.simulator.time();
				Event event = new EMovingInSwitchEvent(selectedENB.physicalLayer.simulator, time,
						time + Constant.SWITCH_CYCLE, selectedENB, selectedENB.getPeekPacket());
				event.register(); // add a new packet
			}
		}
	}
}
