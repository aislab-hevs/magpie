package ch.hevs.aislab.magpie.support;

import java.util.Collection;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface MagpieSvcApi {

	public static final String RULE_SVC_PATH = "/rule";
	
	public static final String ALERT_SVC_PATH = "/alert";
	
	@GET(RULE_SVC_PATH)
	public Collection<Rule> getRuleList();
	
	@POST(ALERT_SVC_PATH)
	public LogicTupleEvent postAlert(@Body LogicTupleEvent alert);
	
}
