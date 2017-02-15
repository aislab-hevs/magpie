package ch.hevs.aislab.paams.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Value {

    protected long id;
    long timestamp;
    protected Type type;

    boolean marked;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getStringTimestamp(String format) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }
}
