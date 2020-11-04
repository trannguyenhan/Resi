package weightedloadexperiment.pairstrategies.interpod;

import java.util.ArrayList;
import java.util.List;
import common.RandomGenerator;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import weightedloadexperiment.pairstrategies.InterPodIncoming;

public class MinimalCoreSwitches extends InterPodIncoming {

	private int[][] coreInPath;
	private int[] allCores;

	private int[] oversubscriptedCores;

	public MinimalCoreSwitches(FatTreeRoutingAlgorithm routing, FatTreeGraph G) {
		super(routing, G);

	}

	@Override
	public void setAllHosts(Integer[] allHosts) {
		super.setAllHosts(allHosts);
		this.k = (int) Math.cbrt(4 * allHosts.length);

		int numOfHosts = allHosts.length;

		coreInPath = new int[numOfHosts][numOfHosts];
		for (int i = 0; i < numOfHosts; i++) {
			for (int j = 0; j < numOfHosts; j++) {
				int source = getHostIndex(i);
				int dest = getHostIndex(j);
				int core = getRealCoreSwitch(source, dest);
				coreInPath[i][j] = core;
			}
		}

		allCores = new int[k * k / 4];
		int minCore = k * k * k / 4 + k * k;
		for (int i = 0; i < k * k / 4; i++) {
			allCores[i] = i + minCore;
		}
	}

	@Override
	public void pairHosts() {
		int delta = RandomGenerator.nextInt(0, k * k / 4);
		int numOfOversubscriptedCores = k * k / 8;
		oversubscriptedCores = new int[numOfOversubscriptedCores];
		delta = delta % numOfOversubscriptedCores;

		for (int i = delta; (i - delta) < numOfOversubscriptedCores; i++) {
			oversubscriptedCores[i - delta] = allCores[i % (k * k / 4)];
		}

		delta = delta + RandomGenerator.nextInt(0, k * k / 4);
		delta = delta % numOfOversubscriptedCores;
		List<Integer> dests = getDestinations();

		List<Integer> usedPods = new ArrayList<Integer>();

		for (int pod = 0; pod < k; pod++) {
			usedPods = new ArrayList<Integer>();
			int indexOfFirstCore = delta;
			for (int offset = 0; offset < k * k / 4; offset++) {
				int i = pod * k * k / 4 + offset;
				int dst = getHostIndex(i);
				if (isCoreAvailable(dst, indexOfFirstCore, usedPods)) {
					dests.add(dst);
				}

			}
		}
	}
	
	// Input is the host's index in the list of hosts from 0 to 15
	// Returns host's ID in the list of nodes: 0..3, 8..11, 16..19, 24..27
	private int getHostIndex(int i) {
		int result = 0;
		int pod = i / (k * k / 4);
		int delta = i % (k * k / 4);
		result = pod * (k * k / 4 + k) + delta;
		return result;
	}

	private boolean isCoreAvailable(int dst, int firstIndex, List<Integer> usedPods) {
		int countOfLoop = 0;
		boolean found = false;
		List<Integer> sources = getSources();
		List<Integer> dests = getDestinations();
		if (dests.contains(dst)) {
			return false;
		}

		while (countOfLoop < oversubscriptedCores.length && !found) {
			int delta = RandomGenerator.nextInt(0, k * k * k / 4);

			for (int i = 0; i < k * k * k / 4; i++) {
				int src = getHostIndex((i + delta) % (k * k * k / 4));
				if (!sources.contains(src) && src != dst && (src / (k * k / 4 + k) != dst / (k * k / 4 + k))) {
					if (getRealCoreSwitch(src, dst) == oversubscriptedCores[firstIndex]) {
						if (isFromAcceptablePod(usedPods, src, dst)) {
							sources.add(src);
							found = true;
							return found;
						}
					}
				}
			}
			if (!found) {
				firstIndex = (firstIndex + 1) % oversubscriptedCores.length;
			}
			countOfLoop++;
		}
		return found;
	}

	private boolean isFromAcceptablePod(List<Integer> usedPods, int src, int dst) {

		int pod = src / (k * k / 4 + k);
		if (usedPods.size() == 0) {
			usedPods.add(pod);
			return true;
		} else {
			if (!usedPods.contains(pod)) {
				usedPods.add(pod);
				return true;
			} else {
				int delta = (k - 1) - usedPods.size();
				int remaining = (k * k / 4) - dst % (k * k / 4);
				if (delta < remaining && delta != 0) {
					return false;
				} else {
					return true;
				}
			}
		}

	}

}
