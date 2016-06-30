package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentMind {

	void updatePerception(MagpieEvent event);
	MagpieEvent produceAction(long timestamp);
}
