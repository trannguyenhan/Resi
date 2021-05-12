package custom.fattree;

import infrastructure.entity.Node;
import network.elements.Packet;

public class FatTreeFlowScheduler extends FatTreeRoutingAlgorithm {

	public FatTreeFlowScheduler(FatTreeGraph graph, boolean precomputed) {
		super(graph, precomputed);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int next(int source, int current, int destination) {
		// TODO Auto-generated method stub
		return super.next(source, current, destination);
	}
	
	public int next(Packet packet, Node node) {
		
		return 0;
	}
}
