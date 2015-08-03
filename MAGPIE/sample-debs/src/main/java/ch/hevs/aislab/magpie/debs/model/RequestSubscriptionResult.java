package ch.hevs.aislab.magpie.debs.model;


public enum RequestSubscriptionResult {

    INVALID_SUBSCRIBER,
    INVALID_PUBLISHER,
    GCM_ERROR,
    STATUS_PENDING,
    STATUS_ALREADY_SUBSCRIBED,
    STATUS_OK
}
