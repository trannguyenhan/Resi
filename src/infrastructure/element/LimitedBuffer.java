package infrastructure.element;

import infrastructure.entity.Node;
import network.elements.Packet;

public abstract class LimitedBuffer extends Buffer {
	// should be set to protected
	protected Node node; // can be removed
	protected Node connectNode; // can not be removed, because from buffer, we do not know which node will be
								// connected
	protected int size;

	public Node getConnectNode() {
		return connectNode;
	}

	public Node getNode() {
		return node;
	}

	// to be overridden
	public void checkStateChange() {
	}

	/**
	 * The method will insert packet p into its buffer
	 * 
	 * @param p The packet needs inserting
	 * @return true if the packet is inserted successfully; false if the packet is
	 *         inserted unsuccessfully (the buffer is full)
	 */
	public void insertPacket(Packet p) {
		if (allPackets.size() > size)
			System.out.println("ERROR: Buffer: " + this.toString() + " oversized");
		allPackets.enqueue(p);
	}

	/**
	 * This method is used to remove and return the packet in the queue that was
	 * least recently added
	 * 
	 * @return null if the queue of all packets is empty; otherwise, return the
	 *         packet in the queue that was least recently added
	 */
	public Packet removePacket() {
		if (allPackets.isEmpty())
			return null;
		return allPackets.dequeue();
	}

	/**
	 * This method is used to check whether the buffer is over-sized or not
	 * 
	 * @return true if the buffer's size = the size of all packets; false otherwise
	 */
	public boolean isFull() {
		if (allPackets.size() > size)
			System.out.println("ERROR: Buffer: " + this.toString() + " oversized");
		return allPackets.size() == size;
	}

	/**
	 * This method is used to get the number of packets in the queue
	 * 
	 * @return the number of packets in the queue
	 */
	public int getNumOfPacket() {
		if (allPackets.size() > size)
			System.out.println("ERROR: Buffer: " + this.toString() + " oversized");
		return allPackets.size();
	}

	/**
	 * This method is used to check whether a packet can be added to the limited
	 * buffer or not
	 * 
	 * @return true if the size of all packets < the size of the buffer; false
	 *         otherwise
	 */
	public boolean canAddPacket() {
		return allPackets.size() < size;
	}
}
