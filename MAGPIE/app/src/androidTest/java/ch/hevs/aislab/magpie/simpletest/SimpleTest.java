package ch.hevs.aislab.magpie.simpletest;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.ServiceTestCase;
import android.util.Log;

import junit.framework.TestCase;

import java.lang.reflect.Method;

import alice.tuprolog.Term;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieApp;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;

public class SimpleTest extends AndroidTestCase {

    private final static String TAG = "SimpleTest";

    public void testPass(){
        assertTrue(true);
    }

    /**
     * Test if a LogicTuple is created correctly
     */
    public void testLogicTuple() {
    	
    	final String logicTuple = "blood_pressure(Sys,Dias)";  
    	
    	// Creation with multiple arguments
    	LogicTupleEvent bp1 = new LogicTupleEvent("blood_pressure", "Sys", "Dias");
    	String tupleFromArgs = bp1.toTuple();
    	
    	assertEquals(logicTuple, tupleFromArgs);
    	
    	// Creation from a tuProlog Term
    	Term t = Term.createTerm(logicTuple);
    	LogicTupleEvent bp2 = new LogicTupleEvent(t);
    	String tupleFromTerm = bp2.toTuple();
    	
    	assertEquals(logicTuple, tupleFromTerm);

        // Creation from String
        LogicTupleEvent bp3 = new LogicTupleEvent(logicTuple);
        String tupleFromString = bp3.toTuple();

        assertEquals(logicTuple, tupleFromString);
    }

    public void testAgentMind() {

        // Events triggering a 'Brittle diabetes' alert
        LogicTupleEvent ev1 = new LogicTupleEvent("glucose(3.5)"); // Term
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose", "9"); // Strings
        ev1.setTimeStamp(1418646780000L);
        ev2.setTimeStamp(1418660940000L);

        PrologAgentMind mind = new PrologAgentMind(getTestContext());

        // First event
        mind.updatePerception(ev1);
        mind.produceAction(1418646780000L);

        // Second event
        mind.updatePerception(ev2);
        LogicTupleEvent alert = (LogicTupleEvent) mind.produceAction(1418660940000L);

        Log.i(TAG, alert.toTuple());

        assertEquals(alert.toTuple(), "act(act(produce_alert(second,'Brittle diabetes')),1418660940001)");
    }

    /**
     * Gets the context
     * @return
     */
    private Context getTestContext() {
        try {
            Method getTestContext = AndroidTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}