package ch.hevs.aislab.magpie.support;

import java.util.Collection;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface MagpieSvcApi {

	String RULE_SVC_PATH = "/rule";
	
	String ALERT_SVC_PATH = "/alert";
	
	@GET(RULE_SVC_PATH)
	Collection<Rule> getRuleList();
	
	@POST(ALERT_SVC_PATH)
	LogicTupleEvent postAlert(@Body LogicTupleEvent alert);
	
}
