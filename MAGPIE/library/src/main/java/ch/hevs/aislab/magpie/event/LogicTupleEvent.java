package ch.hevs.aislab.magpie.event;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import alice.tuprolog.Term;
import ch.hevs.aislab.magpie.environment.Services;

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

    public String getName() {
        int end = logicRepresentation.indexOf("(");
        return logicRepresentation.substring(0,end);
    }

    public List<String> getArguments() {
        List<String> arguments = new ArrayList<>();
        int elements = StringUtils.countMatches(logicRepresentation, ",");

        if (elements == 0) {
            arguments.add(getSubstring("(", ")"));
        } else if (elements == 1) {
            arguments.add(getSubstring("(", ","));
            arguments.add(getSubstring(",", ")"));
        } else if (elements > 1) {
            arguments.add(getSubstring("(", ","));
            String restString = getSubstring(",", ")");
            String[] restArray = StringUtils.split(restString, ",");
            for (int i = 0; i < restArray.length; i++) {
                arguments.add(restArray[i]);
            }
        }
        return arguments;
    }

    private String getSubstring(String start, String end) {
        int first = StringUtils.indexOf(logicRepresentation, start);
        int second = StringUtils.indexOf(logicRepresentation, end);
        return StringUtils.substring(logicRepresentation, first + 1, second);
    }

}
