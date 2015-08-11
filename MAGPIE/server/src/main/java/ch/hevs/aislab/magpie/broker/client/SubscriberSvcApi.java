package ch.hevs.aislab.magpie.broker.client;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import ch.hevs.aislab.magpie.broker.model.GlucoseAlert;
import ch.hevs.aislab.magpie.broker.model.RequestSubscriptionResult;

public interface SubscriberSvcApi {

	String SUBSCRIBER_ID = "subId";
	String PUBLISHER_ID = "pubId";
	
	String SUB_SVC = "/subscriber";
	String SUB_SUBSCRIBE_SVC = SUB_SVC + "/{" + SUBSCRIBER_ID + "}/subscribe";
	String SUB_ALERTS_SVC = SUB_SVC + "/{" + SUBSCRIBER_ID + "}/alertsByUser";
	
	@POST(SUB_SUBSCRIBE_SVC)
	RequestSubscriptionResult doSubscriptionByUsername(
			@Path(SUBSCRIBER_ID) long subId,
			@Body String pubUsername,
			HttpServletResponse response);
	
	@GET(SUB_ALERTS_SVC)
	Collection<GlucoseAlert> getAlertsByUser(
			@Path(SUBSCRIBER_ID) long subId,
			@Query(PUBLISHER_ID) long pubId,
			HttpServletResponse response);

}
