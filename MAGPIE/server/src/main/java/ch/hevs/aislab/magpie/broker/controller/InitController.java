package ch.hevs.aislab.magpie.broker.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ch.hevs.aislab.magpie.broker.model.MobileClient;
import ch.hevs.aislab.magpie.broker.repository.MobileClientRepo;

@Controller
public class InitController implements InitializingBean {

	@Autowired
	private MobileClientRepo clients;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		MobileClient publisher = new MobileClient("publisher", "John", "Jameson");
		clients.save(publisher);
		
		MobileClient subscriber = new MobileClient("subscriber", "Peter", "Parker");
		clients.save(subscriber);
		
	}
	
}
