package events.layers;

import custom.fattree.FatTreeFlowClassifier;
import events.IEventGenerator;
import infrastructure.event.Event;
import network.layers.NetworkLayer;
import simulator.DiscreteEventSimulator;

public class RearrangementEvent extends Event {
	private DiscreteEventSimulator simulator;
	
	public RearrangementEvent(DiscreteEventSimulator sim, long startTime, long endTime, IEventGenerator elem) {
		super(sim, endTime);
		this.element = elem;
		this.startTime = startTime;
		this.endTime = endTime;
		this.simulator = sim;
	}

	@Override
	public void actions() {
		NetworkLayer networkLayer = (NetworkLayer) element;
		FatTreeFlowClassifier ftfc = (FatTreeFlowClassifier) networkLayer.routingAlgorithm;
//		ftfc.update(null, null);
		
		ftfc.rearrangeFlows();
		
		Event event = new RearrangementEvent(simulator, startTime + 1, startTime + 1, networkLayer);
		event.register();
	}
}
