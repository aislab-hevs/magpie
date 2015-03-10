package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.magpie.environment.IEnvironment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentBody {

    public void senseEvent(MagpieEvent event);

	/** Activate is a method that will produce the behaviour of the agent */
	public void activate();
	
	public void doPerception();
	
	public void doBehaviour();

	public void setEnvironment(IEnvironment environment);
}