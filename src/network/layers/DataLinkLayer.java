package network.layers;

import network.elements.Packet;

public class DataLinkLayer extends Layer {
	public Packet packet;

	// cap nhat thong tin packet
	public DataLinkLayer(Packet p) {
		this.packet = p;
	}

	public void update(Packet p) {

	}
}
