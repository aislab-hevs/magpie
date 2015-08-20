package ch.hevs.aislab.magpie.debs.gcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileOutputStream;
import java.util.Date;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.activity.PublisherTestFragment;
import ch.hevs.aislab.magpie.debs.background.NotificationProcessorReceiver;
import ch.hevs.aislab.magpie.debs.model.MobileClient;

public class GcmMessageHandler extends GcmListenerService {

    private final String TAG = getClass().getName();

    private static final String FILENAME = "Subscrber.txt";

    private static final String MESSAGE_TYPE = "type";

    /**
     * Types of messages
     */
    private static final String SUBSCRIPTION_REQUEST = "subscriptionRequest";
    private static final String ALERT_NOTIFICATION = "alertNotification";

    /**
     * Additional parameters
     */
    public static final String SUBSCRIBER_ID = "subscriberId";
    public static final String SUBSCRIBER_USERNAME = "subscriberUsername";
    private static final String PUBLISHER_ID = "publisherId";
    private static final String PUBLISHER_USERNAME = "publisherUsername";
    private static final String ALERT_TYPE = "alertType";
    private static final String ALERT_TIMESTAMP = "alertTimestamp";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String messageType = data.getString(MESSAGE_TYPE);
        Log.d(TAG, "GCM message received, type: " + messageType);

        switch (messageType) {
            case SUBSCRIPTION_REQUEST:
                processSubscriptionRequest(data);
                break;
            case ALERT_NOTIFICATION:
                // Method used for the evaluation of the whole architecture
                storeTime(data);
                //processAlertNotification(data);
                break;
            default:
                break;
        }
    }

    private void processSubscriptionRequest(Bundle data) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long pubId = preferences.getLong(MobileClient.PUBLISHER_ID, 0);

        // The id must be parsed, as it is in fact a String
        long subId = Long.parseLong(data.getString(SUBSCRIBER_ID));
        String subUsername = data.getString(SUBSCRIBER_USERNAME);
        int notificationId = 1;

        // Pending Intent for the Reject action
        PendingIntent rejectPendingIntent = createPendingIntentForActions(
                NotificationProcessorReceiver.ACTION_REJECT, pubId, subId, subUsername, notificationId);

        // Pending Intent for the Accept action
        PendingIntent acceptPendingIntent = createPendingIntentForActions(
                NotificationProcessorReceiver.ACTION_ACCEPT, pubId, subId, subUsername, notificationId);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MAGPIE Notification")
                .setContentText("Subscription request from " + subUsername)
                .addAction(R.drawable.abc_ic_clear_mtrl_alpha, "Reject", rejectPendingIntent)
                .addAction(R.drawable.ic_done_black_24dp, "Accept", acceptPendingIntent);

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    private void processAlertNotification(Bundle data) {

        String pubUsername = data.getString(PUBLISHER_USERNAME);
        String type = data.getString(ALERT_TYPE).toLowerCase();

        long timestamp = Long.parseLong(data.getString(ALERT_TIMESTAMP));
        MutableDateTime date = new MutableDateTime(timestamp);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("kk:mm dd/MM/yyyy");

        int notificationId = 2;

        String msg =  "Alert from " + pubUsername + "\nType: " + type + "\nDate: " + date.toString(dtf);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MAGPIE Notification")
                .setContentText("Alert from " + pubUsername)
                .setStyle(new Notification.BigTextStyle().bigText(msg));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    private PendingIntent createPendingIntentForActions(
            String action, long pubId, long subId, String subUsername, int notificationId) {
        Intent intent = new Intent(this, NotificationProcessorReceiver.class);
        intent.setAction(action);
        intent.putExtra(MobileClient.PUBLISHER_ID, pubId);
        intent.putExtra(SUBSCRIBER_ID, subId);
        intent.putExtra(SUBSCRIBER_USERNAME, subUsername);
        intent.putExtra(NotificationProcessorReceiver.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    private void storeTime(Bundle data) {

        long timestamp = new Date().getTime();
        Log.d(PublisherTestFragment.TEST_TAG,
                "Alert with timestamp: " + Long.parseLong(data.getString(ALERT_TIMESTAMP)));
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_APPEND);
            String timestampStr = String.valueOf(timestamp);
            timestampStr = timestampStr + "\n";
            outputStream.write(timestampStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
