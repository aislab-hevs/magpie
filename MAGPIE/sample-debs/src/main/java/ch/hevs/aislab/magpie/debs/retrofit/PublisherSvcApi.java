package ch.hevs.aislab.magpie.debs.retrofit;


import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;
import ch.hevs.aislab.magpie.debs.model.SubscriptionResult;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface PublisherSvcApi {

    String PUBLISHER_ID = "pubId";

    String PUB_SVC = "/publisher";
    String PUB_CONFIRM_SVC = PUB_SVC + "/{" + PUBLISHER_ID + "}/confirmSubscription";
    String PUB_REVOKE_SVC = PUB_SVC + "/{" + PUBLISHER_ID + "}/revokeSubscription";
    String PUB_ALERT_SVC = PUB_SVC + "/{" + PUBLISHER_ID + "}/notifyAlert";


    @POST(PUB_CONFIRM_SVC)
    boolean confirmSubscription(
            @Path(PUBLISHER_ID) long pubId, @Body SubscriptionResult subscriptionResult);

    @POST(PUB_REVOKE_SVC)
    boolean revokeSubscription(
            @Path(PUBLISHER_ID) long pubId, @Body long subId);

    @POST(PUB_ALERT_SVC)
    boolean notifyAlert(
            @Path(PUBLISHER_ID) long pubId, @Body GlucoseAlert glucose);
}
