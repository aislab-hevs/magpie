package ch.hevs.aislab.paams.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.paams.connector.AlertDAO;
import ch.hevs.aislab.paams.connector.ValueDAO;
import ch.hevs.aislab.paams.db.DBHelper;
import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class MainActivity extends MagpieActivity implements AddValueFragment.OnAddedNewMeasurementListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private AlertDAO alertDAO;

    private List<OnChangeDummyDataDisplayListener> callbacks;

    public interface OnChangeDummyDataDisplayListener {
        void displayDummyData(Boolean display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbacks = new ArrayList<>();

        // Setup MainActivity
        setupDummyDatabase();
        setupSharedPrefs();
        setupToolbar();
        setupInstances();
        setNavigationView();

        alertDAO = new AlertDAO(this);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart()");
        super.onStart();
        alertDAO.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        alertDAO.close();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setupSharedPrefs() {
        Log.i(TAG, "setupSharedPrefs()");
        if (!getPreferences(Context.MODE_PRIVATE).contains(getString(R.string.pref_showMocked))) {
            updateSharedPrefs(true);
        }
    }

    private void setupDummyDatabase() {
        Log.i(TAG, "setupDummyDatabase()");
        DBHelper dbHelper = new DBHelper(this);
        if (!dbHelper.hasData()) {
            Log.i(TAG, "inserting dummy data..");
            dbHelper.initializeDummyData(this, 500, 30);
        }
    }

    private void setNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null) {
            prepareDrawer(navigationView);
            navigate(navigationView.getMenu().getItem(0));
        }
    }

    private void prepareDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        navigate(item);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    private void navigate(MenuItem itemDrawer) {
        Fragment genericFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = null;

        switch (itemDrawer.getItemId()) {
            case R.id.item_glucose:
                genericFragment = PhysioParamFragment.newInstance(Type.GLUCOSE);
                fragmentTag = Type.GLUCOSE.name();
                break;
            case R.id.item_blood_pressure:
                genericFragment = PhysioParamFragment.newInstance(Type.BLOOD_PRESSURE);
                fragmentTag = Type.BLOOD_PRESSURE.name();
                break;
            case R.id.item_weight:
                genericFragment = PhysioParamFragment.newInstance(Type.WEIGHT);
                fragmentTag = Type.WEIGHT.name();
                break;
            case R.id.item_alerts:
                genericFragment = AlertFragment.newInstance();
                fragmentTag = "ALERT";
                break;
            case R.id.item_about:
                showAboutDialog();
                return;
        }

        if (genericFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, genericFragment, fragmentTag)
                    .commit();
        }

        setTitle(itemDrawer.getTitle());
    }

    private void showAboutDialog() {
        View aboutView = getLayoutInflater().inflate(R.layout.dialog_about, null, false);

        Switch dummyDataSwitch = (Switch) aboutView.findViewById(R.id.dummyDataSwitch);
        dummyDataSwitch.setChecked(getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.pref_showMocked), true));
        dummyDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "callback.displayDummyData() with " + isChecked);
                updateSharedPrefs(isChecked);
                notifyCallbacks(isChecked);
            }
        });

        TextView iconsTextView = (TextView) aboutView.findViewById(R.id.iconsTextView);
        Pattern pattern = Pattern.compile("Icons8");
        String url = getString(R.string.about_url);

        Linkify.TransformFilter urlFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String url) {
                return getString(R.string.about_url)   ;
            }
        };

        Linkify.addLinks(iconsTextView, pattern, url, null, urlFilter);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                new android.view.ContextThemeWrapper(
                        this,
                        android.R.style.Theme_Material_Light_NoActionBar_Fullscreen));
        builder.setTitle(getString(R.string.about_title))
                .setView(aboutView)
                .create()
                .show();
    }

    private void updateSharedPrefs(boolean showMocked) {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(getString(R.string.pref_showMocked), showMocked);
        Log.i(TAG, "updateSharedPrefs() with " + showMocked);
        editor.commit();
    }

    public Boolean setOnChangeDummyDataDisplayListener(OnChangeDummyDataDisplayListener listener) {
        this.callbacks.add(listener);
        Log.i(TAG, "setOnChangeDummyDataDisplayListener() from " + listener.getClass());
        return getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.pref_showMocked), true);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void addSingleValue(SingleValue measurement) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, PhysioParamFragment.newInstance(measurement.getType()), measurement.getType().name()).commit();
        // Send also the measurement to MAGPIE
        sendEvent(measurement);
    }

    @Override
    public void addDoubleValue(DoubleValue measurement) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, PhysioParamFragment.newInstance(measurement.getType()), measurement.getType().name()).commit();
        // Send also the measurement to MAGPIE
        sendEvent(measurement);
    }

    /**
     * MAGPIE related methods
     */
    @Override
    public void onEnvironmentConnected() {
        MagpieAgent agent = new MagpieAgent("demo-agent", Services.LOGIC_TUPLE);
        PrologAgentMind mind = new PrologAgentMind(getApplicationContext(), R.raw.demo_rules);
        agent.setMind(mind);
        registerAgent(agent);

        ValueDAO valueDAO = new ValueDAO(this);
        valueDAO.open();
        List<Value> weightValues = valueDAO.getAllValues(Type.WEIGHT);
        for (Value value : weightValues) {
            sendEvent(value);
        }
    }

    @Override
    public void onAlertProduced(LogicTupleEvent alert) {
        alertDAO.createAlert(alert);
    }

    private void sendEvent(Value measurement) {
        LogicTupleEvent event = null;
        long timestamp = measurement.getTimestamp();
        String name = measurement.getType().name().toLowerCase();
        if (measurement instanceof SingleValue) {
            String value = String.valueOf(((SingleValue)measurement).getValue());
            event = new LogicTupleEvent(timestamp, name, value);
        } else if (measurement instanceof DoubleValue) {
            String sys = String.valueOf(((DoubleValue)measurement).getFirstValue());
            String dias = String.valueOf(((DoubleValue)measurement).getSecondValue());
            event = new LogicTupleEvent(timestamp, name, sys, dias);
        }
        sendEvent(event);
    }

    private void notifyCallbacks(boolean isChecked) {
        for (OnChangeDummyDataDisplayListener callback : callbacks) {
            callback.displayDummyData(isChecked);
        }
    }
}
