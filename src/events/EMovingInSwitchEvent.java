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
	// Event dai dien cho su kien loai (E): goi tin roi khoi ENB cua Switch de sang
	// EXB

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
		{
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

				{
					packet.setType(Type.P5);
				}
				if (entranceBuffer.getState() instanceof N1) {
					entranceBuffer.setState(new N0(entranceBuffer));
					entranceBuffer.getState().act();
				}
				if (exitBuffer.isFull()) {
					type = TypeE.E2;
					if (exitBuffer.getState().type == Type.X00) {
						exitBuffer.setType(Type.X10);
						exitBuffer.getState().act();
					}
					if (exitBuffer.getState().type == Type.X01) {
						exitBuffer.setType(Type.X11);
						exitBuffer.getState().act();
					}
				}

				if (exitBuffer.isPeekPacket(packet)) {
					// add event F
					long time = (long) exitBuffer.physicalLayer.simulator.time();
					Event event = new FLeavingSwitchEvent(sim, time, time + Constant.SWITCH_CYCLE, exitBuffer, packet);
					event.register();
				}

				exitBuffer.getNode().getNetworkLayer().controlFlow(exitBuffer);

				if (!entranceBuffer.isEmpty()) {
					entranceBuffer.getNode().getNetworkLayer().route((entranceBuffer));
				}
			}
		}
	}
}
