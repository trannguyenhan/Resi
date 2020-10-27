package network.entities;

import infrastructure.entity.Node;

/**
 * Created by Dandoh on 6/27/17.
 */
public class Switch extends Node {
	public int numPorts = 0;

	public Switch(int id) {
		super(id);
	}

}