package ch.hevs.aislab.magpie.broker.model;

import javax.persistence.Entity;

@Entity
public class Subscriber extends User {

	public Subscriber() {
		
	}
	
	public Subscriber(String firstName, String lastName) {
		super(firstName, lastName);
	}
}
