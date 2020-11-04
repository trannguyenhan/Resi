package network.entities;

import config.Constant;
import infrastructure.entity.Device;
import infrastructure.entity.Node;
import network.elements.UnidirectionalWay;
import weightedloadexperiment.pairstrategies.OverSubscription;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Link extends Device {

	private Map<Integer, UnidirectionalWay> ways; // key int is the ID of toNode in way
	private long bandwidth;

	public long getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}

	private double length;

	public Link(Node u, Node v) { // link co 2 unidirectional way nen can put 2 new way moi theo 2 chieu khi khoi
									// tao
		super(0);
		this.ways = new HashMap<>();
		ways.put(u.getId(), new UnidirectionalWay(u, v, this));
		ways.put(v.getId(), new UnidirectionalWay(v, u, this));

		this.bandwidth = Constant.LINK_BANDWIDTH;
		this.length = Constant.DEFAULT_LINK_LENGTH;
	}

	public Link(Node u, Node v, double length) {
		this(u, v);
		this.length = length;
	}

	public Node getOtherNode(Node node) {
		return ways.get(node.getId()).getToNode();
	}

	public UnidirectionalWay getWayToOtherNode(Node node) {
		return ways.get(node.getId());
	}

	public Map<Integer, UnidirectionalWay> Ways() {
		return ways;
	}

	public long serialLatency(int packetSize) {
		// if(packetSize != 100000 && this.bandwidth != 1e9)
		// System.out.println("INFO: " + packetSize + " " + this.bandwidth);
		if (OverSubscription.isOversubscriptedLink(this, 35, 32)) {
			if (this.bandwidth != OverSubscription.OVERSUBSCRIPTION_BANDWIDTH) {
				System.exit(0);
			}
		} else {
			if (this.bandwidth != OverSubscription.NORMAL_BANDWIDTH) {
				System.exit(0);
			}
		}
		return (long) (1e9 * packetSize / this.bandwidth);
	}

	public long propagationLatency() {
		// if(length != Constant.DEFAULT_LINK_LENGTH && length !=
		// Constant.HOST_TO_SWITCH_LENGTH)
		// System.out.println("!!!!!!!!!Length = " + length);
		return (long) (length / Constant.PROPAGATION_VELOCITY);
	}

	public long getTotalLatency(int packetSize) {
		return serialLatency(packetSize) + propagationLatency();
	}

	public double getLength() {
		return this.length;
	}
}
