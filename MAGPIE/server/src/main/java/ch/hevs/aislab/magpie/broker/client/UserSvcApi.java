package ch.hevs.aislab.magpie.broker.client;

import java.security.Principal;

import ch.hevs.aislab.magpie.broker.model.MobileClient;
import retrofit.http.Body;
import retrofit.http.POST;

public interface UserSvcApi {

	String USER_SVC = "/user";
	String USER_GET_SVC = USER_SVC + "/getUser";
	
	@POST(USER_GET_SVC)
	public MobileClient getMobileClient(@Body String gcmToken, Principal p);
	
}
