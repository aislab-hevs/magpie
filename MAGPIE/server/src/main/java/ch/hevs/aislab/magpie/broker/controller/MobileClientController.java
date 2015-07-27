package ch.hevs.aislab.magpie.broker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.hevs.aislab.magpie.broker.client.UserSvcApi;
import ch.hevs.aislab.magpie.broker.model.MobileClient;
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
}
