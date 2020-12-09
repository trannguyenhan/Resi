package network.elements;

import infrastructure.element.LimitedBuffer;
import infrastructure.entity.Node;
import network.states.enb.N0;

public class EntranceBuffer extends LimitedBuffer {
	protected int nextNodeId;

	public EntranceBuffer(Node node, Node connectNode, int size) {
		this.node = node;
		this.size = size;
		this.connectNode = connectNode;
		this.nextNodeId = -1;
		this.setState(new N0(this));
	}

	public void checkStateChange() {
	}

	public int getNextNodeId() {
		return nextNodeId;
	}

	public void dropNextNode() {
		this.nextNodeId = -1;
	}

	public void setNextNode(int nextNodeId) {
		this.nextNodeId = nextNodeId;
	}

	public boolean hasNextNode() {
		return !(nextNodeId == -1);
	}
}
