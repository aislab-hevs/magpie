package ch.hevs.aislab.magpie.broker.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.hevs.aislab.magpie.broker.client.SubscriberSvcApi;
import ch.hevs.aislab.magpie.broker.gcm.GcmSender;
import ch.hevs.aislab.magpie.broker.model.GlucoseAlert;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.model.RequestSubscriptionResult;
import ch.hevs.aislab.magpie.broker.model.SubscriptionStatus;
import ch.hevs.aislab.magpie.broker.repository.GlucoseAlertRepo;
import ch.hevs.aislab.magpie.broker.repository.MobileClientRepo;

@Controller
public class SubscriberController implements SubscriberSvcApi {
	
    @Autowired
    private MobileClientRepo mobileclients;
    
    @Autowired
    private GlucoseAlertRepo glucosealerts;
    
    @Override
    @RequestMapping(value = SUB_SUBSCRIBE_SVC, method = RequestMethod.POST)
	public @ResponseBody RequestSubscriptionResult doSubscriptionByUsername(
			@PathVariable(SUBSCRIBER_ID) long subId,
			@RequestBody String pubUsername,
			HttpServletResponse response) {
    	if (!mobileclients.exists(subId)) {
    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    		return RequestSubscriptionResult.INVALID_SUBSCRIBER;
    	}
    	
    	MobileClient subscriber = mobileclients.findOne(subId);
    	String subUsername = subscriber.getUsername();
    	
    	// Remove quotation marks from the body
		pubUsername = pubUsername.substring(1, pubUsername.length()-1);
		MobileClient publisher = mobileclients.findByUsername(pubUsername);
		
		// The client must exist and have a publisher role
		if ((publisher == null) || (!publisher.getRoles().contains(MobileClient.ROLE_PUBLISHER))) {
			return RequestSubscriptionResult.INVALID_PUBLISHER;
		}
		String pubGcmToken = publisher.getGcmToken();
		
		// Check that the subscription is not pending confirmation or already accepted
		SubscriptionStatus status = subscriber.getClients().get(publisher);
		if (status != null) {
			switch (status) {
				case PENDING:
					return RequestSubscriptionResult.STATUS_PENDING;
				case ACCEPTED:
					return RequestSubscriptionResult.STATUS_ALREADY_SUBSCRIBED;
				default:
					break;
			}
		}
		
		try {
			GcmSender.requestSubscription(subId, subUsername, pubGcmToken);
		} catch (IOException e) {
			e.printStackTrace();
			return RequestSubscriptionResult.GCM_ERROR;
		}
		
		// Update the status of the subscription
		publisher.getClients().put(subscriber.getId(), SubscriptionStatus.PENDING);
		mobileclients.save(publisher);
		subscriber.getClients().put(publisher.getId(), SubscriptionStatus.PENDING);
		mobileclients.save(subscriber);
		return RequestSubscriptionResult.STATUS_OK;
	}

	@Override
	@RequestMapping(value = SUB_ALERTS_SVC, method = RequestMethod.GET)
	public @ResponseBody Collection<GlucoseAlert> getAlertsByUser(
			@PathVariable(SUBSCRIBER_ID) long subId,
			@RequestParam(PUBLISHER_ID) long pubId,
			HttpServletResponse response) {
				
		// Check that exists a publisher with the provided id
		if ((!mobileclients.exists(pubId)) || (!mobileclients.exists(subId))) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		return glucosealerts.findByPublisherId(pubId);		
	}
}
