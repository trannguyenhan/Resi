package network.states.sourcequeue;

import infrastructure.state.State;
import network.elements.SourceQueue;

public class Sq2 extends State {
	// � State Sq2: source queue is not empty.
	public Sq2(SourceQueue sourceQueue) {
		this.element = sourceQueue;
	}

	@Override
	public void act() {

	}

}
