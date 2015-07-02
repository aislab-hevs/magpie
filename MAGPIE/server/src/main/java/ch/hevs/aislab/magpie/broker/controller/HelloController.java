package ch.hevs.aislab.magpie.broker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

	public static final String HELLO_SVC_PATH = "/hello";
	
	@RequestMapping(value=HELLO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody String getRequest(){
		return "Hello world!";
	}
	
}
