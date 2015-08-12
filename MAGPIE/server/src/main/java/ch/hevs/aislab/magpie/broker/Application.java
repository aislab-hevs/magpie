package ch.hevs.aislab.magpie.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.hevs.aislab.magpie.broker.auth.OAuth2SecurityConfiguration;

// Inject automatically the dependencies marked as @Autowired
@EnableAutoConfiguration
// Tell Spring to turn on WebMVC
@EnableWebMvc
// This object is the Configuration for the application
@Configuration
// Scan packages for controllers
@ComponentScan
// Include OAuth2SecurityConfiguration as part of the configuration
@Import(OAuth2SecurityConfiguration.class)
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
	
}
