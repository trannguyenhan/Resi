package infrastructure.event;

import config.Constant;
import events.CLeavingEXBEvent;
import events.DReachingENBEvent;
import events.FLeavingSwitchEvent;
import events.GReachingDestinationEvent;
import events.IEventGenerator;
import infrastructure.element.Element;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.SourceQueue;
import network.elements.UnidirectionalWay;
import network.states.enb.N0;
import network.states.enb.N1;
import network.states.unidirectionalway.W0;
import network.states.unidirectionalway.W1;
import network.states.unidirectionalway.W2;
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
	 * This method is used to add a new event
	 */
	public void register() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		if (sim == null)
			return;
		sim.addEvent(this);
	}

	/**
	 * This method is used to create event A and B, which will be overridden in
	 * class AGenerationEvent
	 * 
	 * @param sim         This is the discrete event simulator
	 * @param sourceQueue This is the source queue
	 * @param newPacket   This is the new packet
	 */
	public void createEvent(DiscreteEventSimulator sim, SourceQueue sourceQueue, Packet newPacket) {

	}

	/**
	 * This method is used to create event C, which will be overridden in class
	 * BLeavingSourceQueueEvent
	 * 
	 * @param sourceQueue This is the source queue
	 * @param exitBuffer  This is the exit buffer
	 * @param sim         This is the discrete event simulator
	 */
	public void createEvent(SourceQueue sourceQueue, ExitBuffer exitBuffer, DiscreteEventSimulator sim) {

	}

	/**
	 * This method is used to create event type D, G, or F.
	 * 
	 * @param exitBuffer        This is the exit buffer
	 * @param unidirectionalWay This is the unidirectional way
	 * @param sim               This is the discrete event simulator
	 * @param type              This is the type of event. If type = 'D' then create
	 *                          event D. If type = 'G' then create event G. If type
	 *                          = 'F' then create event F
	 */
	public void createEvent(ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay, DiscreteEventSimulator sim,
			char type) {
		long time = (long) exitBuffer.physicalLayer.simulator.time();
		Event event = null;

		if (type == 'D') { // create event D
			event = new DReachingENBEvent(sim, time,
					time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay, packet);
		} else if (type == 'G') { // create event G
			event = new GReachingDestinationEvent(sim, time,
					time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay, packet);
		} else if (type == 'F') { // create event F
			event = new FLeavingSwitchEvent(sim, time, time + Constant.SWITCH_CYCLE, exitBuffer, packet);
		}
		event.register(); // add a new event
	}

	/**
	 * This method is used to change the state of entrance buffer, exit buffer or
	 * unidirectional way. And it will be overridden in subclasses
	 * 
	 * @param entranceBuffer    This is the entrance buffer
	 * @param exitBuffer        This is the exit buffer
	 * @param unidirectionalWay This is the unidirectional way
	 */
	public void changeState(EntranceBuffer entranceBuffer, ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {

	}

	/**
	 * This method is used to change the state of exit buffer to X00, X01, X10 or
	 * X11
	 * 
	 * @param exitBuffer This is the exit buffer
	 * @param state      This is the state of exit buffer, including X00, X01, X10
	 *                   and X11
	 */
	public void changeEXBState(ExitBuffer exitBuffer, String state) {
		if (state == "X00") {
			exitBuffer.setType(Type.X00); // change EXB state to X00
			exitBuffer.getState().act();
		} else if (state == "X01") {
			exitBuffer.setType(Type.X01); // change EXB state to X01
			exitBuffer.getState().act();
		} else if (state == "X10") {
			exitBuffer.setType(Type.X10); // change EXB state to X10
			exitBuffer.getState().act();
		} else if (state == "X11") {
			exitBuffer.setType(Type.X11); // change EXB state to X11
			exitBuffer.getState().act();
		}
	}

	/**
	 * This method is used to change the state of entrance buffer to N0 or N1
	 * 
	 * @param entranceBuffer This is the entrance buffer
	 * @param state          This is the state of entrance buffer, including N0 and
	 *                       N1
	 */
	public void changeENBState(EntranceBuffer entranceBuffer, String state) {
		if (state == "N0") {
			entranceBuffer.setState(new N0(entranceBuffer)); // change ENB state to N0
			entranceBuffer.getState().act();
		} else if (state == "N1") {
			entranceBuffer.setState(new N1(entranceBuffer)); // change ENB state to N1
			entranceBuffer.getState().act();
		}
	}

	/**
	 * This method is used to change the state of unidirectional way to W0, W1 or W2
	 * 
	 * @param unidirectionalWay This is the unidirectional way
	 * @param state             This is the state of unidirectional way, including
	 *                          W0, W1 and W2
	 */
	public void changeWayState(UnidirectionalWay unidirectionalWay, String state) {
		if (state == "W0") {
			unidirectionalWay.setState(new W0(unidirectionalWay)); // change state of way to W0
			unidirectionalWay.getState().act();
		} else if (state == "W1") {
			unidirectionalWay.setState(new W1(unidirectionalWay)); // change state of way to W1
			unidirectionalWay.getState().act();
		} else if (state == "W2") {
			unidirectionalWay.setState(new W2(unidirectionalWay)); // change state of way to W2
			unidirectionalWay.getState().act();
		}
	}
}
