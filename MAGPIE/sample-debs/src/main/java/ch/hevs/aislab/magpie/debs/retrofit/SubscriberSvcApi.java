package ch.hevs.aislab.magpie.debs.retrofit;


import java.util.Collection;

import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;
import ch.hevs.aislab.magpie.debs.model.RequestSubscriptionResult;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SubscriberSvcApi {

    String SUBSCRIBER_ID = "subId";
    String PUBLISHER_ID = "pubId";

    String SUB_SVC = "/subscriber";
    String SUB_SUBSCRIBE_SVC = SUB_SVC + "/{" + SUBSCRIBER_ID + "}/subscribe";
    String SUB_ALERTS_SVC = SUB_SVC + "/{" + SUBSCRIBER_ID + "}/alertsByUser";

    @POST(SUB_SUBSCRIBE_SVC)
    RequestSubscriptionResult doSubscriptionByUsername(
            @Path(SUBSCRIBER_ID) long subId, @Body String pubUsername);

    @GET(SUB_ALERTS_SVC)
    Collection<GlucoseAlert> getAlertsByUser(
            @Path(SUBSCRIBER_ID) long subId, @Query(PUBLISHER_ID) long pubId);
}
