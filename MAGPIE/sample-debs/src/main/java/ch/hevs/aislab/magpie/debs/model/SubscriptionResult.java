package ch.hevs.aislab.magpie.debs.model;

/**
 * Object sent by the publisher to notify
 * the decision for a subscription request
 */
public class SubscriptionResult {

    private long subscriberId;
    private boolean decision;

    public SubscriptionResult() {

    }

    public SubscriptionResult(long subscriberId, boolean decision) {
        this.subscriberId = subscriberId;
        this.decision = decision;
    }

    public long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public boolean isDecision() {
        return decision;
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
    }

}
