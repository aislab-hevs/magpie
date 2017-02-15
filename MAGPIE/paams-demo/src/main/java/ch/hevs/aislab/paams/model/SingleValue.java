package ch.hevs.aislab.paams.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleValue extends Value implements Parcelable {

    private double value;


    public SingleValue() {

    }

    private SingleValue(Parcel in) {
        id = in.readLong();
        value = in.readDouble();
        timestamp = in.readLong();
        type = Type.valueOf(in.readString());
        marked = (in.readByte() == 1);
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static final Creator<SingleValue> CREATOR = new Creator<SingleValue>() {
        @Override
        public SingleValue createFromParcel(Parcel in) {
            return new SingleValue(in);
        }

        @Override
        public SingleValue[] newArray(int size) {
            return new SingleValue[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(value);
        dest.writeLong(timestamp);
        dest.writeString(type.name());
        dest.writeByte((byte) (marked? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
