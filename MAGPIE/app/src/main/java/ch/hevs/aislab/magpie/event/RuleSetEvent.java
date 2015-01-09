package ch.hevs.aislab.magpie.event;

import java.util.Collection;

import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.support.Rule;

public class RuleSetEvent extends MagpieEvent {

	Collection<Rule> rules;
	
	public RuleSetEvent(Collection<Rule> rules) {
		this.type = Services.RULE_SET;
		this.rules = rules;
	}
	
	public Collection<Rule> getRules() {
		return rules;
	}
}
