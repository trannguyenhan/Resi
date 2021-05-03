package weightedloadexperiment.pairstrategies;

import java.util.HashMap;
import java.util.Map;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class IPIHalfCoreSwitchRandom extends IPIBacktrackingRandom{
	public IPIHalfCoreSwitchRandom(FatTreeRoutingAlgorithm routing, FatTreeGraph graph) {
		super(routing, graph);
	}

	@Override
	public boolean check(int src, int dst, int a, int i) {
		if (src == dst)
			return false;
		if (checkHostInPod(src, dst))
			return false;
		if (mark[i])
			return false;
		
		Map<Integer, Integer> mapCoreInUse = new HashMap<Integer, Integer>(); // number of flow pass core i
		for (int j = 0; j < a; j++) {
			int tmpCoreSW = getRealCoreSwitch(allHosts[j], allHosts[pair[j]]);
			if(mapCoreInUse.containsKey(tmpCoreSW)) {
				mapCoreInUse.put(tmpCoreSW, mapCoreInUse.get(tmpCoreSW) + 1);
			} else {
				mapCoreInUse.put(tmpCoreSW, 1);
			}
		}
		int nowCoreSW = getCoreSwitch(src, dst);
		if(mapCoreInUse.containsKey(nowCoreSW)) {
			mapCoreInUse.put(nowCoreSW, mapCoreInUse.get(nowCoreSW));
		} else {
			mapCoreInUse.put(nowCoreSW, 1);
		}
		
		if(mapCoreInUse.size() > k * k / 8) {
			return false;
		}
		
		return true;
	}
}
