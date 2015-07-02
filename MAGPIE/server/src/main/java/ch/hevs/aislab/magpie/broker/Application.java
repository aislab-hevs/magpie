package ch.hevs.aislab.magpie.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//Inject automatically the dependencies marked as @Autowired
@EnableAutoConfiguration
//This object is the Configuration for the application
@Configuration
//Scan packages for controllers
@ComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
