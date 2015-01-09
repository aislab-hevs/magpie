package ch.hevs.aislab.magpie.environment;

import java.util.List;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.GPSContextEntity;
import ch.hevs.aislab.magpie.context.RestClientContextEntity;
import ch.hevs.aislab.magpie.support.MultiMap;

public class ServiceActivator {
	
	private static MultiMap<String, Integer> agentsInterests = new MultiMap<String, Integer>();
		
	protected static void registerInterests(MagpieAgent agent) {
				
		Environment env = Environment.getInstance();
		List<String> interests = agent.getInterests();
		
		for (String interest : interests) {
			agentsInterests.put(interest, agent.getId());
			
			if ((interest.equals(Services.GPS_LOCATION)) &&
					(!env.getRegisteredContextEntities().containsKey(Services.GPS_LOCATION))) {
				GPSContextEntity gps = new GPSContextEntity();
				env.registerContextEntity(gps);
				gps.init();
			}
			
			if ((interest.equals(Services.REST_CLIENT)) &&
					(!env.getRegisteredContextEntities().containsKey(Services.REST_CLIENT))) {
				RestClientContextEntity restClient = new RestClientContextEntity();
				env.registerContextEntity(restClient);				
			}
		}
		
	}
	
}
