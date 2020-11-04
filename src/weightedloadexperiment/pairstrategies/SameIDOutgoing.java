package weightedloadexperiment.pairstrategies;

import java.util.ArrayList;
import java.util.List;
import common.RandomGenerator;
import custom.fattree.Address;
import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class SameIDOutgoing extends OverSubscription {

	public SameIDOutgoing(FatTreeGraph G, FatTreeRoutingAlgorithm routing) {
		super();
		this.G = G;
		this.routing = routing;
	}

	public SameIDOutgoing() {
		// TODO Auto-generated constructor stub
	}

	public SameIDOutgoing(Integer[] allHosts) {
		super(allHosts);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void pairHosts() {
		setTypeOfAddresss();
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();

		Integer[] allHosts = this.getAllHosts();
		int numOfHosts = allHosts.length;
		int previousSrc = 0;
		int sameHostID = -1;
		int delta = RandomGenerator.nextInt(0, k * k * k / 4);
		int count = 0;

		int i = 0;
		while (i < numOfHosts && count < numOfHosts * 1000) {
			sameHostID = -1;
			List<Integer> allTempDsts = new ArrayList<Integer>();
			List<Integer> allTempSrcs = new ArrayList<Integer>();

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

			if (allTempDsts.size() == k / 2) {
				i += k / 2;
				System.out.print("\n");
				sources.addAll(allTempSrcs);
				destinations.addAll(allTempDsts);
				for (int m = 0; m < allTempDsts.size(); m++) {
					System.out.print(allTempDsts.get(m) + "(" + getHostID(allTempDsts.get(m)) + ") ");
					int id = allTempDsts.get(m);
					Address host = G.getAddress(id);
					System.out.print("Addr: " + host._1 + "." + host._2 + "." + host._3 + "." + host._4);
					System.out.println();
				}
				System.out.print("\n");
			} else {
				delta = RandomGenerator.nextInt(0, k * k * k / 4);
			}
			count++;
		}
	}

	@Override
	public void setAllHosts(Integer[] allHosts) {
		super.setAllHosts(allHosts);
		this.k = (int) Math.cbrt(4 * allHosts.length);
	}

	private boolean IsSameSubNet(int preHost, int currHost) {
		if (Math.abs(preHost - currHost) >= k / 2) {
			return false;
		}
		if (preHost / (k / 2) == currHost / (k / 2)) {
			return true;
		}
		return false;
	}

	public int getHostID(int id) {
		Address host = G.getAddress(id);
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
		Address one = G.getAddress(0);
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
			return;
		}

	}

	@Override
	public void checkValid() {
		List<Integer> sources = getSources();
		List<Integer> destinations = getDestinations();
		int realCore = 0;
		if (sources.size() != k * k * k / 4) {
			System.out.println("Not enough pair! Just " + sources.size());
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
