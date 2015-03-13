package ch.hevs.aislab.magpie.environment;

import java.util.Map;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IEnvironment {

	public void registerAgent(MagpieAgent agent);
	
	public void unregisterAgent(MagpieAgent agent);

    public Map<Integer, MagpieAgent> getRegisteredAgents();
	
	public void registerContextEntity(ContextEntity contextEntity);
	
	public void unregisterContextEntity(ContextEntity contextEntity);
	
	public Map<String, ContextEntity> getRegisteredContextEntities();
	
	public ContextEntity getContextEntity(String service);

	public MagpieEvent registerEvent(MagpieEvent ev);
	
	public void registerAlert(MagpieEvent al);
}
