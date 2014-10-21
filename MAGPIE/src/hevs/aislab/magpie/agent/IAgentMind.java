package hevs.aislab.magpie.agent;

import hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentMind {
	
	public void updatePerception(MagpieEvent event);
	public MagpieEvent produceAction();
	public void update();
}
