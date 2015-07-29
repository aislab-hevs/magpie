package ch.hevs.aislab.magpie.debs.retrofit;


import ch.hevs.aislab.magpie.debs.model.RequestSubscriptionResult;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SubscriberSvcApi {

    String SUBSCRIBER_ID = "subId";

    String SUBSCRIBER_SVC = "/subscriber";
    String SUBSCRIBER_SUBSCRIBE_SVC = SUBSCRIBER_SVC + "/{" + SUBSCRIBER_ID + "}/subscribe";

    @POST(SUBSCRIBER_SUBSCRIBE_SVC)
    RequestSubscriptionResult doSubscriptionByUsername(
            @Path(SUBSCRIBER_ID) long subId, @Body String pubUsername);
}
