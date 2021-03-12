package weightedloadexperiment.pairstrategies;

import java.util.Random;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class IPIBacktrackingRandom extends IPIBacktracking{

	public IPIBacktrackingRandom(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
	}
	
	protected Random random = new Random();
	
	@Override
	public void TRY(int a) {
		for (int i = 0; i < numOfHosts; i++) {
			i = random.nextInt(numOfHosts);
			
			if (exit == 1)
				return;

			int src = allHosts[a];
			int dst = allHosts[i];
			if (check(src, dst, a, i)) {
				pair[a] = i;
				mark[i] = true;

				if (a == numOfHosts - 1)
					solution();
				else
					TRY(a + 1);

				mark[i] = false;
			}
		}
	}
}
