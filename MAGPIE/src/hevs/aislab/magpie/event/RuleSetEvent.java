package hevs.aislab.magpie.event;

import hevs.aislab.magpie.environment.Services;
import hevs.aislab.magpie.support.Rule;

import java.util.Collection;

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
