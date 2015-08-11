package ch.hevs.aislab.magpie.debs.retrofit;


import java.util.Collection;

import ch.hevs.aislab.magpie.debs.model.MobileClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface UserSvcApi {

    String CLIENT_ID = "mobile";

    String SERVICE_URL = "https://10.0.2.2:8443";
    String TOKEN_PATH = "/oauth/token";

    String USER_ID = "userId";

    String USER_SVC = "/user";
    String USER_GET_SVC = USER_SVC + "/getUser";
    String USER_GET_CONTACTS_ACCEPTED_SVC = USER_SVC + "/{" + USER_ID + "}" + "/getContactsAccepted";
    String USER_GET_CONTACTS_PENDING_SVC = USER_SVC + "/{" + USER_ID + "}" + "/getContactsPending";


    @POST(USER_GET_SVC)
    MobileClient getUser(@Body String gcmToken);

    @GET(USER_GET_CONTACTS_ACCEPTED_SVC)
    Collection<MobileClient> getContactsAccepted(@Path(USER_ID) long userId);

    @GET(USER_GET_CONTACTS_PENDING_SVC)
    public Collection<MobileClient> getContactsPending(@Path(USER_ID) long userId);

}
