package infrastructure.event;

import config.Constant;
import events.AGenerationEvent;
import events.BLeavingSourceQueueEvent;
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
	 * This method is used to add an event
	 */
	public void register() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		if (sim == null)
			return;
		sim.addEvent(this);
	}
	
	/**
	 * This method is used to change the state of exit buffer, type X00
	 * 
	 * @param exitBuffer
	 */
	public void changeEXBStateX00(ExitBuffer exitBuffer) {
		exitBuffer.setType(Type.X00);
		exitBuffer.getState().act();
	}
	
	/**
	 * This method is used to change the state of exit buffer, type X01
	 * 
	 * @param exitBuffer
	 */
	public void changeEXBStateX01(ExitBuffer exitBuffer) {
		exitBuffer.setType(Type.X01);
		exitBuffer.getState().act();
	}
	
	/**
	 * This method is used to change the state of exit buffer, type X10
	 * 
	 * @param exitBuffer
	 */
	public void changeEXBStateX10(ExitBuffer exitBuffer) {
		exitBuffer.setType(Type.X10);
		exitBuffer.getState().act();
	}
	
	/**
	 * This method is used to change the state of exit buffer, type X11
	 * 
	 * @param exitBuffer
	 */
	public void changeEXBStateX11(ExitBuffer exitBuffer) {
		exitBuffer.setType(Type.X11);
		exitBuffer.getState().act();
	}
	
	/**
	 * This method is used to change state of entrance buffer, type N0
	 * 
	 * @param entranceBuffer
	 */
	public void changeENBStateN0(EntranceBuffer entranceBuffer) {
		entranceBuffer.setState(new N0(entranceBuffer));
		entranceBuffer.getState().act();
	}
	
	/**
	 * This method is used to change state of entrance buffer, type N1
	 * 
	 * @param entranceBuffer
	 */
	public void changeENBStateN1(EntranceBuffer entranceBuffer) {
		
		entranceBuffer.setState(new N1(entranceBuffer));
		entranceBuffer.getState().act();
	}
	
	/**
	 * This method is used to the state of unidirectioncal way, type W0
	 * 
	 * @param unidirectionalWay
	 */
	public void changeWayStateW0(UnidirectionalWay unidirectionalWay) {
		unidirectionalWay.setState(new W0(unidirectionalWay));
		unidirectionalWay.getState().act();
	}
	
	/**
	 * This method is used to the state of unidirectioncal way, type W1
	 * 
	 * @param unidirectionalWay
	 */
	public void changeWayStateW1(UnidirectionalWay unidirectionalWay) {
		
		unidirectionalWay.setState(new W1(unidirectionalWay));
		unidirectionalWay.getState().act();
	}
	
	/**
	 * This method is used to the state of unidirectioncal way, type W2
	 * 
	 * @param unidirectionalWay
	 */
	public void changeWayStateW2(UnidirectionalWay unidirectionalWay) {
		
		unidirectionalWay.setState(new W2(unidirectionalWay));
		unidirectionalWay.getState().act();
	}
	
	/**
	 * This method is used to add event A
	 * 
	 * @param sim
	 * @param sourceQueue
	 */
	public void addEventA(DiscreteEventSimulator sim, SourceQueue sourceQueue) {
		
		long time = (long) sourceQueue.getNextPacketTime();
		Event ev = new AGenerationEvent(sim, time, time, sourceQueue);

		sim.addEvent(ev);
	}
	
	/**
	 * This method is used to add event B
	 * 
	 * @param sim
	 * @param sourceQueue
	 * @param newPacket
	 */
	public void addEventB(DiscreteEventSimulator sim, SourceQueue sourceQueue, Packet newPacket) {
		
		long time = (long) sim.time();
		Event event = new BLeavingSourceQueueEvent(sim, time, time, sourceQueue, newPacket);
		
		sim.addEvent(event);
	}
	
	
	/**
	 * This method is used to add event C
	 * 
	 * @param sourceQueue
	 * @param exitBuffer
	 * @param sim
	 */
	public void addEventC(SourceQueue sourceQueue, ExitBuffer exitBuffer, DiscreteEventSimulator sim) {

		long time = (long) sourceQueue.physicalLayer.simulator.time();
		Event event = new CLeavingEXBEvent(sim, time, time, exitBuffer, packet);
		
		event.register();// add a new event
	}

	/**
	 * This method is used to add event D
	 * 
	 * @param exitBuffer
	 * @param unidirectionalWay
	 * @param sim
	 */
	public void addEventD(ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay, DiscreteEventSimulator sim) {
		
		long time = (long) exitBuffer.physicalLayer.simulator.time();
		Event event = new DReachingENBEvent(sim, time,
				time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay, packet);
		
		event.register(); // add a new event
	}

	/**
	 * This method is used to add event G
	 * 
	 * @param exitBuffer
	 * @param unidirectionalWay
	 * @param sim
	 */
	public void addEventG(ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay, DiscreteEventSimulator sim) {
		
		long time = (long) exitBuffer.physicalLayer.simulator.time();
		Event event = new GReachingDestinationEvent(sim, time,
				time + unidirectionalWay.getLink().getTotalLatency(packet.getSize()), unidirectionalWay, packet);
	
		event.register(); // add a new event
	}

	/**
	 * This method is used to add event F
	 * 
	 * @param exitBuffer
	 * @param sim
	 */
	public void addEventF(ExitBuffer exitBuffer, DiscreteEventSimulator sim) {
		
		long time = (long) exitBuffer.physicalLayer.simulator.time();
		Event event = new FLeavingSwitchEvent(sim, time, time + Constant.SWITCH_CYCLE, exitBuffer, packet);
		
		event.register();
	}
}
