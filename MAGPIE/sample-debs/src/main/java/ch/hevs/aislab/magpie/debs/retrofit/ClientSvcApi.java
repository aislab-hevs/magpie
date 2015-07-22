package ch.hevs.aislab.magpie.debs.retrofit;


import ch.hevs.aislab.magpie.debs.model.MobileClient;
import retrofit.http.Body;
import retrofit.http.POST;

public interface ClientSvcApi {

    String SERVICE_URL = "https://10.0.2.2:8443";

    String TOKEN_PATH = "/oauth/token";

    String CLIENT_SVC = "/client";

    @POST(CLIENT_SVC)
    MobileClient getRoles(@Body String gcmToken);

}
