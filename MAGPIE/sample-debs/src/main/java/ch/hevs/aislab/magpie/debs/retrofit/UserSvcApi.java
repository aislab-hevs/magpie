package ch.hevs.aislab.magpie.debs.retrofit;


import ch.hevs.aislab.magpie.debs.model.MobileClient;
import retrofit.http.Body;
import retrofit.http.POST;

public interface UserSvcApi {

    String CLIENT_ID = "mobile";

    String SERVICE_URL = "https://10.0.2.2:8443";
    String TOKEN_PATH = "/oauth/token";

    String USER_SVC = "/user";
    String USER_GET_SVC = USER_SVC + "/getUser";

    @POST(USER_GET_SVC)
    MobileClient getUser(@Body String gcmToken);

}
