package network.layers.states;

import events.layers.RearrangementEvent;
import infrastructure.event.Event;
import infrastructure.state.State;
import network.layers.NetworkLayer;
import simulator.DiscreteEventSimulator;

public class ReuseRoutingState extends State {

	public ReuseRoutingState(NetworkLayer networkLayer) {
		this.element = networkLayer;
	}

	public void act() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		NetworkLayer networkLayer = (NetworkLayer) element;
		// if(notYetAddGenerationEvent(sourceQueue))//Kiem tra xem Source Queue da co
		// event tao goi tin moi chua?
		{
			long time = networkLayer.getDurrationTime();
			long now = (long) sim.getTime();
			Event event = new RearrangementEvent(sim, now, now + time, element);
			event.register();
		}
	}

}
