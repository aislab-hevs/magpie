package ch.hevs.aislab.magpie.context;

import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.RuleSetEvent;
import ch.hevs.aislab.magpie.support.MagpieSvcApi;
import ch.hevs.aislab.magpie.support.Rule;

import java.util.Collection;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

public class RestClientContextEntity extends ContextEntity {

	private String endPointEmulator = "http://10.0.2.2:8080";
	
	private String endPointPhone = "http://192.168.173.1:8080";
		
	private RestAdapter restAdapter;
	
	private MagpieSvcApi magpieSvc; 
	
	private static final String service = Services.REST_CLIENT;
	
	public RestClientContextEntity() {
		super(service);
		
		if (android.os.Build.MODEL.contains("google_sdk") ||
			    android.os.Build.MODEL.contains("Emulator")) {
			// emulator
			restAdapter = new RestAdapter.Builder()
			.setEndpoint(endPointEmulator)
			.setLogLevel(LogLevel.FULL)
			.build();
		} else {
			//not emulator
			restAdapter = new RestAdapter.Builder()
			.setEndpoint(endPointPhone)
			.setLogLevel(LogLevel.FULL)
			.build();
		}
		
		this.magpieSvc = restAdapter.create(MagpieSvcApi.class);
	}
	
	public RuleSetEvent getRules() {
		Collection<Rule> rules = magpieSvc.getRuleList();
		return new RuleSetEvent(rules);
	}
	
	public LogicTupleEvent postAlert(LogicTupleEvent alert) {
		return magpieSvc.postAlert(alert);
	}
}