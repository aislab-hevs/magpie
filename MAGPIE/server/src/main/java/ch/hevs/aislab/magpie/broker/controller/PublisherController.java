package ch.hevs.aislab.magpie.broker.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.hevs.aislab.magpie.broker.client.PublisherSvcApi;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.model.SubscriptionResult;
import ch.hevs.aislab.magpie.broker.model.SubscriptionStatus;
import ch.hevs.aislab.magpie.broker.repository.MobileClientRepo;

@Controller
public class PublisherController implements PublisherSvcApi {

	@Autowired
	private MobileClientRepo mobileclients;
	
	@Override
	@RequestMapping(value = PUB_CONFIRM_SVC, method = RequestMethod.POST)
	public @ResponseBody boolean confirmSubscription(
			@PathVariable(PUBLISHER_ID) long pubId,
			@RequestBody SubscriptionResult subscriptionResult,
			HttpServletResponse response) {
		
		long subId = subscriptionResult.getSubscriberId();
		
		System.out.println("Publisher ID: " + pubId);
		System.out.println("Subscriber ID: " + subId);
		System.out.println("Decision: " + subscriptionResult.isDecision());
		
		
		// Check that exists a publisher with the provided id
		if ((!mobileclients.exists(pubId)) || (!mobileclients.exists(subId))) {
    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    		return false;
    	}
		
		MobileClient publisher = mobileclients.findOne(pubId);
		MobileClient subscriber = mobileclients.findOne(subId);
		boolean decision = subscriptionResult.isDecision();
		
		if (decision) {
			publisher.getClients().put(subscriber.getId(), SubscriptionStatus.ACCEPTED);
			mobileclients.save(publisher);
			subscriber.getClients().put(publisher.getId(), SubscriptionStatus.ACCEPTED);
			mobileclients.save(subscriber);
		} else {
			publisher.getClients().put(subscriber.getId(), SubscriptionStatus.REJECTED);
			mobileclients.save(publisher);
			subscriber.getClients().put(publisher.getId(), SubscriptionStatus.REJECTED);
			mobileclients.save(subscriber);
		}
		return true;
	}

	@Override
	@RequestMapping(value = PUB_REVOKE_SVC, method = RequestMethod.POST)
	public @ResponseBody boolean revokeSubscription(
			@PathVariable("pubId") long pubId,
			@RequestBody long subId,
			HttpServletResponse response) {
		// Check that exists a publisher with the provided id
		if ((!mobileclients.exists(pubId)) || (!mobileclients.exists(subId))) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		
		MobileClient publisher = mobileclients.findOne(pubId);
		MobileClient subscriber = mobileclients.findOne(subId);
		
		publisher.getClients().put(subscriber.getId(), SubscriptionStatus.REJECTED);
		mobileclients.save(publisher);
		subscriber.getClients().put(publisher.getId(), SubscriptionStatus.REJECTED);
		mobileclients.save(subscriber);
		
		return true;
	}

}
