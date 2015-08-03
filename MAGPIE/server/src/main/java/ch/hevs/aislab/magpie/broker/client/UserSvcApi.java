package ch.hevs.aislab.magpie.broker.client;

import java.security.Principal;
import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import ch.hevs.aislab.magpie.broker.model.MobileClient;

public interface UserSvcApi {

	String USER_ID = "userId";
	
	String USER_SVC = "/user";
	String USER_GET_SVC = USER_SVC + "/getUser";
	String USER_GET_CONTACTS_ACCEPTED_SVC = USER_SVC + "/{" + USER_ID + "}" + "/getContactsAccepted";
	String USER_GET_CONTACTS_PENDING_SVC = USER_SVC + "/{" + USER_ID + "}" + "/getContactsPending";
	
	@POST(USER_GET_SVC)
	public MobileClient getMobileClient(@Body String gcmToken, Principal p);
	
	@GET(USER_GET_CONTACTS_ACCEPTED_SVC)
	public Collection<MobileClient> getContactsAccepted(@Path(USER_ID) long userId);
	
	@GET(USER_GET_CONTACTS_PENDING_SVC)
	public Collection<MobileClient> getContactsPending(@Path(USER_ID) long userId);
}
