package network.elements;

import infrastructure.element.LimitedBuffer;
import infrastructure.entity.Node;
import infrastructure.state.State;
import infrastructure.state.Type;

import java.util.ArrayList;

public class ExitBuffer extends LimitedBuffer {
	protected ArrayList<EntranceBuffer> requestList;

	public ExitBuffer(Node node, Node connectNode, int size) {
		this.node = node;
		this.size = size;
		this.connectNode = connectNode;
		this.requestList = new ArrayList<>();
		State s = new State();
		s.element = this;
		s.type = Type.X01;
		this.setState(s);
	}

	public ArrayList<EntranceBuffer> getRequestList() {
		return requestList;
	}

	public void addToRequestList(EntranceBuffer entranceBuffer) {
		requestList.add(entranceBuffer);
	}

	public void removeFromRequestList(EntranceBuffer entranceBuffer) {
		if (requestList.contains(entranceBuffer)) {
			requestList.remove(entranceBuffer);
		} else
			System.out.println("ERROR: ExitBuffer: " + this.toString() + " does not contain request id: " + id);
	}

	public boolean isRequestListEmpty() {
		return requestList.isEmpty();
	}

	@Override
	public void checkStateChange() {
	}
}
