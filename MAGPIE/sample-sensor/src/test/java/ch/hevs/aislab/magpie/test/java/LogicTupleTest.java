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
}
