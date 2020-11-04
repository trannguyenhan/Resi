package network.elements;

import config.Constant;
import infrastructure.element.Buffer;

//import network.states.packet.StateP1;
import network.states.sourcequeue.*;

public class SourceQueue extends Buffer {
	// todo nen bo thuoc tinh desID di ko vi the nay chi gioi han moi sourceNode chi
	// gui packet den duoc mot desNode duy nhat
	protected int sourceId;
	protected int destinationId;
	protected long numGeneratedPacket; // so packet da tao, khoi tao bawng -1

	public SourceQueue(int sourceId) {
		this.id = sourceId;
		this.sourceId = sourceId;
		this.destinationId = sourceId;
		this.numGeneratedPacket = -1;
		setState(new Sq1(this));
	}

	public SourceQueue(int sourceId, int destinationId) {
		this.id = sourceId;
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.numGeneratedPacket = -1; // so packet da tao
		setState(new Sq1(this));
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setDestinationID(int destinationID) {
		this.destinationId = destinationID;
	}

	public int getDestinationID() {
		return this.destinationId;
	}

	public boolean hasOnlyOnePacket() {
		return allPackets.size() == 1;
	}

	public Packet generatePacket(long currentTime) {
		if (this.isDelayed(currentTime))
			return null;

		numGeneratedPacket++;
		double timeSent = numGeneratedPacket * Constant.HOST_DELAY;
		Packet p = new Packet(0, sourceId, destinationId, timeSent);
//        p.setState( new StateP1(physicalLayer.sourceQueue, p)); // state set o ngoai khi event xay ra
		insertPacket(p);
		return p;
	}

	public Packet removePacket() {
		if (allPackets.size() == 1) {
			if (state instanceof Sq2) {
				state = new Sq1(this);
				state.act();
			}
		}
		return allPackets.dequeue();
	}

	public void insertPacket(Packet p) {
		allPackets.enqueue(p);
		if (state instanceof Sq1) {
			state = new Sq2(this);
		}

	}

	public boolean isDelayed(long currentTime) {
		long r = currentTime / Constant.HOST_DELAY; // host delay chi thoi gian can thiet de gen packet tiep theo

		return r <= numGeneratedPacket;
	}

	public double getNextPacketTime() {
		return (double) (numGeneratedPacket + 1) * Constant.HOST_DELAY;
	}

}
