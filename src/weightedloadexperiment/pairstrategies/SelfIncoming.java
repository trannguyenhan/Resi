package weightedloadexperiment.pairstrategies;

import java.util.Arrays;
import java.util.List;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class SelfIncoming extends InterPodIncoming{
	private List<Integer> sources = getSources();
	private List<Integer> destinations = getDestinations();
	
	public SelfIncoming(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void pairHosts() {
		List<Integer> tmpSources = Arrays.asList(0,1,2,3,8,9,10,11,16,17,18,19,24,25,26,27);
		List<Integer> tmpDestinations = Arrays.asList(11,19,8,24,17,25,2,18,9,27,0,26,1,3,16,10);
		
		for(int i=0; i<tmpSources.size(); i++) {
			sources.add(tmpSources.get(i));
		}
		
		for(int i=0; i<tmpDestinations.size(); i++) {
			destinations.add(tmpDestinations.get(i));
		}
		
		this.setSources(sources);
		this.setDestinations(destinations);
		
	}

}
