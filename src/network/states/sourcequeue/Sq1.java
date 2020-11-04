package network.states.sourcequeue;

import infrastructure.event.Event;
import events.AGenerationEvent;
import network.elements.SourceQueue;
import infrastructure.state.State;

public class Sq1 extends State {
	// � State Sq1: source queue is empty.
	public Sq1(SourceQueue e) {
		this.element = e;
	}

	/**
	 * This method is used when an element change its state. When the element Source
	 * queue is in the state Sq1, it will check whether the coming event list has an
	 * event which generates the next packet or not. If not, it will create this event.
	 * The time of this event is in the future (one more Constant.HOST_DELAY)
	 */
	@Override
	public void act() {
		SourceQueue sourceQueue = (SourceQueue) element;
		long time = (long) sourceQueue.getNextPacketTime();
		Event event = new AGenerationEvent(sourceQueue.physicalLayer.simulator, time, time, element);
		event.register();

	}

}
