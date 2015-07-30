package ch.hevs.aislab.magpie.broker.client;

import java.security.Principal;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.model.SubscriptionStatus;

public interface UserSvcApi {

	String USER_ID = "userId";
	
	String USER_SVC = "/user";
	String USER_GET_SVC = USER_SVC + "/getUser";
	String USER_GET_CONTACTS_SVC = USER_SVC + "/{" + USER_ID + "}" + "/getContacts";
	
	@POST(USER_GET_SVC)
	public MobileClient getMobileClient(@Body String gcmToken, Principal p);
	
	@GET(USER_GET_CONTACTS_SVC)
	public Map<MobileClient, SubscriptionStatus> getContacts(@Path(USER_ID) long userId);
	
}
