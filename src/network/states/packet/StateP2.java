package network.states.packet;

import events.BLeavingSourceQueueEvent;
import infrastructure.state.State;
import network.elements.ExitBuffer;
import network.elements.Packet;

public class StateP2 extends State {
	// � State P2: the packet is located at EXB of the source node.
	public Packet packet;

	public StateP2(ExitBuffer exitBuffer, Packet p, BLeavingSourceQueueEvent ev) {
		this.element = exitBuffer;
		this.packet = p;
		// this.ancestorEvent = ev;
	}

	@Override
	public void act() {
	}
}
