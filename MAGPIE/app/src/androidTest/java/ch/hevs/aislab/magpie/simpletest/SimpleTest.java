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
import ch.hevs.aislab.magpie.event.MagpieEvent;

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
        ev1.setTimeStamp(1418646780000L); // Mon, 15 Dec 2014 13:33:00
        ev2.setTimeStamp(1418660940000L); // Mon, 15 Dec 2014 17:29:00

        PrologAgentMind mind = new PrologAgentMind(getTestContext());

        // First event
        mind.updatePerception(ev1);
        mind.produceAction(1418646780000L);

        // Second event
        mind.updatePerception(ev2);
        LogicTupleEvent alert = (LogicTupleEvent) mind.produceAction(1418660940000L);

        assertEquals(alert.toTuple(), "act(act(produce_alert(second,'Brittle diabetes')),1418660940001)");

        // Events triggering a 'pre-hypertension' alert
        LogicTupleEvent ev3 = new LogicTupleEvent("blood_pressure", "150", "84");
        LogicTupleEvent ev4 = new LogicTupleEvent("blood_pressure", "134", "82");
        ev3.setTimeStamp(1407778799000L); // Mon, 11 Aug 2014 19:39:59
        ev4.setTimeStamp(1407900429000L); // Wed, 13 Aug 2014 05:27:09

        // Event within the same time window
        LogicTupleEvent ev5 = new LogicTupleEvent("blood_pressure(134,83)");
        ev5.setTimeStamp(1407951178000L); // Wed, 13 Aug 2014 19:32:58

        // Update the events
        mind.updatePerception(ev3);
        mind.produceAction(1407778799000L);

        mind.updatePerception(ev4);
        LogicTupleEvent alertTwo = (LogicTupleEvent) mind.produceAction(1407900429000L);

        // Last event should not trigger an alert
        mind.updatePerception(ev5);
        mind.produceAction(1407951178000L);

        assertEquals(alertTwo.toTuple(), "act(act(produce_alert(third,'pre-hypertension, consider lifestyle modification')),1407900429001)");

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