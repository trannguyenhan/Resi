package weightedloadexperiment.pairstrategies;

import java.util.List;
import java.util.Map;

import custom.fattree.Address;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;
import javatuples.Triplet;
import network.Topology;
import network.entities.Link;
import network.entities.Switch;

public abstract class OverSubscription extends PairGenerator {

	public static final int OVERSUBSCRIPTION_BANDWIDTH = 106670000;
	public static final int NORMAL_BANDWIDTH = 96 * 1000 * 1000;
	public int modulo;

	public int k;

	public FatTreeRoutingAlgorithm routing;
	public FatTreeGraph G;

	public OverSubscription() {

	}

	public OverSubscription(Integer[] allHosts) {
		super(allHosts);

	}

	@Override
	public void setUpBandwidth(Topology network) {
		Integer[] allHosts = getAllHosts();

		List<Switch> switches = network.getSwitches();

		int k = (int) Math.cbrt(4 * allHosts.length);
		int maxIndexOfCore = allHosts.length + 5 * k * k / 4 - 1;
		int minIndexOfCore = maxIndexOfCore - k + 1;

		for (Switch sw : switches) {
			for (Link link : sw.physicalLayer.links.values()) {
				if (isOversubscriptedLink(link, maxIndexOfCore, minIndexOfCore)) {
					link.setBandwidth(OVERSUBSCRIPTION_BANDWIDTH);
				} else {
					link.setBandwidth(NORMAL_BANDWIDTH);
				}
			}

		}
	}

	@Override
	public void setAllHosts(Integer[] allHosts) {
		super.setAllHosts(allHosts);
		this.modulo = allHosts.length;
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
