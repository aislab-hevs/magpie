package ch.hevs.aislab.paams.model;


import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Value implements Parcelable, Comparable<Value> {

    protected long id;
    long timestamp;
    protected Type type;
    boolean marked;
    boolean dummy;


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

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public String getStringTimestamp(String format) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    @Override
    public int compareTo(Value v) {
        return this.timestamp < v.timestamp ? -1 :
                this.timestamp > v.timestamp ? 1 : 0;
    }
}
