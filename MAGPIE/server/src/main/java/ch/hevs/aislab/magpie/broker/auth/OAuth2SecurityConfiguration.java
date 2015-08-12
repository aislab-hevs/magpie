package ch.hevs.aislab.magpie.broker.auth;

import java.io.File;
import java.util.Arrays;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import ch.hevs.aislab.magpie.broker.model.MobileClient;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class OAuth2SecurityConfiguration {
	
	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Autowired
		protected void registerAuthentication(
				final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}
	}
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServer extends
					ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			
			http
			.authorizeRequests()
				.antMatchers("/oauth/token").anonymous();
			
			http
			.authorizeRequests()
					.antMatchers(HttpMethod.GET, "/**")
					.access("#oauth2.hasScope('read')");
			
			http
			.authorizeRequests()
					.antMatchers("/**")
					.access("#oauth2.hasScope('write')");
		}
		
	}
	
	@Configuration
	@EnableAuthorizationServer
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	protected static class OAuthConfig extends
					AuthorizationServerConfigurerAdapter {
		
		@Autowired
		private AuthenticationManager authenticationManager;
		
		private ClientAndUserDetailsService combinedService_;
		
		public OAuthConfig() throws Exception {
			
			ClientDetailsService csvc = new InMemoryClientDetailsServiceBuilder()
							.withClient("mobile").authorizedGrantTypes("password")
							.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
							.scopes("read","write").resourceIds("prublisher")
							.accessTokenValiditySeconds(3600).and().build();
			
			UserDetailsService svc = new InMemoryUserDetailsManager(
					Arrays.asList(
									User.create("publisher", "publisher", MobileClient.ROLE_PUBLISHER),
									User.create("subscriber", "subscriber", MobileClient.ROLE_SUBSCRIBER)));
			
			combinedService_ = new ClientAndUserDetailsService(csvc,svc);
		}
		
		@Bean
		public ClientDetailsService clientDetailsService() {
			return combinedService_;
		}
		
		@Bean
		public UserDetailsService userDetailsService() {
			return combinedService_;
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) 
						throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) 
						throws Exception {
			clients.withClientDetails(clientDetailsService());
		}
	}
	
	@Bean
	EmbeddedServletContainerCustomizer containerCostumizer(
			@Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
			@Value("${keystore.pass:changeit}") final String keystorePass) throws Exception {
		
		final String absoluteKeyStore = new File(keystoreFile).getAbsolutePath();
		
		return new EmbeddedServletContainerCustomizer() {
			
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = null;
				boolean casted = true;
				try {
					tomcat = (TomcatEmbeddedServletContainerFactory) container;
				} catch(ClassCastException ex) {
					casted = false;
				}
				
				if (casted) {
					tomcat.addConnectorCustomizers(
							new TomcatConnectorCustomizer() {
								@Override
								public void customize(Connector connector) {
									connector.setPort(8443);
									connector.setSecure(true);
									connector.setScheme("https");
									
									Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
									proto.setSSLEnabled(true);
									proto.setKeystoreFile(absoluteKeyStore);
									proto.setKeystorePass(keystorePass);
									proto.setKeystoreType("JKS");
									proto.setKeyAlias("tomcat");
								}
							});
				} else {
					tomcat = new TomcatEmbeddedServletContainerFactory();
					tomcat.addAdditionalTomcatConnectors(createSSLConnector(absoluteKeyStore, keystorePass));
				}
			}
		};
	}
	
	private Connector createSSLConnector(String absoluteKeyStore, String keystorePass) {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
		try {
			connector.setPort(8443);
			connector.setSecure(true);
			connector.setScheme("https");
			
			protocol.setSSLEnabled(true);
			protocol.setKeystoreFile(absoluteKeyStore);
			protocol.setKeystorePass(keystorePass);
			protocol.setKeystoreType("JKS");
			protocol.setKeyAlias("tomcat");
			return connector;
		} catch(Exception ex) {
			throw new IllegalStateException("Can't access to keystore: [" + absoluteKeyStore + "]", ex);
		}
	}

}
