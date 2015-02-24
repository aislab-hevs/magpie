package ch.hevs.aislab.magpie.event;

import ch.hevs.aislab.magpie.environment.Services;
import alice.tuprolog.Term;

public class LogicTupleEvent extends MagpieEvent {

	private String logicRepresentation;
	
	public LogicTupleEvent(Term term) {
		this.type = Services.LOGIC_TUPLE;
		this.logicRepresentation = term.toString();
	}

    public LogicTupleEvent(String logicEvent) {
        this.type = Services.LOGIC_TUPLE;

        // This checks if the Term is correct
        Term term = Term.createTerm(logicEvent);
        this.logicRepresentation = term.toString();

    }

    /**
     * Creates a logic tuple with format: name(arg1,arg2,...,argN)
     * @param name
     * @param args
     */
	public LogicTupleEvent(String name, String ... args) {
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
	 * It returns the tuple representation of the event
	 * 
	 * @return
	 */
	public String toTuple(){
		return logicRepresentation;
	}
}
