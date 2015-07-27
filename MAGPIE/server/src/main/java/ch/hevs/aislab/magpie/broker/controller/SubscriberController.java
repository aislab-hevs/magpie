package ch.hevs.aislab.magpie.broker.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.hevs.aislab.magpie.broker.client.SubscriberSvcApi;
import ch.hevs.aislab.magpie.broker.gcm.GcmSender;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.repository.MobileClientRepo;

@Controller
public class SubscriberController implements SubscriberSvcApi {
	    
    @Autowired
    MobileClientRepo mobileclients;
    
    @RequestMapping(value = SUB_SUBSCRIBE_SVC, method = RequestMethod.POST)
	public @ResponseBody boolean doSubscriptionByUsername(
			@PathVariable(SUBSCRIBER_ID) long subId,
			@RequestBody String pubUsername,
			HttpServletResponse response) {
    	if (!mobileclients.exists(subId)) {
    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    		return false;
    	}
    	String subUsername = mobileclients.findOne(subId).getUsername();
    	
		// Remove quotation marks from the body
		pubUsername = pubUsername.substring(1, pubUsername.length()-1);
		MobileClient mc = mobileclients.findByUsername(pubUsername);
		String pubGcmToken = mc.getGcmToken(); 
		// The subscriber must exist and have a publisher role
		if ((mc == null) || (!mc.getRoles().contains(MobileClient.ROLE_PUBLISHER))) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		
		try {
			GcmSender.requestSubscription(subUsername, pubGcmToken);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
