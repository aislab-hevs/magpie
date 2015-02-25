package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.magpie.environment.IEnvironment;

public interface IAgentBody {

	/**
	 * Activate is a method that will produce the behaviour of the agent
	 */	
	public void activate();
	
	public void doPerception();
	
	public void doBehaviour();

	public void setEnvironment(IEnvironment env);
}