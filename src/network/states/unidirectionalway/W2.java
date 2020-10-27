package network.states.unidirectionalway;

import infrastructure.state.State;
import network.elements.UnidirectionalWay;

public class W2 extends State {
	// � State W2: the way has no packet but it is unable to transfer.

	public W2(UnidirectionalWay unidirectionalWay) {
		this.element = unidirectionalWay;
	}

	@Override
	public void act() {

	}
}
