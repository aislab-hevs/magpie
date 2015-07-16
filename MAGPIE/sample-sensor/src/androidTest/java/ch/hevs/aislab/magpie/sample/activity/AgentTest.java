package ch.hevs.aislab.magpie.sample.activity;

import android.content.Context;
import android.test.AndroidTestCase;

import java.lang.reflect.Method;

import alice.tuprolog.Term;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.sample.R;

public class AgentTest extends AndroidTestCase {

    private final static String TAG = "AgentTest";

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

        //getContext().getResources().;
        PrologAgentMind mind = new PrologAgentMind(getContext(), R.raw.monitoring_rules);

        // *************** Test 1 ***************
        // Events triggering a 'Brittle diabetes' alert
        LogicTupleEvent ev1 = new LogicTupleEvent("glucose(3.5)"); // Term
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose", "9"); // Strings
        ev1.setTimeStamp(1418646780000L); // Mon, 15 Dec 2014 13:33:00
        ev2.setTimeStamp(1418660940000L); // Mon, 15 Dec 2014 17:29:00

        // First event
        mind.updatePerception(ev1);
        mind.produceAction(1418646780000L);

        // Second event
        mind.updatePerception(ev2);
        LogicTupleEvent alertOne = (LogicTupleEvent) mind.produceAction(1418660940000L);

        assertEquals(alertOne.toTuple(), "act(act(produce_alert(second,'Brittle diabetes')),1418660940001)");
        // *************** Test 1 ***************

        // *************** Test 2 ***************
        // Events triggering a 'pre-hypertension' alert
        LogicTupleEvent ev3 = new LogicTupleEvent("blood_pressure", "150", "84");
        LogicTupleEvent ev4 = new LogicTupleEvent("blood_pressure", "134", "82");
        ev3.setTimeStamp(1407778799000L); // Mon, 11 Aug 2014 19:39:59
        ev4.setTimeStamp(1407900429000L); // Wed, 13 Aug 2014 05:27:09

        // Event within the same time window happening after the alert
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
        // *************** Test 2 ***************

        // *************** Test 3 ***************
        // Events triggering a 'DM treatment is not effective'
        LogicTupleEvent ev6 = new LogicTupleEvent("glucose(10.5)");
        LogicTupleEvent ev7 = new LogicTupleEvent("glucose(10.6)");
        LogicTupleEvent ev8 = new LogicTupleEvent("weight", "87.2");
        ev6.setTimeStamp(1407322080000L); // Wed, 06 Aug 2014 10:48:00
        ev7.setTimeStamp(1407660840000L); // Sun, 10 Aug 2014 10:54:00
        ev8.setTimeStamp(1408345255000L); // Mon, 18 Aug 2014 09:00:55

        // Fake event within the same time window happening before the alert
        LogicTupleEvent ev9 = new LogicTupleEvent("glucose", "10.2");
        ev9.setTimeStamp(1439362800L); // Wed, 12 Aug 2015 09:00:00

        mind.updatePerception(ev6);
        mind.produceAction(1407322080000L);

        mind.updatePerception(ev7);
        mind.produceAction(1407660840000L);

        mind.updatePerception(ev9);
        mind.produceAction(1439362800L);

        mind.updatePerception(ev8);
        LogicTupleEvent alertThree = (LogicTupleEvent) mind.produceAction(1408345255000L);

        assertEquals(alertThree.toTuple(), "act(act(produce_alert(first,'DM treatment is not efective')),1408345255001)");
        // *************** Test 3 ***************

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