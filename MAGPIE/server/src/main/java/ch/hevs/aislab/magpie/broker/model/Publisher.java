package ch.hevs.aislab.magpie.broker.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Publisher extends User {
	
	@ManyToMany
	Set<Subscriber> subscribers = new HashSet<Subscriber>();
	
	public Publisher() {
		
	}
	
	public Publisher(String firstName, String lastName) {
		super(firstName, lastName);
	}
}
