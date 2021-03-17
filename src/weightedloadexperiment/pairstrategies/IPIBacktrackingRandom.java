package weightedloadexperiment.pairstrategies;

import java.util.Random;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

/* The subject is suited to picking out a random pair that will produce different pairs each run
 * Note: If you want to find multiple pairs at the same time, you cannot use this object, it will find all the same objects in each run.
 * */
public class IPIBacktrackingRandom extends IPIBacktracking {

	public IPIBacktrackingRandom(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
	}

	protected Random random = new Random();

	@Override
	public void TRY(int a) {
		for (int j = 0; j < numOfHosts; j++) {
			int i = random.nextInt(numOfHosts);
				
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
