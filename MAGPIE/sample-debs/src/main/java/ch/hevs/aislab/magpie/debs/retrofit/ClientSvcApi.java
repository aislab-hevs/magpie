package ch.hevs.aislab.magpie.debs.retrofit;


import ch.hevs.aislab.magpie.debs.model.MobileClient;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ClientSvcApi {

    String SERVICE_URL = "https://10.0.2.2:8443";
    String TOKEN_PATH = "/oauth/token";

    String CLIENT_SVC = "/client";
    String SUBSCRIBER_SVC = "/subscriber";
    String SUBSCRIBER_SUBSCRIBE_SVC = SUBSCRIBER_SVC + "/subscribe";

    @POST(CLIENT_SVC)
    MobileClient getUser(@Body String gcmToken);

    @POST(SUBSCRIBER_SUBSCRIBE_SVC)
    boolean doSubscriptionByUsername(@Body String username);

}
