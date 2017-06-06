package ch.hevs.aislab.paams.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alert implements Parcelable, Comparable<Alert> {

    private long id;
    private String name;
    private long timestamp;
    private boolean marked;
    private boolean dummy;

    public Alert() {

    }

    public Alert(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    private Alert(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = in.readLong();
        marked = (in.readByte() == 1);
        dummy = (in.readByte() == 1);
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

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel parcel) {
            return new Alert(parcel);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (marked? 1 : 0));
        dest.writeByte((byte) (dummy ? 1 : 0));
    }

    @Override
    public int compareTo(Alert a) {
        return this.timestamp < a.timestamp ? -1 :
                this.timestamp > a.timestamp ? 1 : 0;
    }
}
