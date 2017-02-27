package ch.hevs.aislab.magpie.test.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.sample.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Instrumentation test, which will execute on an Android device.
 * The test checks whether the monitoring rules are triggered properly.
 */
@RunWith(AndroidJUnit4.class)
public class PrologAgentMindTest {

    private PrologAgentMind agentMind;

    @Before
    public void initAgentMind() {
        Context context = InstrumentationRegistry.getTargetContext();
        agentMind = new PrologAgentMind(context, R.raw.monitoring_rules);
    }


    @Test
    public void brittleDiabetesAlertTest() {
        /**
         * A 'Brittle diabetes' alert is triggered if glucose measurements go from less than or
         * equal to 3.8 mmol/L to more than or equal to 8.0 mmol/L inside a period of six hours
         */

        // First event: created with the constructor accepting timestamp and Term
        long tsEv1 = convertDateToMills("15-12-2014 13:33");
        LogicTupleEvent ev1 = new LogicTupleEvent(tsEv1, "glucose(3.5)");

        /*
         * Second event: created with the constructor accepting name and arguments.
         * In this case, the timestamp is defined with the setter method
         */
        LogicTupleEvent ev2 = new LogicTupleEvent("glucose", "9");
        long tsEv2 = convertDateToMills("15-12-2014 17:29");
        ev2.setTimestamp(tsEv2);

        // The mind perceives the first event and checks if an alert is triggered
        agentMind.updatePerception(ev1);
        LogicTupleEvent noAlert = (LogicTupleEvent) agentMind.produceAction(tsEv1);
        // There should be no alert for the first event
        assertNull(noAlert);

        // The mind perceives the second event and checks if an alert is triggered
        agentMind.updatePerception(ev2);
        LogicTupleEvent alert = (LogicTupleEvent) agentMind.produceAction(tsEv2);
        // This time a 'Brittle diabetes' alert should be triggered
        String alertName = alert.getArguments().get(0);
        assertEquals("'Brittle diabetes'", alertName);

        // There is a third glucose event inside the six hours period, whose value is above 8 mmol/L
        long tsEv3 = convertDateToMills("15-12-2014 17:45");
        LogicTupleEvent ev3 = new LogicTupleEvent(tsEv3, "glucose", "8.7");

        // The mind perceives the third event and checks if an alert is triggered
        agentMind.updatePerception(ev1);
        LogicTupleEvent secondAlert = (LogicTupleEvent) agentMind.produceAction(tsEv3);
        // There is no alert triggered due to the 'no alert' condition
        assertNull(secondAlert);
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

        assertEquals("act('Pre-hypertension'," + ++tsEv2 + ")", alert.toTuple());

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

        assertEquals(alert.toTuple(), "act('DM treatment is not effective'," + ++tsEv3 + ")");
    }

    private long convertDateToMills(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date.getTime();
    }
}