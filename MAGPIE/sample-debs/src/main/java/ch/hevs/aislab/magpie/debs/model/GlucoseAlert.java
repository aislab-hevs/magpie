package ch.hevs.aislab.magpie.debs.model;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GlucoseAlert {

    private long publisherId;
    private Double value;
    private long timestamp;
    private Type type;

    public GlucoseAlert(Double value, long timestamp, Type type) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDate() {
        DateTime date = new DateTime(timestamp);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("kk:mm dd/MM/yyyy");
        return date.toString(dtf);
    }

    public enum Type {
        UNKNOWN,
        HYPOGLYCEMIA,
        HYPERGLYCEMIA
    }
}
