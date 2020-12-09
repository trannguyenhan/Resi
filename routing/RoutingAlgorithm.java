package routing;

import infrastructure.entity.Node;
import network.elements.Packet;

public interface RoutingAlgorithm {
	int next(int source, int current, int destination);

	RoutingPath path(int source, int destination);

	int next(Packet packet, Node node);

	RoutingAlgorithm build(Node node) throws CloneNotSupportedException;

	void update(Packet p, Node node);
}
