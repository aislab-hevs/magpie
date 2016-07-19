package ch.hevs.aislab.magpie.environment;

import java.util.Map;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IEnvironment {

	void registerAgent(MagpieAgent agent);
	
	void unregisterAgent(MagpieAgent agent);

    Map<Integer, MagpieAgent> getRegisteredAgents();
	
	void registerContextEntity(ContextEntity contextEntity);
	
	void unregisterContextEntity(ContextEntity contextEntity);
	
	Map<String, ContextEntity> getRegisteredContextEntities();
	
	ContextEntity getContextEntity(String service);

	void registerAlert(MagpieEvent al);
}
