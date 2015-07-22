package ch.hevs.aislab.magpie.debs.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Collection;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.gcm.RegistrationIntentService;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.retrofit.ClientSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.gcm.Preferences;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;


public class MainActivity extends ActionBarActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String TAG = getClass().getName();


    private EditText usernameEditTxt;
    private EditText passwordEditTxt;

    private BroadcastReceiver registrationBroadcastReceiver;
    private ProgressBar registrationProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditTxt = (EditText) findViewById(R.id.usernameEditTxt);
        passwordEditTxt = (EditText) findViewById(R.id.passwordEditTxt);

        registrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                registrationProgressBar.setVisibility(ProgressBar.GONE);
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver,
                new IntentFilter(Preferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Called when register button is pressed
     */
    public void doLogin(View view) {
        // username and password introduced by the user
        final String username = usernameEditTxt.getText().toString();
        final String password = passwordEditTxt.getText().toString();
        // Check that the values are not empty
        if (username.isEmpty() || password.isEmpty()) {
            MainActivity.this.showToast("Type username and password");
            return;
        }

        // Adapter for the HTTPS connections
        final ClientSvcApi clientSvc = new SecuredRestBuilder()
                .setLoginEndpoint(ClientSvcApi.SERVICE_URL + ClientSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId("mobile")
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(ClientSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(ClientSvcApi.class);

        // Thread to do the connection with the server
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String gcmToken = preferences.getString(Preferences.TOKEN, "null");

                MobileClient client;
                try {
                    client = clientSvc.getRoles(gcmToken);
                } catch (Exception e) {
                    if (e instanceof RetrofitError) {
                        RetrofitError error = (RetrofitError) e;
                        if (error.getMessage().contains("HttpHostConnectException")) {
                            MainActivity.this.showToast("Server is down!");
                        } else if (error.getMessage().contains("SecuredRestException")){
                            MainActivity.this.showToast("Incorrect username or password");
                        }
                    }
                    return;
                }

                if (client != null) {
                    if (client.getRoles().contains("SUBSCRIBER")) {
                        MainActivity.this.showToast("Hello " + client.getFirstName() + " " + client.getLastName());
                        Intent i = new Intent(MainActivity.this, SubscriberActivity.class);
                        i.putExtra(MobileClient.SUBSCRIBER_EXTRA, client);
                        startActivity(i);
                    }
                }

            }
        });
        t.start();
    }

    private void showToast(final String message) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
