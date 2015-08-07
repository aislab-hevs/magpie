package ch.hevs.aislab.magpie.broker.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GlucoseAlert {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long publisherId;
	private Double value;
    private long timestamp;
    private Type type;
    
    public GlucoseAlert() {
    	
    }

    public GlucoseAlert(Double value, long timestamp, Type type) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
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

    public enum Type {
        UNKNOWN,
        HYPOGLYCEMIA,
        HYPERGLYCEMIA
    }
    
    @Override
    public String toString() {
    	return "GlucoseAlert[[id:" + id + 
    			"],[pubId:" + publisherId +
    			"],[value:" + value + 
    			"],[timestamp:" + timestamp +
    			"],[type:" + type.toString() + "]]";
    }
}
