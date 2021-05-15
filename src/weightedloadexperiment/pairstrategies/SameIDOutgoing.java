package weightedloadexperiment.pairstrategies;

import java.util.ArrayList;
import java.util.List;
import common.RandomGenerator;
import custom.fattree.Address;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class SameIDOutgoing extends OverSubscription {
	private List<Integer> sources = getSources();
	private List<Integer> destinations = getDestinations();
	private int delta = RandomGenerator.nextInt(0, k * k * k / 4);
	private int sameHostID = -1;

	public SameIDOutgoing(FatTreeGraph g, FatTreeRoutingAlgorithm routing) {
		super();
		this.graph = g;
		this.routing = routing;
	}

	public SameIDOutgoing() {
		
	}

	public SameIDOutgoing(Integer[] allHosts) {
		super(allHosts);
	}

	@Override
	public void pairHosts() {
		setTypeOfAddresss();

		Integer[] allHosts = this.getAllHosts();
		int numOfHosts = allHosts.length;
		int count = 0;
		int i = 0;

		while (i < numOfHosts && count < numOfHosts * 100000) {
			sameHostID = -1;
			List<Integer> allTempDsts = new ArrayList<>();
			List<Integer> allTempSrcs = new ArrayList<>();

			addSrcAndDst(i, numOfHosts, allTempDsts, allTempSrcs, allHosts);

			if (allTempDsts.size() == k / 2) {
				getHostAddr(i, allTempSrcs, allTempDsts);
			} else {
				delta = RandomGenerator.nextInt(0, k * k * k / 4);
			}
			count++;
			i += k/2;
		}

	}

	private void addSrcAndDst(int i, int numOfHosts, List<Integer> allTempDsts, List<Integer> allTempSrcs,
			Integer[] allHosts) {
		for (int j = i; j < i + (k / 2); j++) {
			int src = allHosts[j];
			boolean found = false;

			for (int k = 0; k < numOfHosts; k++) {
				int dst = allHosts[(k + delta) % numOfHosts];
				if (dst != src && !destinations.contains(dst) && !allTempDsts.contains(dst)) {
					if (sameHostID == -1) {
						sameHostID = getHostID(dst);
						allTempDsts.add(dst);
						found = true;
						break;
					} else {
						if (sameHostID == getHostID(dst)) {
							allTempDsts.add(dst);
							found = true;
							break;
						}
					}
				}
			}
			if (found) {
				allTempSrcs.add(src);
			} else {
				break;
			}
		}
	}

	/**
	 * This method is used to add all temporary sources to a list of sources, and
	 * add all temporary destinations to a list of destinations. After that, print
	 * all temporary destinations with their hostIDs and print the address of hosts
	 * 
	 * @param i
	 * @param allTempSrcs
	 * @param allTempDsts
	 */
	private void getHostAddr(int i, List<Integer> allTempSrcs, List<Integer> allTempDsts) {

		i += k / 2;
		System.out.print("\n");
		sources.addAll(allTempSrcs);
		destinations.addAll(allTempDsts);

		for (int m = 0; m < allTempDsts.size(); m++) {
			System.out.print(allTempDsts.get(m) + "(" + getHostID(allTempDsts.get(m)) + ") ");
			int id = allTempDsts.get(m);
			Address host = graph.getAddress(id);
			System.out.print("Addr: " + host._1 + "." + host._2 + "." + host._3 + "." + host._4);
			System.out.println();
		}
		System.out.print("\n");
	}

	@Override
	public void setAllHosts(Integer[] allHosts) {
		super.setAllHosts(allHosts);
		this.k = (int) Math.cbrt(4 * (double)allHosts.length);
	}

	/**
	 * This method is used to check whether the previous host and the current host
	 * are in the same subnet or not
	 * 
	 * @param preHost  This is the previous host
	 * @param currHost This is the current host
	 * @return
	 */
	private boolean isSameSubNet(int preHost, int currHost) {
		if (Math.abs(preHost - currHost) >= k / 2) {
			return false;
		}
		if (preHost / (k / 2) == currHost / (k / 2)) {
			return true;
		}
		return false;
	}

	public int getHostID(int id) {
		Address host = graph.getAddress(id);
		int lastPart = host._4;
		int hostID = 0;
		if (lengthOfHostID == 8) {
			hostID = (lastPart << 24) >> 24;
		}

		if (lengthOfHostID == 16) {
			hostID = (lastPart << 16) >> 16;
		}

		if (lengthOfHostID == 24) {
			hostID = (lastPart << 8) >> 8;
		}
		return hostID;
	}

	private int lengthOfHostID = 8;

	private void setTypeOfAddresss() {
		Address one = graph.getAddress(0);
		int firstPart = one._1;
		int firstBit = firstPart >> 31;
		int firstTwoBits = firstPart >> 30;
		int firstThreeBits = firstPart >> 29;
		if (firstBit == 0) {
			lengthOfHostID = 24;
			return;
		}
		if (firstTwoBits == 1) {
			lengthOfHostID = 16;
			return;
		}
		if (firstThreeBits == 5) {
			lengthOfHostID = 8;
		}
	}

	/**
	 * This method is used to check whether there are enough pairs or not. Moreover,
	 * source nodes and destination nodes must have the same hostID
	 */
	@Override
	public void checkValid() {
		int realCore = 0;
		if (sources.size() != k * k * k / 4) {
			System.out.println("Not enough pairs! Just " + sources.size());
			for (int i = 0; i < sources.size(); i++) {
				realCore = getRealCoreSwitch(sources.get(i), destinations.get(i));
				System.out.println("From " + sources.get(i) + " through "
						+ getCoreSwitch(sources.get(i), destinations.get(i)) + "/" + realCore + " to "
						+ destinations.get(i) + "(HostID = " + getHostID(destinations.get(i)) + ")");
			}
			System.exit(0);
		}

		for (int i = 0; i < sources.size(); i++) {
			realCore = getRealCoreSwitch(sources.get(i), destinations.get(i));
			System.out.println("From " + sources.get(i) + " through "
					+ getCoreSwitch(sources.get(i), destinations.get(i)) + "/" + realCore + " to " + destinations.get(i)
					+ "(HostID = " + getHostID(destinations.get(i)) + ")");

		}
	}
}
