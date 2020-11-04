package network.entities;

import infrastructure.event.Event;
import config.Constant;
import events.AGenerationEvent;
import infrastructure.entity.Node;
import network.elements.Packet;
import network.elements.SourceQueue;

/**
 * Created by Dandoh on 6/27/17.
 */

public class Host extends Node {

	public TypeOfHost type;

	public Host(int id) {
		super(id);
//	     this.physicalLayer = new PhysicalLayer(this);
	}

	@Override
	public void clear() {

	}

	public void generatePacket(int destination) {
		if (this.physicalLayer.sourceQueue == null)
			this.physicalLayer.sourceQueue = new SourceQueue(this.id, destination);
		else
			this.physicalLayer.sourceQueue.setDestinationID(destination);

		long time = (long) this.physicalLayer.simulator.time();
		Event ev = new AGenerationEvent(this.physicalLayer.simulator, time, time, this.physicalLayer.sourceQueue);
		ev.register();
	}

	protected int receivedPacketInNode = 0;
	protected double lastRx = 0; // thoi gian goi tin cuoi cung den host
	protected double firstTx = -1; // la thoi gian goi tin dau tien den host

	public int getReceivedPacketInNode() {
		return receivedPacketInNode;
	}

	public double getLastRx() {
		return lastRx;
	}

	public double getFirstTx() {
		return firstTx;
	}

	public void receivePacket(Packet packet) {
		double currentTime = this.physicalLayer.simulator.getTime();
		this.physicalLayer.simulator.numReceived++;
		if (this.receivedPacketInNode == 0) {
//           this.firstTx = packet.getStartTime();
			this.firstTx = currentTime;
			// System.out.println("Thoi gian goi tin dau tien den voi host " + self.id + "
			// la: " + this.firstTx);
		}
		this.receivedPacketInNode++;
		this.lastRx = currentTime;
		this.physicalLayer.simulator.receivedPacketPerUnit[(int) (currentTime / Constant.EXPERIMENT_INTERVAL + 1)]++;
		packet.setEndTime(currentTime);

		this.physicalLayer.simulator.totalPacketTime += packet.timeTravel();
		this.physicalLayer.simulator.totalHop += packet.nHop; // todo xem nHop la gi

	}

	@Override
	public boolean isDestinationNode() {
		return (this.type == TypeOfHost.Destination || this.type == TypeOfHost.Mix);
	}

	public boolean isSourceNode() {
		return (this.type == TypeOfHost.Source || this.type == TypeOfHost.Mix);
	}
}