package ch.hevs.aislab.magpie.test.java;


import org.junit.Test;

import alice.tuprolog.Term;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;

import static junit.framework.TestCase.assertEquals;

public class LogicTupleTest {

    @Test
    public void createLogicTuple() {

        // This method tests if the following LogicTuple is created correctly using the different constructors
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

    @Test
    public void getNameAndArguments() {

        String name = "blood_pressure";
        String arg1 = "Sys";
        String arg2 = "Dias";
        LogicTupleEvent ev = new LogicTupleEvent(name, arg1, arg2);

        assertEquals(name, ev.getName());
        assertEquals(arg1, ev.getArguments().get(0));
        assertEquals(arg2, ev.getArguments().get(1));
    }

    @Test
    public void getTimestampAsString() {

        long timestamp = System.currentTimeMillis();

        LogicTupleEvent ev1 = new LogicTupleEvent(timestamp, "glucose", "5.5");
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose", "5.5");

        String dateEv1 = ev1.getStringTimestamp("dd.MM.yyyy");
        String dateEv2 = ev2.getStringTimestamp("dd.MM.yyyy");

        // Checks that both dates are the same, so the test might fail if it runs around midnight
        assertEquals(dateEv1, dateEv2);
    }
}
