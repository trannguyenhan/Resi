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
	public static final int numPacket = 0;
	public static final int numStateENB = 0;

	public static final int numStateEXB = 0;
	public IEventGenerator element;
	public Type type = Type.NONE;

	public void act() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		switch (type) {
		case X00:
			actX00(sim);
			break;
		case X01:
			actX01(sim);
			break;
		case X11:
			actX11();
			break;
		default:
			break;
		}
	}

	private void actX00(DiscreteEventSimulator sim) {
		ExitBuffer exitBuffer = (ExitBuffer) this.element;
		Node currentNode = exitBuffer.getNode();
		if (currentNode.isSourceNode()) {
			Host sourceNode = (Host) currentNode;
			SourceQueue sourceQueue = sourceNode.physicalLayer.sourceQueue;
			Packet packet = sourceQueue.getPeekPacket();
			if (packet != null && !sourceQueue.hasEventOfPacket(packet)) {
				long time = (long) sourceQueue.physicalLayer.simulator.time();
				Event event = new BLeavingSourceQueueEvent(sim, time, time, sourceQueue, packet);
				event.register(); // add a new event
			}
		} else if (currentNode instanceof Switch) {
			exitBuffer.getNode().getNetworkLayer().controlFlow(exitBuffer);
		}
	}

	private void actX01(DiscreteEventSimulator sim) {
		ExitBuffer exitBuffer1 = (ExitBuffer) this.element;
		Node currentNode1 = exitBuffer1.getNode();
		if (currentNode1.isSourceNode()) {
			Host sourceNode = (Host) currentNode1;
			SourceQueue sourceQueue = sourceNode.physicalLayer.sourceQueue;
			Packet packet = sourceQueue.getPeekPacket();
			if (packet != null && !sourceQueue.hasEventOfPacket(packet)) {
				long time = (long) sourceQueue.physicalLayer.simulator.time();
				Event event = new BLeavingSourceQueueEvent(sim, time, time, sourceQueue, packet);
				event.register(); // add a new event
			}
		} else if (currentNode1 instanceof Switch) {
			// call event E (call controlFlow)
			exitBuffer1.getNode().getNetworkLayer().controlFlow(exitBuffer1);
		}

		Packet packet = exitBuffer1.getPeekPacket();
		if (packet != null && !(exitBuffer1.hasEventOfPacket(packet))) {
			if (exitBuffer1.getNode().isSourceNode()) {
				long time = (long) exitBuffer1.physicalLayer.simulator.time();
				Event event = new CLeavingEXBEvent(exitBuffer1.physicalLayer.simulator, time, time, exitBuffer1,
						packet);
				event.register(); // add a new event
			} else if (exitBuffer1.getNode() instanceof Switch) {
				long time = (long) exitBuffer1.physicalLayer.simulator.time();
				Event event = new FLeavingSwitchEvent(exitBuffer1.physicalLayer.simulator, time,
						time + Constant.SWITCH_CYCLE, exitBuffer1, packet);
				event.register(); // add a new event
			}
		}
	}

	private void actX11() {
		ExitBuffer exitBuffer2 = (ExitBuffer) this.element;
		Packet packet2 = exitBuffer2.getPeekPacket();
		if (packet2 != null && !(exitBuffer2.hasEventOfPacket(packet2))) {
			if (exitBuffer2.getNode().isSourceNode()) {
				long time = (long) exitBuffer2.physicalLayer.simulator.time();
				Event event = new CLeavingEXBEvent(exitBuffer2.physicalLayer.simulator, time, time, exitBuffer2,
						packet2);
				event.register(); // add a new event
			} else if (exitBuffer2.getNode() instanceof Switch) {
				long time = (long) exitBuffer2.physicalLayer.simulator.time();
				Event event = new FLeavingSwitchEvent(exitBuffer2.physicalLayer.simulator, time,
						time + Constant.SWITCH_CYCLE, exitBuffer2, packet2);
				event.register(); // add a new event
			}
		}
	}

	public void getNextState(Element e) {
	}

}
