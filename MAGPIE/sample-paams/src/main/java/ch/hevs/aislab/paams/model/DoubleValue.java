package ch.hevs.aislab.paams.model;


import android.os.Parcel;
import android.os.Parcelable;

public class DoubleValue extends Value implements Parcelable {

    private int firstValue;
    private int secondValue;

    public DoubleValue() {

    }

    private DoubleValue(Parcel in) {
        id = in.readLong();
        firstValue = in.readInt();
        secondValue = in.readInt();
        timestamp = in.readLong();
        type = Type.valueOf(in.readString());
        marked = (in.readByte() == 1);
        dummy = (in.readByte() == 1);
    }

    public int getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(int firstValue) {
        this.firstValue = firstValue;
    }

    public int getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(int secondValue) {
        this.secondValue = secondValue;
    }

    public static final Creator<DoubleValue> CREATOR = new Creator<DoubleValue>() {
        @Override
        public DoubleValue createFromParcel(Parcel in) {
            return new DoubleValue(in);
        }

        @Override
        public DoubleValue[] newArray(int size) {
            return new DoubleValue[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(firstValue);
        dest.writeInt(secondValue);
        dest.writeLong(timestamp);
        dest.writeString(type.name());
        dest.writeByte((byte) (marked? 1 : 0));
        dest.writeByte((byte) (dummy ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
