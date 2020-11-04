package infrastructure.element;

import common.Queue;
import network.elements.Packet;
import network.layers.PhysicalLayer;

public abstract class Buffer extends Element {
	protected Queue<Packet> allPackets;
	public PhysicalLayer physicalLayer;

	public Buffer() {
		allPackets = new Queue<>();
	}

	public boolean isPeekPacket(Packet packet) {
		if (allPackets.isEmpty())
			return false;
		return allPackets.peek() == packet;
	}

	public boolean isEmpty() {
		return allPackets.isEmpty();
	}

	public Packet getPeekPacket() {
		if (allPackets.isEmpty())
			return null;
		return allPackets.peek();
	}

}
