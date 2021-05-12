package weightedloadexperiment.pairstrategies;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

/* Improvements from IPI to be able to generate many different pairing ways in one run
 * 
 * */
public class IPIBacktrackingRandomImprove extends IPIBacktrackingRandom {

	public IPIBacktrackingRandomImprove(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
	}

	@Override
	public void pairHosts() {
		int scount = 12; // modify number of test in here
		
		listSources = new ArrayList<List<Integer>>();
		listDestinations = new ArrayList<List<Integer>>();

		allHosts = this.getAllHosts();
		numOfHosts = allHosts.length;
		pair = new int[numOfHosts];
		mark = new boolean[numOfHosts];

		total_devices = k * k * k / 4 + 5 * k * k / 4;
		each_devices_in_hostpod = (k * k) / 4 + k;
		
		for (int j = 0; j < scount; j++) {
			exit = 0;
			count = 0;

			for (int i = 0; i < numOfHosts; i++) {
				mark[i] = false;
			}

			TRY(0);
		}

		/* choose one result in list result */
		int index = random.nextInt(scount - 1);
		addDestinationsAndSource(index);

		this.setSources(sources);
		this.setDestinations(destinations);

//		try {
//			printFile(scount - 2);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@Override
	public boolean stopWithCout(int count) {
		if(count == 1) return true;
		return false;
	}
}
