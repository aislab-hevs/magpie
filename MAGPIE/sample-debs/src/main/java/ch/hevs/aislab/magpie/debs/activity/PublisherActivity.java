package ch.hevs.aislab.magpie.debs.activity;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.manager.SessionManager;
import ch.hevs.aislab.magpie.debs.model.MobileClient;

public class PublisherActivity extends ActionBarActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler drawerActionHandler = new Handler();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private int mNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        setupToolbar();
        setupDrawerLayout();
        loadNavigationState(savedInstanceState);
        setupNavigationView();
        navigate(mNavItemId);

        MobileClient mc = getIntent().getParcelableExtra(MobileClient.EXTRA_PUBLISHER);
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

                break;
            case R.id.menuPubItem2:
                SessionManager manager = new SessionManager(this);
                manager.logoutUser();
                break;
            default:
                break;
        }
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
}
