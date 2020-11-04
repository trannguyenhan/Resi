package network.layers;

import network.elements.Packet;

public class DataLinkLayer extends Layer {
	public Packet packet;

	// update the packet's information
	public DataLinkLayer(Packet p) {
		this.packet = p;
	}

	public void update(Packet p) {

	}
}
