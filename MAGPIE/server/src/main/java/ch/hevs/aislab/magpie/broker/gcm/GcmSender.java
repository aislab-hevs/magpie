package ch.hevs.aislab.magpie.broker.gcm;

import java.io.IOException;
import java.util.List;

import ch.hevs.aislab.magpie.broker.model.GlucoseAlert;
import ch.hevs.aislab.magpie.broker.model.MobileClient;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GcmSender {
	
	private static final String API_KEY = "YOUR_GCM_API_KEY";
	
	private static final String MESSAGE_TYPE = "type";
	
	/**
	 * Types of messages
	 */
	private static final String SUBSCRIPTION_REQUEST = "subscriptionRequest";
	private static final String ALERT_NOTIFICATION = "alertNotification";
	
	/**
	 * Additional parameters
	 */
	private static final String SUBSCRIBER_ID = "subscriberId";
	private static final String SUBSCRIBER_USERNAME = "subscriberUsername";
	private static final String PUBLISHER_ID = "publisherId";
	private static final String PUBLISHER_USERNAME = "publisherUsername";
	private static final String ALERT_TYPE = "alertType";
	private static final String ALERT_TIMESTAMP = "alertTimestamp";
	
	public static void requestSubscription(long id, String subUsername, String pubGcmToken) throws IOException {
				
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
				.addData(MESSAGE_TYPE, SUBSCRIPTION_REQUEST)
				.addData(SUBSCRIBER_ID, id + "")
				.addData(SUBSCRIBER_USERNAME, subUsername)
				.build();
		Result result = sender.send(msg, pubGcmToken, 1);
		System.out.println(msg.toString());
		System.out.println(result.toString());		
	}
	
	public static void notifyAlert(MobileClient publisher, GlucoseAlert alert,
			List<String> subGcmTokens) throws IOException {
		
		Sender sender = new Sender(API_KEY);
		Message msg = new Message.Builder()
				.addData(MESSAGE_TYPE, ALERT_NOTIFICATION)
				.addData(PUBLISHER_ID, publisher.getId() + "")
				.addData(PUBLISHER_USERNAME, publisher.getUsername())
				.addData(ALERT_TYPE, alert.getType().toString())
				.addData(ALERT_TIMESTAMP, alert.getTimestamp() + "")
				.build();
		MulticastResult result = sender.sendNoRetry(msg, subGcmTokens);
		System.out.println(msg.toString());
		System.out.println(result.toString());	
	}
}