package ch.hevs.aislab.magpie.broker.model;

/**
 * Used by the SubscriberController to report to a 
 * subscriber the result about a subscription request 
 */
public enum RequestSubscriptionResult {
	
	INVALID_SUBSCRIBER,
	INVALID_PUBLISHER,
	GCM_ERROR,
	STATUS_PENDING,
	STATUS_ALREADY_SUBSCRIBED,
	STATUS_OK
}
