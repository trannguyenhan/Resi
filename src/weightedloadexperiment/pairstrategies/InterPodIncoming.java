package weightedloadexperiment.pairstrategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javatuples.*;

import common.RandomGenerator;
import custom.fattree.Address;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class InterPodIncoming extends OverSubscription {

	private int[][] adjMx;

	public InterPodIncoming(FatTreeRoutingAlgorithm routing, FatTreeGraph G) {
		super();
		this.routing = routing;
		this.G = G;
	}

	@Override
	public void setAllHosts(Integer[] allHosts) {
		super.setAllHosts(allHosts);
		this.k = (int) Math.cbrt(4 * allHosts.length);

		int numOfHosts = allHosts.length;

		adjMx = new int[numOfHosts][numOfHosts];
		for (int i = 0; i < numOfHosts; i++) {
			for (int j = 0; j < numOfHosts; j++) {
				if (i / (k * k / 4) != j / (k * k / 4)) {
					int src = allHosts[i];
					int dst = allHosts[j];
					int core = getCoreSwitch(src, dst);
					adjMx[i][j] = core;
				} else {
					adjMx[i][j] = 0;
				}
			}
		}

	}

	@Override
	public void pairHosts() {
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();

		Integer[] allHosts = this.getAllHosts();
		int delta = RandomGenerator.nextInt(0, k * k / 4);
		int numOfHosts = allHosts.length;
		int sizeOfPod = k * k / 4;
		int currPod = 0, prePod = 0;
		for (int i = 0; i < numOfHosts; i++) {
			int dst = allHosts[i];
			prePod = currPod;
			if (!destinations.contains(dst)) {
				int index = (i + sizeOfPod + delta) % numOfHosts;
				if (index / sizeOfPod == prePod) {
					index = (index + sizeOfPod) % numOfHosts;
					if (index / sizeOfPod == dst / sizeOfPod) {
						index = (index + sizeOfPod) % numOfHosts;
					}
				}
				int count = 0;
				int expectedSrc = allHosts[index];
				boolean found = false;
				while (!found && count < k) {
					if (sources.contains(expectedSrc)) {
						for (int j = index + 1; j < (index / sizeOfPod + 1) * sizeOfPod; j++) {
							expectedSrc = allHosts[j];
							if (!sources.contains(expectedSrc) && ((expectedSrc / sizeOfPod) != (dst / sizeOfPod))) {
								found = true;
								sources.add(expectedSrc);
								destinations.add(dst);
								if ((i + 1) % sizeOfPod == 0) {
									currPod = (i + 1) / sizeOfPod;
								} else {
									currPod = j / sizeOfPod;
								}

								sources.add(dst);
								destinations.add(expectedSrc);
								break;
							}
						}

					} // end of if(sources.contains(expectedSrc))
					else {
						if (expectedSrc / sizeOfPod != dst / sizeOfPod) {
							found = true;
							sources.add(expectedSrc);
							destinations.add(dst);

							sources.add(dst);
							destinations.add(expectedSrc);

							if ((i + 1) % sizeOfPod == 0) {
								currPod = (i + 1) / sizeOfPod;
							} else {
								currPod = index / sizeOfPod;
							}
							break;
						}
					}

					if (!found) {
						count++;
						index = (index + sizeOfPod) % numOfHosts;
					}
				}
				// end of while(!found && count < k)
			}
			// end of if(!destinations.contains(dst))
			else {
				currPod = i / sizeOfPod;
			}
		}
		this.setSources(sources);
		this.setDestinations(destinations);
	}

	@Override
	public void checkValid() {
		Map<Integer, Integer> flowPerCore = new HashMap<Integer, Integer>();
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();
		int realCore = 0;
		int sizeOfPod = k * k / 4;
		if (sources.size() != k * k * k / 4) {
			System.out.println("Not enough pair! Just " + sources.size());
			for (int i = 0; i < sources.size(); i++) {
				realCore = getRealCoreSwitch(sources.get(i), destinations.get(i));
				System.out.println(
						"From " + sources.get(i) + " through " + getCoreSwitch(sources.get(i), destinations.get(i))
								+ "/" + realCore + " to " + destinations.get(i));
			}
			System.exit(0);
		}

		for (int i = 0; i < sources.size(); i++) {
			realCore = getRealCoreSwitch(sources.get(i), destinations.get(i));
			System.out
					.println("From " + sources.get(i) + " through " + getCoreSwitch(sources.get(i), destinations.get(i))
							+ "/" + realCore + " to " + destinations.get(i));

			if (flowPerCore.containsKey(realCore)) {
				int value = flowPerCore.get(realCore) + 1;
				flowPerCore.put(realCore, value);
			} else {
				flowPerCore.put(realCore, 1);
			}

			if (sources.get(i) / sizeOfPod == destinations.get(i) / sizeOfPod) {
				System.out.print("Source and destination are in the same pod. INVALID!!!!");
				System.exit(0);
				// count++;
				break;
			}
		}

		int average = k;
		int equal = 0;
		for (int core : flowPerCore.keySet()) {
			if (average == flowPerCore.get(core)) {
				equal++;
			}
		}
		if (equal == k * k * k / 4) {

		}
	}

	public void transform(int[][] M, int length) {
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (i > j) {
					int temp = M[j][i];
					M[j][i] = M[i][j];
					M[i][j] = temp;
				}
			}
		}
	}

	public int getCoreSwitch(int source, int destination) {
		int edge = G.adj(source).get(0);
		int agg = G.adj(edge).get(k / 2);
		int core = G.adj(agg).get(k / 2);
		return core;
	}

	public int getRealCoreSwitch(int source, int destination) {
		int edge = G.adj(source).get(0);
		Address address = G.getAddress(destination);
		Map<Integer, Map<Integer, Integer>> suffixTables = routing.getSuffixTables();
		Map<Integer, Map<Triplet<Integer, Integer, Integer>, Integer>> prefixTables = routing.getPrefixTables();

		Map<Integer, Integer> suffixTable = suffixTables.get(edge);
		int suffix = address._4;
		int agg = suffixTable.get(suffix);

		Triplet<Integer, Integer, Integer> prefix = new Triplet<>(address._1, address._2, address._3);

		Map<Triplet<Integer, Integer, Integer>, Integer> prefixTable = prefixTables.get(agg);
		suffixTable = suffixTables.get(agg);

		if (prefixTable.containsKey(prefix)) {
			return prefixTable.get(prefix);
		} else {
			return suffixTable.get(suffix);
		}
	}

}
