package ch.hevs.aislab.magpie.debs.activity;

import android.app.Fragment;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.behavior.PriorityBehaviorAgentMind;
import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.agent.GlucoseBehavior;
import ch.hevs.aislab.magpie.debs.background.UIUpdaterReceiver;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;

public class PublisherActivity extends MagpieActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler drawerActionHandler = new Handler();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private int mNavItemId;

    private LocalBroadcastManager mBroadcastMgr;
    private final IntentFilter intentFilter =
            new IntentFilter(UIUpdaterReceiver.ACTION_UPDATE_UI);
    private final UIUpdaterReceiver receiver = new UIUpdaterReceiver(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        setupToolbar();
        setupDrawerLayout();
        loadNavigationState(savedInstanceState);
        setupNavigationView();
        navigate(mNavItemId);

        MobileClient publisher = getIntent().getParcelableExtra(MobileClient.EXTRA_USER);
        long publisherId = publisher.getId();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putLong(MobileClient.PUBLISHER_ID, publisherId).apply();

        mBroadcastMgr = LocalBroadcastManager.getInstance(getApplicationContext());
        mBroadcastMgr.registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        mBroadcastMgr.unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void sensorConnectionResult(int code) {

    }

    private void setupToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.pubDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(
                PublisherActivity.this, drawerLayout, R.string.open_menu, R.string.close_menu);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void loadNavigationState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mNavItemId = R.id.menuPubItem1;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }
    }

    private void setupNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.pubNavigationView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);
    }

    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.menuPubItem1:
                Fragment magpieFrag = new MagpieFragment();
                setFragmentArgs(magpieFrag);
                getFragmentManager()
                        .beginTransaction()
                        .remove(magpieFrag)
                        .replace(R.id.pubContentFrame, magpieFrag)
                        .commit();
                break;
            case R.id.menuPubItem2:
                Fragment contactsFrag = new ContactsFragment();
                setFragmentArgs(contactsFrag);
                getFragmentManager()
                        .beginTransaction()
                        .remove(contactsFrag)
                        .replace(R.id.pubContentFrame, contactsFrag)
                        .commit();
                break;
            case R.id.menuPubItem3:
                SessionManager manager = new SessionManager(this);
                manager.logoutUser();
                break;
            default:
                break;
        }
    }

    private void setFragmentArgs(Fragment frag) {
        Bundle bundle = new Bundle();
        String key = MobileClient.EXTRA_USER;
        MobileClient value = getIntent().getParcelableExtra(MobileClient.EXTRA_USER);
        bundle.putParcelable(key, value);
        frag.setArguments(bundle);
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            if (!(mi.getItemId() == menuItem.getItemId())) {
                mi.setCheckable(false);
            }
        }
        menuItem.setCheckable(true);
        if (menuItem.getTitle().equals(getResources().getString(R.string.logout))) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        drawerLayout.closeDrawer(GravityCompat.START);
        drawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onEnvironmentConnected() {
        MagpieAgent agent = new MagpieAgent("monitoring_agent", Services.LOGIC_TUPLE);
        PriorityBehaviorAgentMind mind = new PriorityBehaviorAgentMind();
        GlucoseBehavior hypoBehavior = new GlucoseBehavior(this, agent, 0);
        mind.addBehavior(hypoBehavior);
        agent.setMind(mind);
        getService().registerAgent(agent);
    }

    @Override
    public void onAlertProduced(LogicTupleEvent alert) {

    }
}
