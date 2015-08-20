package ch.hevs.aislab.magpie.debs.background;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.gcm.GcmMessageHandler;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.model.SubscriptionResult;
import ch.hevs.aislab.magpie.debs.retrofit.PublisherSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

public class NotificationProcessorReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName();

    public static final String NOTIFICATION_ID = "notificationId";

    public static final String ACTION_REJECT = "ch.hevs.aislab.magpie.background.REJECT";
    public static final String ACTION_ACCEPT = "ch.hevs.aislab.magpie.background.ACCEPT";

    private final Handler handler = new Handler();
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        int notificationId = intent.getExtras().getInt(NOTIFICATION_ID);
        cancelNotification(context, notificationId);

        long pubId = intent.getExtras().getLong(MobileClient.PUBLISHER_ID);
        long subId = intent.getExtras().getLong(GcmMessageHandler.SUBSCRIBER_ID);
        String subUsername = intent.getExtras().getString(GcmMessageHandler.SUBSCRIBER_USERNAME);

        Log.d("Retrofit", "Subscriber ID (NotificationProcessorReceiver): " + subId);

        String action = intent.getAction();
        switch (action) {
            case ACTION_ACCEPT:
                notifySubscription(context, pubId, subId, subUsername, true);
                break;
            case ACTION_REJECT:
                notifySubscription(context, pubId, subId, subUsername, false);
                break;
            default:
                Log.e(TAG, "Action '" + action + "' not understood");
                break;
        }
    }

    private void cancelNotification(Context context, int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    private void notifySubscription(
            final Context context, final long pubId, final long subId, final String subUsername, final boolean result) {

        SessionManager session = new SessionManager(context);
        String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final SubscriptionResult subscriptionResult = new SubscriptionResult(subId, result);

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
                boolean answer = publisherSvc.confirmSubscription(pubId, subscriptionResult);
                if (answer) {
                    if (result) {
                        handler.post(new ShowToastMessage(subUsername + " is now subscribed to your alerts"));
                        Intent intent = new Intent(UIUpdaterReceiver.ACTION_UPDATE_UI);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        handler.post(new ShowToastMessage(subUsername + " will not receive your alerts"));
                    }
                } else {
                    handler.post(new ShowToastMessage("Error while processing the confirmation"));
                }
            }

        });
        t.start();
    }

    class ShowToastMessage extends Thread {
        private String msg;

        ShowToastMessage(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    }
}
