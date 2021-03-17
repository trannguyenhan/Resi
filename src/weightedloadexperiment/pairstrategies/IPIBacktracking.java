package weightedloadexperiment.pairstrategies;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

/* Exhaust each case one by one to find all the ways of pairing
 * Note: Cannot be used with large k (causes combinatorial bursts, with k> 6 cannot be run)
 * */
public class IPIBacktracking extends InterPodIncoming {
	protected List<List<Integer>> listSources;
	protected List<List<Integer>> listDestinations;

	protected List<Integer> sources = getSources();
	protected List<Integer> destinations = getDestinations();
	protected int pair[];
	protected boolean mark[];
	
	protected int total_devices;
	protected int each_devices_in_hostpod;
	protected int exit = 0;
	protected int count = 0;
	protected Integer[] allHosts;
	protected int numOfHosts;

	public IPIBacktracking(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
	}

	@Override
	public void pairHosts() {
		listSources = new ArrayList<List<Integer>>();
		listDestinations = new ArrayList<List<Integer>>();

		allHosts = this.getAllHosts();
		numOfHosts = allHosts.length;
		pair = new int[numOfHosts];
		mark = new boolean[numOfHosts];
		
		total_devices = k*k*k/4 + 5*k*k/4;
		each_devices_in_hostpod = (k*k)/4+k;
		
		for (int i = 0; i < numOfHosts; i++) {
			mark[i] = false;
		}

		TRY(0);

		Random rand = new Random();
		System.out.println("There are " + count + " pair source to destination");
		int index = rand.nextInt(count);
		addDestinationsAndSource(index);
		
		this.setSources(sources);
		this.setDestinations(destinations);
		
		try {
			printFile(count);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Print result to file text
	 * */
	public void printFile(int count) throws FileNotFoundException {
		for(int i=0; i<count; i++) {
			List<Integer> tmpSource = listSources.get(i);
			List<Integer> tmpDestination = listDestinations.get(i);
			int lens = tmpSource.size();
			
			String fileName = "data/result_" + Integer.toString(k) + "_" + Integer.toString(i) + ".txt";
			PrintWriter printWriter = new PrintWriter(fileName);
			
			printWriter.write(k + "\n");
			for(int j=0; j<lens; j++) {
				printWriter.write(tmpSource.get(j) + " " + tmpDestination.get(j) + "\n");
			}
			
			printWriter.close();
		}
	}
	
	public void addDestinationsAndSource(int index) {
		for (int i = 0; i < listDestinations.get(index).size(); i++) {
			sources.add(listSources.get(index).get(i));
			destinations.add(listDestinations.get(index).get(i));
		}
	}

	public void TRY(int a) {
		for (int i = 0; i < numOfHosts; i++) {
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

	public void solution() {
		count++;
		if (count == 30)
			exit = 1;

		List<Integer> tmpSources = new ArrayList<>();
		List<Integer> tmpDestinations = new ArrayList<>();
		for (int i = 0; i < numOfHosts; i++) {
			// System.out.println(allHosts[i] + " " + allHosts[pair[i]]);
			tmpSources.add(allHosts[i]);
			tmpDestinations.add(allHosts[pair[i]]);
		}

		listSources.add(tmpSources);
		listDestinations.add(tmpDestinations);
		
		System.out.println("find result " + count);
	}

	public boolean check(int src, int dst, int a, int i) {
		if (src == dst)
			return false;
		if (checkHostInPod(src, dst))
			return false;
		if (mark[i])
			return false;

		// The targets of the same group have node sources in different groups
		int t = a / k;
		int d = a % k;
		int beg = t * k;
		int en = t * k + d;

		for (int j = beg; j < en; j++) {
			if (checkHostInPod(allHosts[j], dst)) {
				return false;
			}
		}

		// All threads go to the same port with at least one other thread
		int numberHostsUseCoreSW[] = new int[total_devices];
		for (int j = 0; j < a; j++) {
			int tmpCoreSW = getRealCoreSwitch(allHosts[j], allHosts[pair[j]]);
			numberHostsUseCoreSW[tmpCoreSW]++;
		}
		int nowCoreSW = getRealCoreSwitch(src, dst);
		numberHostsUseCoreSW[nowCoreSW]++;
		if (numberHostsUseCoreSW[nowCoreSW] > k)
			return false;

		//Check threads
		for(int j=0; j<total_devices; j++) {
			numberHostsUseCoreSW[j] = 0;
		}
		
		if (a != 0 && a % k == 0) {
			t = (a-1) / k;
			d = (a-1) % k;
			beg = t * k;
			en = t *k + d;
			for(int j=beg; j<=en; j++) {
				int tmpCoreSW = getRealCoreSwitch(allHosts[j], allHosts[pair[j]]);
				numberHostsUseCoreSW[tmpCoreSW]++;
			}
			
			for(int j=0; j<total_devices; j++) {
				if(numberHostsUseCoreSW[j] != 0 && numberHostsUseCoreSW[j] != k/2) return false;
			}
		}
		
		return true;
	}

	/*
	 * Check that host number a and b are on the same pod
	 */
	public boolean checkHostInPod(int a, int b) {
		int podA = a / each_devices_in_hostpod;
		int podB = b / each_devices_in_hostpod;

		if (podA == podB)
			return true;
		return false;
	}
}
