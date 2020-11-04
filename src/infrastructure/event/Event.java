package infrastructure.event;

import events.IEventGenerator;
import infrastructure.element.Element;
import network.elements.Packet;
import simulator.DiscreteEventSimulator;

public abstract class Event extends umontreal.ssj.simevents.Event {
	protected Packet packet; // packet ID
	protected long startTime;
	protected long endTime;
	public static int countSubEvent = 0;

	protected IEventGenerator element;

	public Event(DiscreteEventSimulator sim, long time) {
		super(sim);
		this.eventTime = (double) time;
	}

	public Packet getPacket() {
		return packet;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public IEventGenerator getElement() {
		return element;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * build method insertEvent to insert an event called ev
	 * @param ev
	 */
	public void register() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		if (sim == null)
			return;
		sim.addEvent(this);
	}

}
