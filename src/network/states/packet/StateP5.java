package network.states.packet;

import events.EMovingInSwitchEvent;
import infrastructure.state.State;
import network.elements.ExitBuffer;
import network.elements.Packet;

public class StateP5 extends State {
	// � State P5: the packet is located at EXB of switch.

	public Packet packet;

	public StateP5(ExitBuffer exitBuffer, Packet p, EMovingInSwitchEvent ev) {
		this.element = exitBuffer;
		this.packet = p;
		// this.ancestorEvent = ev;
	}

	@Override
	public void act() {

	}
}
