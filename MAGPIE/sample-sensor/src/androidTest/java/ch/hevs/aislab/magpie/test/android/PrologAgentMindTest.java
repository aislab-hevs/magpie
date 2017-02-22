package ch.hevs.aislab.magpie.test.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.sample.R;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class PrologAgentMindTest {

    PrologAgentMind agentMind;

    @Before
    public void initAgentMind() {
        Context context = InstrumentationRegistry.getTargetContext();
        agentMind = new PrologAgentMind(context, R.raw.monitoring_rules);
    }


    @Test
    public void brittleDiabetesAlertTest() {
        // Glucose events that trigger the alert
        LogicTupleEvent ev1 = new LogicTupleEvent("glucose(3.5)"); // Term
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose", "9"); // Strings

        // Timestamps of the events
        long tsEv1 = 1418646780000L; // Mon, 15 Dec 2014 13:33:00
        long tsEv2 = 1418660940000L; // Mon, 15 Dec 2014 17:29:00
        ev1.setTimestamp(tsEv1);
        ev2.setTimestamp(tsEv2);

        // The mind perceives the first event
        agentMind.updatePerception(ev1);
        agentMind.produceAction(tsEv1);

        // The mind perceives the second event
        agentMind.updatePerception(ev2);
        LogicTupleEvent alert = (LogicTupleEvent) agentMind.produceAction(tsEv2);

        // Internally the PrologAgentMind increments the timestamp 1L, which corresponds to 1ms
        assertEquals(alert.toTuple(), "act(act(produce_alert(first,'Brittle diabetes'))," + ++tsEv2 + ")");
    }

    @Test
    public void preHypertenstionAlertTest() {
        // Blood pressure events that trigger the alert
        LogicTupleEvent ev1 = new LogicTupleEvent("blood_pressure", "150", "84");
        LogicTupleEvent ev2 = new LogicTupleEvent("blood_pressure", "134", "82");

        // Timestamps of the events
        long tsEv1 = 1407778799000L; // Mon, 11 Aug 2014 19:39:59
        long tsEv2 = 1407900429000L; // Wed, 13 Aug 2014 05:27:09
        ev1.setTimestamp(tsEv1);
        ev2.setTimestamp(tsEv2);

        // The mind perceives the first event
        agentMind.updatePerception(ev1);
        agentMind.produceAction(tsEv1);

        // The mind perceives the second event
        agentMind.updatePerception(ev2);
        LogicTupleEvent alert = (LogicTupleEvent) agentMind.produceAction(tsEv2);

        assertEquals(alert.toTuple(), "act(act(produce_alert(second,'Pre-hypertension'))," + ++tsEv2 + ")");

        // Blood pressure event that happens after the alert and is within the one week time window
        LogicTupleEvent ev3 = new LogicTupleEvent("blood_pressure(134,83)");
        long tsEv3 = 1407951178000L; // Wed, 13 Aug 2014 19:32:58
        ev3.setTimestamp(tsEv3);

        agentMind.updatePerception(ev3);
        LogicTupleEvent noAlert = (LogicTupleEvent) agentMind.produceAction(tsEv3);

        assertEquals(noAlert, null);


    }

    @Test
    public void notEffectiveTreatmentAlertTest() {
        LogicTupleEvent ev1 = new LogicTupleEvent("glucose(10.5)");
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose(10.6)");
        LogicTupleEvent ev3 = new LogicTupleEvent("weight", "87.2");

        long tsEv1 = 1407322080000L; // Wed, 06 Aug 2014 10:48:00
        long tsEv2 = 1407660840000L; // Sun, 10 Aug 2014 10:54:00
        long tsEv3 = 1408345255000L; // Mon, 18 Aug 2014 09:00:55
        ev1.setTimestamp(tsEv1);
        ev2.setTimestamp(tsEv2);
        ev3.setTimestamp(tsEv3);

        agentMind.updatePerception(ev1);
        agentMind.produceAction(tsEv1);
        agentMind.updatePerception(ev2);
        agentMind.produceAction(tsEv2);
        agentMind.updatePerception(ev3);
        LogicTupleEvent alert = (LogicTupleEvent) agentMind.produceAction(tsEv3);

        assertEquals(alert.toTuple(), "act(act(produce_alert(fourth,'DM treatment is not effective'))," + ++tsEv3 + ")");
    }
}