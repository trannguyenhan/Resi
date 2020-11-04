package infrastructure.state;

import config.Constant;
import events.BLeavingSourceQueueEvent;
import events.CLeavingEXBEvent;
import events.FLeavingSwitchEvent;
import events.IEventGenerator;
import infrastructure.element.Element;
import infrastructure.entity.Node;
import infrastructure.event.Event;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.SourceQueue;
import network.entities.Host;
import network.entities.Switch;
import simulator.DiscreteEventSimulator;

public class State {
	public static int countPacket = 0;
	public static int countStateENB = 0;

	public static int countStateEXB = 0;
	// public Event ancestorEvent;
	public IEventGenerator element;
	public Type type = Type.NONE;

	public void act() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		switch (type) {
		case X00:
			ExitBuffer exitBuffer = (ExitBuffer) this.element;
			Node currentNode = exitBuffer.getNode();
			if (currentNode.isSourceNode()) {
				Host sourceNode = (Host) currentNode;
				SourceQueue sourceQueue = sourceNode.physicalLayer.sourceQueue;
				Packet packet = sourceQueue.getPeekPacket();
				if (packet != null) {
					if (!sourceQueue.hasEventOfPacket(packet)) {
						long time = (long) sourceQueue.physicalLayer.simulator.time();
						Event event = new BLeavingSourceQueueEvent(sim, time, time, sourceQueue, packet);
						event.register(); // chen them su kien moi vao
					}
				}
			} else if (currentNode instanceof Switch) {
				Switch sw = (Switch) currentNode;
				exitBuffer.getNode().getNetworkLayer().controlFlow(exitBuffer);
			}
			break;
		case X01:
			ExitBuffer exitBuffer1 = (ExitBuffer) this.element;
			Node currentNode1 = exitBuffer1.getNode();
			if (currentNode1.isSourceNode()) {
				Host sourceNode = (Host) currentNode1;
				SourceQueue sourceQueue = sourceNode.physicalLayer.sourceQueue;
				Packet packet = sourceQueue.getPeekPacket();
				if (packet != null) {
					if (!sourceQueue.hasEventOfPacket(packet)) {
						long time = (long) sourceQueue.physicalLayer.simulator.time();
						Event event = new BLeavingSourceQueueEvent(sim, time, time, sourceQueue, packet);
						event.register(); // chen them su kien moi vao
					}
				}
			} else if (currentNode1 instanceof Switch) {
				Switch sw = (Switch) currentNode1;
				// todo goi event E( goi ham controlFlow)
				exitBuffer1.getNode().getNetworkLayer().controlFlow(exitBuffer1);
			}

			Packet packet = exitBuffer1.getPeekPacket();
			if (packet != null) {
				if (!(exitBuffer1.hasEventOfPacket(packet))) {
					if (exitBuffer1.getNode().isSourceNode()) {
						long time = (long) exitBuffer1.physicalLayer.simulator.time();
						Event event = new CLeavingEXBEvent(exitBuffer1.physicalLayer.simulator, time, time, exitBuffer1,
								packet);
						event.register(); // chen them su kien moi vao
					} else if (exitBuffer1.getNode() instanceof Switch) {
						long time = (long) exitBuffer1.physicalLayer.simulator.time();
						Event event = new FLeavingSwitchEvent(exitBuffer1.physicalLayer.simulator, time,
								time + Constant.SWITCH_CYCLE, exitBuffer1, packet);
						event.register(); // chen them su kien moi vao
					}
				}
			}
			break;
		case X11:
			ExitBuffer exitBuffer2 = (ExitBuffer) this.element;
			Packet packet2 = exitBuffer2.getPeekPacket();
			if (packet2 != null) {
				if (!(exitBuffer2.hasEventOfPacket(packet2))) {
					// todo xem neu can viet ham set trang thai co packet ve dung P2 hoac P5
					if (exitBuffer2.getNode().isSourceNode()) {
						long time = (long) exitBuffer2.physicalLayer.simulator.time();
						Event event = new CLeavingEXBEvent(exitBuffer2.physicalLayer.simulator, time, time, exitBuffer2,
								packet2);
						event.register(); // chen them su kien moi vao
					} else if (exitBuffer2.getNode() instanceof Switch) {
						long time = (long) exitBuffer2.physicalLayer.simulator.time();
						Event event = new FLeavingSwitchEvent(exitBuffer2.physicalLayer.simulator, time,
								time + Constant.SWITCH_CYCLE, exitBuffer2, packet2);
						event.register(); // chen them su kien moi vao
					}
				}
			}
			break;
		default:
			break;
		}

	}

	public void getNextState(Element e) {
	}

}
