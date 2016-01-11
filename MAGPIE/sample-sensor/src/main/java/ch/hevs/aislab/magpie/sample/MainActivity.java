package ch.hevs.aislab.magpie.sample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.util.Linkify;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.regex.Pattern;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.behavior.PriorityBehaviorAgentMind;
import ch.hevs.aislab.magpie.bioharness.BioHarnessHandler;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;


public class MainActivity extends MagpieActivity {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private EditText glucoseEditTxt;
    private TextView glucoseTstampTxtView;
    private EditText weightEditTxt;
    private TextView weightTstampTxtView;
    private Button connectBtn;
    private Button disconnectBtn;

    private PhysioTimestamp glucoseTstamp;
    private PhysioTimestamp weightTstamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glucoseEditTxt = (EditText) findViewById(R.id.glucoseEditTxt);
        glucoseTstampTxtView = (TextView) findViewById(R.id.glucoseTimestampTxtView);
        weightEditTxt = (EditText) findViewById(R.id.weightEditTxt);
        weightTstampTxtView = (TextView) findViewById(R.id.weightTimestampTxtView);
        connectBtn = (Button) findViewById(R.id.connectSensorBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectSensorBtn);

        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);

        glucoseTstamp = new PhysioTimestamp(glucoseTstampTxtView);
        weightTstamp = new PhysioTimestamp(weightTstampTxtView);
    }

    @Override
    public void onEnvironmentConnected() {

        // Create a new Agent and register it into the environment
        //Prolog example
        MagpieAgent prologAgent = new MagpieAgent("monitoring_agent", Services.LOGIC_TUPLE);
        PrologAgentMind prologMind = new PrologAgentMind(getApplicationContext(), R.raw.monitoring_rules);
        prologAgent.setMind(prologMind);
        getService().registerAgent(prologAgent);

        //Java example
        MagpieAgent behaviorAgent = new MagpieAgent("priority_agent", Services.LOGIC_TUPLE);
        PriorityBehaviorAgentMind behaviorMind = new PriorityBehaviorAgentMind();
        behaviorMind.addBehavior(new CounterBehavior(this, behaviorAgent, 0));
        behaviorMind.addBehavior(new HighWeightBehaviour(this, behaviorAgent, 1));
        behaviorMind.addBehavior(new HypoglycemiaBehaviour(this, behaviorAgent, 3));
        behaviorAgent.setMind(behaviorMind);
        getService().registerAgent(behaviorAgent);
    }

    //Only for prolog
    @Override
    public void onAlertProduced(LogicTupleEvent alert) {
        Toast.makeText(this, alert.toTuple(), Toast.LENGTH_LONG).show();
    }

    public void sendGlucoseEvent(View view) {
        processEvent("glucose", glucoseEditTxt, glucoseTstamp, glucoseTstampTxtView);
    }

    public void sendWeightEvent(View view) {
        processEvent("weight", weightEditTxt, weightTstamp, weightTstampTxtView);
    }

    private void processEvent(String type, EditText editText, PhysioTimestamp timestamp, TextView textView) {
        // This is a boilerplate code
        String value = editText.getText().toString();
        if (value.equals("")) {
            Toast.makeText(this, "Can't send an empty value", Toast.LENGTH_LONG).show();
            return;
        }
        LogicTupleEvent lte = new LogicTupleEvent(type, value);
        lte.setTimeStamp(timestamp.timestamp.toDate().getTime());
        sendEvent(lte);

        editText.setText("");
        editText.clearFocus();
        textView.setText("-");
    }

    /**
     * Called by Android when the corresponding button is pressed
     */
    public void connectToBioHarness(View view) {
        connectToSensor(BioHarnessHandler.class);
    }

    public void disconnectBioHarness(View view) {
        disconnectSensor();
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
    }

    @Override
    protected void sensorConnectionResult(int code) {
        Toast.makeText(this, "Connection code: " + code, Toast.LENGTH_LONG).show();
        if ((code == BioHarnessHandler.BIOHARNESS_CONNECTED) ||
            (code == BioHarnessHandler.BIOHARNESS_ALREADY_CONNECTED)) {
            connectBtn.setEnabled(false);
            disconnectBtn.setEnabled(true);
        }
    }

    /**
     * The rest of the code is to show the time pickers to the user,
     * and makes use of the betterpickers library
     */

    /** Set the glucose timestamp */
    public void setGlucoseTimestamp(View view) {
        showCalendarDatePickerDialog(glucoseTstamp);
    }

    /** Set the weight timestamp */
    public void setWeightTimestamp(View view) {
        showCalendarDatePickerDialog(weightTstamp);
    }

    private void showCalendarDatePickerDialog(PhysioTimestamp physioTimestamp) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(physioTimestamp, now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    private class PhysioTimestamp implements
            CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

        private MutableDateTime timestamp;
        private TextView textView;

        public PhysioTimestamp(TextView textView) {
            this.timestamp = MutableDateTime.now();
            this.textView = textView;
        }

        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog,
                              int year, int monthOfYear, int dayOfMonth) {
            timestamp.setDate(year, monthOfYear + 1, dayOfMonth);
            DateTime now = DateTime.now();
            RadialTimePickerDialog radialTimePickerDialog = RadialTimePickerDialog
                    .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), true);
            radialTimePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        }

        @Override
        public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hourOfDay, int minute) {
            timestamp.setHourOfDay(hourOfDay);
            timestamp.setMinuteOfHour(minute);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("kk:mm dd/MM/yyyy");
            textView.setText(timestamp.toString(dtf));
        }
    }


    /**
     * Methods to handle the About dialog
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_item:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        // Transform text into URL link
        View aboutView = getLayoutInflater().inflate(R.layout.dialog_about, null, false);
        TextView txtView = (TextView) aboutView.findViewById(R.id.aboutTxtView);
        Pattern pattern = Pattern.compile("here");
        Linkify.addLinks(txtView, pattern, getString(R.string.magpie_url));
        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(
                        this,
                        android.R.style.Theme_Material_Light_NoActionBar_Fullscreen));
        builder.setTitle(getString(R.string.about_app))
                .setView(aboutView)
                .create()
                .show();
    }

}
