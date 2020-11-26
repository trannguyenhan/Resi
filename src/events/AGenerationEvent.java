package events;

import infrastructure.event.Event;
import infrastructure.state.Type;
import network.elements.SourceQueue;
import network.elements.Packet;
import network.states.sourcequeue.Sq1;
import network.states.sourcequeue.Sq2;
import simulator.DiscreteEventSimulator;

public class AGenerationEvent extends Event {

	/**
	 * This is the constructor event of AGenerationEvent class extending Event
	 * class. This is the event which represents a type (A) event: packet is
	 * generated
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 */
	public AGenerationEvent(DiscreteEventSimulator sim, long startTime, long endTime, IEventGenerator elem) {
		super(sim, endTime);
		this.element = elem;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	// starting from event type (A)
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();

		SourceQueue sourceQueue = (SourceQueue) getElement();
		Packet newPacket = sourceQueue.generatePacket(this.getStartTime());
		if (newPacket == null) {
			return;
		}

		newPacket.setId(sim.numSent++);
		this.setPacket(newPacket);
		newPacket.setType(Type.P1);

		updateSrcQueue(sourceQueue); // update Source Queue
		addEventB(sim, sourceQueue, newPacket); // add event B
		addEventA(sim, sourceQueue); // add event A

	}

	/**
	 * This method is used to update Source Queue
	 * 
	 * @param sourceQueue
	 */
	private void updateSrcQueue(SourceQueue sourceQueue) {
		// update source queue's state
		if (sourceQueue.getState() instanceof Sq1) // it means that element is an instance of SourceQueue
		{
			sourceQueue.setState(new Sq2(sourceQueue));
		}
	}

}
