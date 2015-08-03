package ch.hevs.aislab.magpie.broker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.hevs.aislab.magpie.broker.client.UserSvcApi;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.model.SubscriptionStatus;
import ch.hevs.aislab.magpie.broker.repository.MobileClientRepo;

import com.google.common.collect.Lists;

@Controller
public class MobileClientController implements UserSvcApi {
	
	@Autowired
	private MobileClientRepo mobileclients;
	
	@Override
	@RequestMapping(value = USER_GET_SVC, method = RequestMethod.POST)
	public @ResponseBody MobileClient getMobileClient(
			@RequestBody String gcmToken, Principal p) {
		
		System.out.println("GCM Token: " + gcmToken);	
		MobileClient mc = null;
		List<String> roles = Lists.newArrayList();
		
		try {
			Authentication auth = (Authentication) p;
			for (GrantedAuthority role : auth.getAuthorities()) {
				roles.add(role.getAuthority());
			}
			mc = mobileclients.findByUsername(auth.getName());
			// Remove the token's quotation marks
			gcmToken = gcmToken.substring(1, gcmToken.length() - 1);
			// Update token and roles of the client
			mc.setGcmToken(gcmToken);
			mc.setRoles(roles);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return mobileclients.save(mc);
	}

	@Override
	@RequestMapping(value = USER_GET_CONTACTS_ACCEPTED_SVC, method = RequestMethod.GET)
	public @ResponseBody Collection<MobileClient> getContactsAccepted(
			@PathVariable(USER_ID) long userId) {
		if (!mobileclients.exists(userId)) {
			return null;
		}
		MobileClient user = mobileclients.findOne(userId);
		Collection<MobileClient> clientsAccepted = new ArrayList<MobileClient>();
		for (Map.Entry<Long, SubscriptionStatus> entryPair : user.getClients().entrySet()) {
			if (entryPair.getValue().equals(SubscriptionStatus.ACCEPTED)) {
				MobileClient clientAccepted = mobileclients.findOne(entryPair.getKey());
				clientsAccepted.add(clientAccepted);
			}
		}
		return clientsAccepted;
	}

	@Override
	@RequestMapping(value = USER_GET_CONTACTS_PENDING_SVC, method = RequestMethod.GET)
	public @ResponseBody Collection<MobileClient> getContactsPending(
			@PathVariable(USER_ID) long userId) {
		if (!mobileclients.exists(userId)) {
			return null;
		}
		MobileClient user = mobileclients.findOne(userId);
		Collection<MobileClient> clientsPending = new ArrayList<MobileClient>();
		for (Map.Entry<Long, SubscriptionStatus> entryPair : user.getClients().entrySet()) {
			if (entryPair.getValue().equals(SubscriptionStatus.PENDING)) {
				MobileClient clientAccepted = mobileclients.findOne(entryPair.getKey());
				clientsPending.add(clientAccepted);
			}
		}
		return clientsPending;
	}
}
