package ch.hevs.aislab.magpie.debs.agent;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.behavior.Behavior;
import ch.hevs.aislab.magpie.debs.activity.PublisherActivity;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.retrofit.PublisherSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

public class GlucoseBehavior extends Behavior {

    private static final double HYPO_THRESHOLD = 3.9;
    private static final double HYPER_THRESHOLD = 7.2;

    public GlucoseBehavior(Context context, MagpieAgent agent, int priority) {
        setContext(context);
        setAgent(agent);
        setPriority(priority);
    }

    @Override
    public void action(MagpieEvent event) {

        LogicTupleEvent lte = (LogicTupleEvent) event;
        double value = Double.parseDouble(lte.getArguments().get(0));

        GlucoseAlert glucose = new GlucoseAlert(value, lte.getTimeStamp(), GlucoseAlert.Type.UNKNOWN);
        if (value < HYPO_THRESHOLD) {
            showToastMessage("ALERT: Hypoglycemia detected!");
            glucose.setType(GlucoseAlert.Type.HYPOGLYCEMIA);
            publishAlert(glucose);
            return;
        } else if ((value <= HYPO_THRESHOLD) && (value >= HYPER_THRESHOLD)) {
            showToastMessage("Your glucose is within the normal range");
            return;
        } else if (value > HYPER_THRESHOLD) {
            showToastMessage("ALERT: Hyperglycemia detected!");
            glucose.setType(GlucoseAlert.Type.HYPERGLYCEMIA);
            publishAlert(glucose);
            return;
        }
    }

    private void showToastMessage(final String msg) {
        ((PublisherActivity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void publishAlert(final GlucoseAlert glucose) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final long pubId = preferences.getLong(MobileClient.PUBLISHER_ID, 0);

        SessionManager session = new SessionManager(getContext());
        String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final PublisherSvcApi publisherSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(PublisherSvcApi.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                glucose.setPublisherId(pubId);
                final boolean result = publisherSvc.notifyAlert(pubId, glucose);
                ((PublisherActivity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            Toast.makeText(getContext(),
                                    "The alert was successfully notified to your contacts",
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(getContext(),
                                    "You don't have contacts to notifiy the alert",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });
        t.start();
    }

    @Override
    public boolean isTriggered(MagpieEvent event) {
        LogicTupleEvent condition = (LogicTupleEvent) event;
        return condition.getName().equals("glucose");
    }
}
