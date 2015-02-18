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
    	String[] args = new String[2];
    	args[0] = "Sys";
    	args[1] = "Dias";

    	LogicTupleEvent bp1 = new LogicTupleEvent("blood_pressure", args);
    	String tupleFromArgs = bp1.toTuple();
    	
    	assertEquals(logicTuple, tupleFromArgs);
    	
    	// Creation from a tuProlog Term
    	Term t = Term.createTerm(logicTuple);
    	LogicTupleEvent bp2 = new LogicTupleEvent(t);
    	String tupleFromTerm = bp2.toTuple();
    	
    	assertEquals(logicTuple, tupleFromTerm);
    }

    public void testAgentMind() {

        PrologAgentMind mind = new PrologAgentMind(getTestContext());
    }

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
