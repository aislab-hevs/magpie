package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.magpie.environment.IEnvironment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentBody {

    void senseEvent(MagpieEvent event);

	/** Activate is a method that will produce the behaviour of the agent */
	void activate();
	
	void doPerception();
	
	void doBehaviour();

	void setEnvironment(IEnvironment environment);
}