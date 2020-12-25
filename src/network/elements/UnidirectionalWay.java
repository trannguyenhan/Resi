package network.elements;

import infrastructure.element.Element;
import infrastructure.entity.Node;
import network.entities.Link;
import network.states.unidirectionalway.W0;

public class UnidirectionalWay extends Element {
	private Node fromNode;
	private Node toNode;
	private Link link;
	private Packet packet;

	public UnidirectionalWay(Node from, Node to, Link link) {
		this.fromNode = from;
		this.toNode = to;
		this.link = link;
		this.packet = null;
		this.setState(new W0(this));
	}

	public Link getLink() {
		return link;
	}

	public Packet getPacket() {
		return packet;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public Packet removePacket() {
		Packet p = this.packet;
		this.packet = null;
		return p;
	}

	public void addPacket(Packet packet) {
		this.packet = packet;
	}
}
