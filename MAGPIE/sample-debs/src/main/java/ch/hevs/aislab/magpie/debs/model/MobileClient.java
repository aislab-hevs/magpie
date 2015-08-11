package ch.hevs.aislab.magpie.debs.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

public class MobileClient implements Parcelable {

    public static final String EXTRA_USER = "user";

    public static final String ROLE_PUBLISHER = "PUBLISHER";
    public static final String ROLE_SUBSCRIBER = "SUBSCRIBER";

    public static final String PUBLISHER_ID = "publisherId";
    public static final String SUBSCRIBER_ID = "subscriberId";

    private long id;

    private String username;
    private String gcmToken;

    private String firstName;
    private String lastName;

    private List<String> roles;

    private SubscriptionStatus status;

    public MobileClient() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(username);
        out.writeString(gcmToken);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeStringList(roles);
    }

    public static final Parcelable.Creator<MobileClient> CREATOR
            = new Parcelable.Creator<MobileClient>() {

        @Override
        public MobileClient createFromParcel(Parcel parcel) {
            return new MobileClient(parcel);
        }

        @Override
        public MobileClient[] newArray(int size) {
            return new MobileClient[size];
        }
    };

    private MobileClient(Parcel in) {
        id = in.readLong();
        username = in.readString();
        gcmToken = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        roles = in.createStringArrayList();
    }

    /**
     * Two MobileClients are considered equals if they have the same username
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobileClient) {
            MobileClient other = (MobileClient) obj;
            return Objects.equals(username, other.username);
        } else {
            return false;
        }
    }
}

