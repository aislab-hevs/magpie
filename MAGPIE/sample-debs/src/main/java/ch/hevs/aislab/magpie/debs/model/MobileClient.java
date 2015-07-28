package ch.hevs.aislab.magpie.debs.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MobileClient implements Parcelable {

    public static final String EXTRA_PUBLISHER = "publisher";
    public static final String EXTRA_SUBSCRIBER = "subscriber";

    private long id;

    private String username;
    private String gcmToken;

    private String firstName;
    private String lastName;

    private List<String> roles;

    private List<MobileClient> clients;

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

    public List<MobileClient> getClients() {
        return clients;
    }

    public void setClients(List<MobileClient> clients) {
        this.clients = clients;
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
        out.writeList(clients);
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
        clients = new ArrayList<>();
        in.readList(clients, null);
    }
}

