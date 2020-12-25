package weightedloadexperiment.pairstrategies;

import java.util.Arrays;
import java.util.List;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class ForcePair extends InterPodIncoming {

	private int modulo = 0;

	public ForcePair(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
		this.modulo = 0;
	}

	public ForcePair(FatTreeRoutingAlgorithm routing, FatTreeGraph graph, int modulo) {
		super(routing, graph);
		this.modulo = modulo;
	}

	@Override
	public void checkValid() {
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();
		int realCore = 0;

		for (int i = 0; i < sources.size(); i++) {
			realCore = getRealCoreSwitch(sources.get(i), destinations.get(i));
			System.out
					.println("From " + sources.get(i) + " through " + getCoreSwitch(sources.get(i), destinations.get(i))
							+ "/" + realCore + " to " + destinations.get(i));
		}
	}

	@Override
	public void pairHosts() {
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();
		if (this.modulo != 0) {
			Integer[] pairs = pairHostsByModulo(this.modulo);
			sources.addAll(Arrays.asList(pairs));
		} else {
			sources.add(1); 
			sources.add(2); 
			sources.add(3); 
			sources.add(0); 

			sources.add(9); 
			sources.add(10); 
			sources.add(11); 
			sources.add(18); 

			sources.add(8); 
			sources.add(17);
			sources.add(26); 
			sources.add(19); 

			sources.add(16); 
			sources.add(25); 
			sources.add(27); 
			sources.add(24); 
		}

		destinations.add(11);
		destinations.add(16);
		destinations.add(25);
		destinations.add(18);

		destinations.add(19);
		destinations.add(24);
		destinations.add(26);
		destinations.add(27);

		destinations.add(0);
		destinations.add(1);
		destinations.add(2);
		destinations.add(3);

		destinations.add(8);
		destinations.add(9);
		destinations.add(10);
		destinations.add(17);

		this.setSources(sources);
		this.setDestinations(destinations);
	}

	private Integer[] pairHostsByModulo(int x) {
		Integer[] results = new Integer[16];
		if (x == 1) {
			results = new Integer[] { 2, 3, 0, 1, 10, 11, 8, 19, 9, 18, 27, 16, 17, 26, 24, 25 };
		}
		if (x == 2) {
			results = new Integer[] { 3, 0, 1, 2, 11, 8, 9, 16, 10, 19, 24, 17, 18, 27, 25, 26 };
		}
		if (x == 3) {
			results = new Integer[] { 0, 1, 2, 3, 8, 9, 10, 17, 11, 16, 25, 18, 19, 24, 26, 25 };
		}
		if (x == 4) {
			results = new Integer[] { 1, 2, 3, 26, 9, 10, 11, 18, 8, 17, 16, 19, 0, 25, 27, 24 };
		}
		if (x == 13) {
			results = new Integer[] { 0, 26, 9, 11, 1, 3, 19, 17, 18, 24, 10, 25, 2, 16, 27, 8 };
		}
		if (x == 12) {
			results = new Integer[] { 25, 18, 16, 27, 0, 26, 3, 17, 2, 8, 11, 9, 10, 24, 19, 1 };
		}
		if (x == 11) {
			results = new Integer[] { 1, 10, 19, 8, 9, 18, 16, 17, 26, 27, 24, 25, 2, 3, 0, 11 };
		}
		if (x == 10) {
			results = new Integer[] { 25, 18, 16, 27, 0, 26, 10, 17, 2, 8, 11, 9, 3, 24, 19, 1 };
		}
		if (x == 9) {
			results = new Integer[] { 25, 2, 16, 3, 0, 18, 19, 17, 10, 8, 11, 9, 26, 24, 27, 1 };
		}
		if (x == 8) {
			results = new Integer[] { 24, 26, 9, 27, 0, 2, 3, 8, 10, 17, 11, 16, 18, 25, 19, 1 };
		}
		if (x == 5) {
			results = new Integer[16];
			for (int i = 0; i < results.length; i++) {
				results[i] = 27;
			}
		}
		if (x == 6) // Achieve 50%
		{
			results = new Integer[] { 1, 26, 8, 10, 0, 3, 19, 16, 11, 9, 27, 17, 2, 25, 18, 24 };
		}
		if (x == 7) // Achieve 50%
		{
			results = new Integer[] { 27, 9, 2, 1, 3, 17, 16, 10, 8, 18, 24, 11, 0, 19, 25, 26 };
		}
		return results;
	}
}
