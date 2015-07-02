package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentMind {

	public void updatePerception(MagpieEvent event);
	public MagpieEvent produceAction(long timestamp);
}
