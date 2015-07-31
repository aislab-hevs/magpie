package ch.hevs.aislab.magpie.broker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MobileClient {

	public static final String ROLE_PUBLISHER = "PUBLISHER";
	public static final String ROLE_SUBSCRIBER = "SUBSCRIBER";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private String username;
    
    @JsonIgnore
    private String gcmToken;

    private String firstName;
    private String lastName;

    @ElementCollection
    private List<String> roles;

    @ElementCollection
    private Map<Long, SubscriptionStatus> clients;

    public MobileClient() {

    }
    
    public MobileClient(String username, String firstName, String lastName) {
    	this.username = username;
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.roles = new ArrayList<String>();
    	this.clients = new HashMap<Long, SubscriptionStatus>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<Long, SubscriptionStatus> getClients() {
        return clients;
    }

    public void setClients(Map<Long, SubscriptionStatus> clients) {
        this.clients = clients;
    }
}
