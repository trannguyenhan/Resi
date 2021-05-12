package weightedloadexperiment.pairstrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import custom.fattree.FatTreeGraph;
import custom.fattree.FatTreeRoutingAlgorithm;

public class StaggeredProb extends InterPodIncoming{
	protected List<Integer> sources = getSources();
	protected List<Integer> destinations = getDestinations();
	
	protected Integer[] allHosts;
	
	protected double subnetP;
	protected double podP;
	
	public StaggeredProb(FatTreeRoutingAlgorithm routing, FatTreeGraph graph, double subnetP, double podP) {
		super(routing, graph);
		this.subnetP = subnetP;
		this.podP = podP;
	}
	
	@Override
	public void pairHosts() {
		allHosts = this.getAllHosts();
		
//		System.out.println(k);
//		System.out.println(allHosts.length);
		int a = (int) subnetP * 100;
		int b = (int) podP * 100;
		int c = (int) 100 - a - b;
		
		// build probability
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<a; i++) {
			list.add(0);
		}
		
		for(int i=0; i<b; i++) {
			list.add(1);
		}
		
		for(int i=0; i<c; i++) {
			list.add(c);
		}
		
		int lens = list.size();
		Random rand = new Random();
		
		for(int i=0; i<allHosts.length; i++) {
			sources.add(allHosts[i]);
			
			int de;
			int prodDe = list.get(rand.nextInt(lens));
			if(prodDe == 0) {
				de = sameSubnetP(i);
			} else if(prodDe == 1) {
				de = samePodP(i);
			} else {
				de = otherP(i);
			}
			
			destinations.add(allHosts[de]);
		}
		
//		sources.forEach(s -> System.out.println(s));
//		System.out.println("\n");
//		destinations.forEach(d -> System.out.println(d));
//		System.out.println("\n");
		this.setSources(sources);
		this.setDestinations(destinations);
	}
	
	/**
	 * return node b is same sub-net with node a
	 * @param a
	 * @return
	 */
	public int sameSubnetP(int a) {
		if(a % 2 ==0) {
			return a + 1;
		} else {
			return a - 1;
		}
	}
	
	/**
	 * return node b is same pod net with node a
	 * @param a
	 * @return
	 */
	public int samePodP(int a) {		
		List<Integer> list = new ArrayList<Integer>();
		int du = a % k;
		int st = a - du;
		int la = a + (k - 1 - du);
		
		for(int i=st; i<=la; i++) {
			if(i != a) {
				list.add(i);
			}
		}
		
		Random rand = new Random();
		
		return list.get(rand.nextInt(list.size()));
	}

	/**
	 * return node b is other node with 2 cases above
	 * @param a
	 * @return
	 */
	public int otherP(int a) {
		int du = a % k;
		int st = a - du;
		int la = a + (k - 1 - du);
		
		Random rand = new Random();
		int de = rand.nextInt(allHosts.length);
		
		while(de >= st && de <=la) {
			de = rand.nextInt(allHosts.length);
		}
		
		return de;
	}
}
