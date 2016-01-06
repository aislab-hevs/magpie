package ch.hevs.aislab.magpie.debs.activity;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.retrofit.PublisherSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

public class PublisherTestFragment extends Fragment implements View.OnClickListener {

    public static final String TEST_TAG = "MAGPIE-Test";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_publisher_test, container, false);
        Button startBtn = (Button) v.findViewById(R.id.testBtn);
        startBtn.setOnClickListener(this);
        return v;

    }

    @Override
    public void onClick(View view) {

        // File to store the results
        DateTime date = DateTime.now();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMM-kkmmss");
        final String filename = "publisher-" + date.toString(dtf) + ".txt";

        // Client Service API for the calls
        final PublisherSvcApi publisherSvc = configureService();

        // Id of the publisher generating the alert
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final long pubId = preferences.getLong(MobileClient.PUBLISHER_ID, 0);

        final List<Long> timestamps = new ArrayList<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 100; i++) {
                    long timestamp = new Date().getTime();
                    Log.d(TEST_TAG, "Iteration number: " + (i + 1) + ", Timestamp: " + timestamp);
                    timestamps.add(timestamp);
                    GlucoseAlert glucose = new GlucoseAlert(2.1, timestamp, GlucoseAlert.Type.HYPOGLYCEMIA);
                    glucose.setPublisherId(pubId);
                    publisherSvc.notifyAlert(pubId, glucose);
                    SystemClock.sleep(2500);
                }

                // Write the results in the file
                FileOutputStream outputStream;
                try {
                    outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    for (long timestamp : timestamps) {
                        String timestampStr = String.valueOf(timestamp);
                        timestampStr = timestampStr + "\n";
                        outputStream.write(timestampStr.getBytes());
                    }
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private PublisherSvcApi configureService() {

        SessionManager session = new SessionManager(getActivity());
        String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        return new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(PublisherSvcApi.class);
    }
}
