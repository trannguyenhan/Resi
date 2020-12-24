package network.states.packet;

import infrastructure.state.State;
import events.AGenerationEvent;
import network.elements.Packet;
import network.elements.SourceQueue;

public class StateP1 extends State {
	// � State P1: the packet is generated
	public Packet packet;

	public StateP1(SourceQueue sq, Packet p, AGenerationEvent ev) {
		this.element = sq;
		this.packet = p;
	}

	@Override
	public void act() {

	}
}
