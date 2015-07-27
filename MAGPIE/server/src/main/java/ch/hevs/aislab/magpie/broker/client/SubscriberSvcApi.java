package ch.hevs.aislab.magpie.broker.client;

import javax.servlet.http.HttpServletResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SubscriberSvcApi {

	String SUBSCRIBER_ID = "subId";
	
	String SUB_SVC = "/subscriber";
	String SUB_SUBSCRIBE_SVC = SUB_SVC + "/{" + SUBSCRIBER_ID + "}/subscribe";
	
	
	@POST(SUB_SUBSCRIBE_SVC)
	boolean doSubscriptionByUsername(
			@Path(SUBSCRIBER_ID) long subId,
			@Body String pubUsername,
			HttpServletResponse response);
	
}
