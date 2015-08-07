package ch.hevs.aislab.magpie.debs.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.Collection;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.background.AlertsListAdapter;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.SubscriberSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;


public class AlertsActivity extends ListActivity {

    private AlertsListAdapter alertsAdapter;

    private MobileClient publisher;
    private Collection<GlucoseAlert> publisherAlerts;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        publisher = getIntent().getParcelableExtra(MobileClient.EXTRA_USER);

        progress = ProgressDialog.show(this, "", "Loading alerts ...", true);
        alertsAdapter = new AlertsListAdapter(this);
        downloadAlerts(publisher.getId());

    }

    private void downloadAlerts(final long pubId) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final long subId = preferences.getLong(MobileClient.SUBSCRIBER_ID, 0);

        SessionManager session = new SessionManager(this);
        final String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final SubscriberSvcApi subscriberSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(SubscriberSvcApi.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                publisherAlerts = subscriberSvc.getAlertsByUser(subId, pubId);
                AlertsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadAlerts();
                        AlertsActivity.this.setListAdapter(alertsAdapter);
                        loadEmptyContent();
                        progress.dismiss();
                    }
                });
            }
        });
        t.start();
    }

    private void loadAlerts() {
        for (GlucoseAlert alert : publisherAlerts) {
            alertsAdapter.addItem(alert);
        }
    }

    private void loadEmptyContent() {
        if (alertsAdapter.isEmpty()) {
            TextView textView = (TextView) findViewById(R.id.noAlertsTxtView);
            String publisherName = publisher.getFullName();
            String msg = String.format(getResources().getString(R.string.no_alerts), publisherName);
            textView.setText(msg);
        }
    }
}
