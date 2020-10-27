package events;

import infrastructure.state.State;

public interface IEventGenerator {
	void setState(State state);

	State getState();
}
