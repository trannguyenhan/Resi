package network.states.packet;

import events.DReachingENBEvent;
import infrastructure.state.State;
import network.elements.EntranceBuffer;
import network.elements.Packet;

public class StateP4 extends State {
	// � State P4: the packet is located at ENB of switch.

	public Packet packet;

	public StateP4(EntranceBuffer entranceBuffer, Packet p, DReachingENBEvent ev) {
		this.element = entranceBuffer;
		this.packet = p;
	}

	@Override
	public void act() {

	}
}
