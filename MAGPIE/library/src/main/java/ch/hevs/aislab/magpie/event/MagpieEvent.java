package ch.hevs.aislab.magpie.event;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MagpieEvent implements Parcelable {

	private long timestamp;
	protected String type;
	 
	
	protected MagpieEvent() {
		this.timestamp = System.currentTimeMillis();
	}


    public String getStringTimestamp(String format) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

	public long getTimestamp() {
		return timestamp;
	}

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

	public String getType() { 
		return type;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(timestamp);
        out.writeString(type);
    }

    public static final Parcelable.Creator<MagpieEvent> CREATOR
            = new Parcelable.Creator<MagpieEvent>() {

        public MagpieEvent createFromParcel(Parcel in) {
            return new MagpieEvent(in);
        }

        public MagpieEvent[] newArray(int size) {
            return new MagpieEvent[size];
        }
    };

    private MagpieEvent(Parcel in) {
        timestamp = in.readLong();
        type = in.readString();
    }
}
