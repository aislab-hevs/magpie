package ch.hevs.aislab.paamsdemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.SolveInfo;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PrologAgentMindTest {

    private PrologAgentMind agentMind;

    @Before
    public void initAgentMind() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getTargetContext();
        agentMind = new PrologAgentMind(context, R.raw.demo_rules);
    }

    @Test
    public void bloodPressureTest() {
        // Test the 'Pre-hypertension' alert
        long tsEv1 = convertDateToMills("20-02-2017 09:00");
        LogicTupleEvent event1 = new LogicTupleEvent(tsEv1, "blood_pressure(135,85)");

        long tsEv2 = convertDateToMills("21-02-2017 09:00");
        LogicTupleEvent event2 = new LogicTupleEvent(tsEv2, "blood_pressure(136,86)");

        agentMind.updatePerception(event1);
        agentMind.produceAction(tsEv1);

        agentMind.updatePerception(event2);
        LogicTupleEvent alert = (LogicTupleEvent) agentMind.produceAction(tsEv2);
        String alertName = alert.getArguments().get(0);
        // The second event triggers the alert
        assertEquals("'Pre-hypertension'", alertName);

        // Test the 'no alert' condition
        long tsEv3 = convertDateToMills("22-02-2017 09:00");
        LogicTupleEvent event3 = new LogicTupleEvent(tsEv3, "blood_pressure(137,87)");

        agentMind.updatePerception(event3);
        LogicTupleEvent noAlert = (LogicTupleEvent)agentMind.produceAction(tsEv3);
        assertNull(noAlert);
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
