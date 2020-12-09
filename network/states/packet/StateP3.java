package network.states.packet;

import infrastructure.event.Event;
import infrastructure.state.State;
import network.elements.Packet;
import network.elements.UnidirectionalWay;

public class StateP3 extends State {
	// � State P3: the packet is moved in a unidirectional way.

	public Packet packet;

	public StateP3(UnidirectionalWay unidirectionalWay, Packet p, Event ev) {
		this.element = unidirectionalWay;
		this.packet = p;
	}

	@Override
	public void act() {

	}
}
