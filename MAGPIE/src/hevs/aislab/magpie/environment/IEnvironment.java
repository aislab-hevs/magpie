package hevs.aislab.magpie.environment;

import hevs.aislab.magpie.agent.MagpieAgent;
import hevs.aislab.magpie.context.ContextEntity;
import hevs.aislab.magpie.event.MagpieEvent;

import java.util.List;
import java.util.Map;

public interface IEnvironment {

	public void registerAgent(MagpieAgent agent);
	
	public void unregisterAgent(MagpieAgent agent);
	
	public void registerContextEntity(ContextEntity contextEntity);
	
	public void unregisterContextEntity(ContextEntity contextEntity);
	
	public List<MagpieAgent> getRegisteredAgents();
	
	public Map<String, ContextEntity> getRegisteredContextEntities();
	
	public ContextEntity getContextEntity(String service);

	public void registerEvent(MagpieEvent ev);
	
	public void registerAlert(MagpieEvent al);
}
