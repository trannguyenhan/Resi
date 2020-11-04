package network.entities;

import infrastructure.entity.Node;

public class Switch extends Node {
	public int numPorts = 0;

	public Switch(int id) {
		super(id);
	}

}