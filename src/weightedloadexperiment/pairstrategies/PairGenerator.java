package weightedloadexperiment.pairstrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import network.Topology;
import network.elements.UnidirectionalWay;
import network.entities.Link;

public abstract class PairGenerator {

	private Integer[] allHosts;

	public Integer[] getAllHosts() {
		return allHosts;
	}

	public void setAllHosts(Integer[] allHosts) {
		this.allHosts = allHosts;
	}

	public List<Integer> getSources() {
		return sources;
	}

	public List<Integer> getDestinations() {
		return destinations;
	}

	public void setSources(List<Integer> sources) {
		this.sources = sources;
	}

	public void setDestinations(List<Integer> destinations) {
		this.destinations = destinations;
	}

	private List<Integer> sources;
	private List<Integer> destinations;

	public PairGenerator() {
		sources = new ArrayList<Integer>();
		destinations = new ArrayList<Integer>();
	}

	public PairGenerator(Integer[] allHosts) {
		this.allHosts = allHosts;
		sources = new ArrayList<Integer>();
		destinations = new ArrayList<Integer>();
	}

	public abstract void pairHosts();

	public void checkValid() {
	}

	public void setUpBandwidth(Topology network) {

	}

	public static boolean isOversubscriptedLink(Link link, int maxIndexOfCore, int minIndexOfCore) {
		boolean result = false;

		Map<Integer, UnidirectionalWay> ways = link.Ways();
		for (UnidirectionalWay way : ways.values()) {
			int idFromNode = way.getFromNode().getId();
			int idToNode = way.getToNode().getId();
			if ((idFromNode >= minIndexOfCore && idFromNode <= maxIndexOfCore)
					|| (idToNode >= minIndexOfCore && idToNode <= maxIndexOfCore)) {
				result = true;
			}
		}
		return result;
	}
}
