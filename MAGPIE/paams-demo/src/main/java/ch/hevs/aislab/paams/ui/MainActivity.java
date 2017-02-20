package ch.hevs.aislab.paams.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;

public class MainActivity extends AppCompatActivity implements AddValueFragment.OnAddedNewMeasurementListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupInstances();
        setNavigationView();

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private void setNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

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
            case R.id.item_bloodpressure:
                genericFragment = PhysioParamFragment.newInstance(Type.BLOOD_PRESSURE);
                fragmentTag = Type.BLOOD_PRESSURE.name();
                break;
            case R.id.item_weight:
                genericFragment = PhysioParamFragment.newInstance(Type.WEIGHT);
                fragmentTag = Type.WEIGHT.name();
                break;
        }

        if (genericFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, genericFragment, fragmentTag)
                    .commit();
        }

        setTitle(itemDrawer.getTitle());
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
        PhysioParamFragment fragment = null;
        switch (measurement.getType()) {
            case GLUCOSE:
                fragment = (PhysioParamFragment)
                        getSupportFragmentManager().findFragmentByTag(Type.GLUCOSE.name());
                break;
            case WEIGHT:
                fragment = (PhysioParamFragment)
                        getSupportFragmentManager().findFragmentByTag(Type.WEIGHT.name());
                break;
        }
        PhysioParamFragment.SectionsAdapter adapter =
                (PhysioParamFragment.SectionsAdapter) fragment.getViewPager().getAdapter();
        ListValuesFragment listValuesFragment = (ListValuesFragment) adapter.getItem(0);
        listValuesFragment.getValueAdapter().addItem(measurement);
    }

    @Override
    public void addDoubleValue(DoubleValue measurement) {
        PhysioParamFragment fragment = (PhysioParamFragment)
                getSupportFragmentManager().findFragmentByTag(Type.BLOOD_PRESSURE.name());
        PhysioParamFragment.SectionsAdapter adapter =
                (PhysioParamFragment.SectionsAdapter) fragment.getViewPager().getAdapter();
        ListValuesFragment listValuesFragment = (ListValuesFragment) adapter.getItem(0);
        listValuesFragment.getValueAdapter().addItem(measurement);
    }
}
