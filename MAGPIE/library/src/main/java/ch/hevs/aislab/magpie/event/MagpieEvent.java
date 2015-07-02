package ch.hevs.aislab.magpie.event;

import android.os.Parcel;
import android.os.Parcelable;

public class MagpieEvent implements Parcelable {

	private long timeStamp;
	protected String type;
	 
	
	protected MagpieEvent() {
		this.timeStamp = System.currentTimeMillis();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
        out.writeLong(timeStamp);
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
        timeStamp = in.readLong();
        type = in.readString();
    }
}
