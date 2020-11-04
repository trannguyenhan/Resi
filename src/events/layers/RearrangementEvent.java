package events.layers;

import custom.fattree.FatTreeFlowClassifier;
import events.IEventGenerator;
import infrastructure.event.Event;
import network.layers.NetworkLayer;
import simulator.DiscreteEventSimulator;

public class RearrangementEvent extends Event {

	public RearrangementEvent(DiscreteEventSimulator sim, long startTime, long endTime, IEventGenerator elem) {
		super(sim, endTime);
		this.element = elem;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public void actions() {
		DiscreteEventSimulator sim = DiscreteEventSimulator.getInstance();
		NetworkLayer networkLayer = (NetworkLayer) element;
		FatTreeFlowClassifier ftfc = (FatTreeFlowClassifier)networkLayer.routingAlgorithm;
		ftfc.update(null, null);
	}

}
