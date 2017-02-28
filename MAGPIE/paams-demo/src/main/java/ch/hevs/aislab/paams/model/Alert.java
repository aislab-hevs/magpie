package ch.hevs.aislab.paams.model;


public class Alert {

    private long id;
    private String name;
    private long timestamp;

    public Alert(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
