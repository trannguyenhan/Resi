package infrastructure.element;

import common.Queue;
import network.elements.Packet;
import network.layers.PhysicalLayer;

public abstract class Buffer extends Element {
	protected Queue<Packet> allPackets;
	public PhysicalLayer physicalLayer;

	protected Buffer() {
		allPackets = new Queue<>();
	}

	/**
	 * This method is used to check whether a packet is the first packet (the packet
	 * least recently added) in the queue or not
	 * 
	 * @param packet the packet needs checking
	 * @return true if the packet is a peek packet; false if the queue of all
	 *         packets is empty or the packet is not a peek packet
	 */
	public boolean isPeekPacket(Packet packet) {
		if (allPackets.isEmpty())
			return false;
		return allPackets.peek() == packet;
	}

	/**
	 * This method is used to check whether the queue of all packets is empty or not
	 * 
	 * @return true if this queue is empty; false otherwise
	 */
	public boolean isEmpty() {
		return allPackets.isEmpty();
	}

	/**
	 * This method is used to get the packet least recently added to the queue of
	 * all packets
	 * 
	 * @return the packet least recently added to the queue of all packets
	 */
	public Packet getPeekPacket() {
		if (allPackets.isEmpty())
			return null;
		return allPackets.peek();
	}

}
