package network.states.unidirectionalway;

import infrastructure.state.State;
import network.elements.UnidirectionalWay;

public class W1 extends State {
	// � State W1: the way has a packet.

	public W1(UnidirectionalWay unidirectionalWay) {
		this.element = unidirectionalWay;
	}

	@Override
	public void act() {
	}
}
