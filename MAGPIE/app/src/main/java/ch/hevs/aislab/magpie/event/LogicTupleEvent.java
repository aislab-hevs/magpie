package ch.hevs.aislab.magpie.event;

import ch.hevs.aislab.magpie.environment.Services;
import alice.tuprolog.Term;

public class LogicTupleEvent extends MagpieEvent {

	private String logicRepresentation;
	
	public LogicTupleEvent(Term t) {
		this.type = Services.LOGIC_TUPLE;
		this.logicRepresentation = t.toString();	
	}
	
	public LogicTupleEvent(String name, String[] args) {
		this.type = Services.LOGIC_TUPLE;
		
		String tuple = name + "(";
		
		for(int i=0; i<args.length; i++) {
			tuple = tuple + args[i] + ",";
		}
		
		// Remove the last comma and close the parenthesis
		tuple = tuple.substring(0, tuple.length() - 1);
		tuple = tuple + ")";
		
		this.logicRepresentation = tuple;
	}
	
	/**
	 * It gives the tuple representation of the event.
	 * 
	 * @return
	 */
	public String toTuple(){
		return this.logicRepresentation;
	}
}
