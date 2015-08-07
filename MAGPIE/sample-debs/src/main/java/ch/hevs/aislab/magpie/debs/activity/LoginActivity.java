package ch.hevs.aislab.magpie.debs.activity;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.gcm.Preferences;
import ch.hevs.aislab.magpie.debs.gcm.RegistrationIntentService;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;


public class LoginActivity extends ActionBarActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String TAG = getClass().getName();

    private EditText usernameEditTxt;
    private EditText passwordEditTxt;

    private BroadcastReceiver registrationBroadcastReceiver;
    private ProgressBar registrationProgressBar;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditTxt = (EditText) findViewById(R.id.usernameEditTxt);
        passwordEditTxt = (EditText) findViewById(R.id.passwordEditTxt);

        registrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                registrationProgressBar.setVisibility(ProgressBar.GONE);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                String gcmToken = preferences.getString(Preferences.TOKEN, "null");
                Log.i(TAG, "GCM Token in LoginActivity: " + gcmToken);
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUserDetails().get(SessionManager.KEY_USERNAME);
            String password = sessionManager.getUserDetails().get(SessionManager.KEY_PASSWORD);
            redirectToMainActivity(username, password);
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
            LoginActivity.this.showToast("Type username and password");
            return;
        }
        redirectToMainActivity(username, password);
    }

    private void redirectToMainActivity(final String username, final String password) {

        // Adapter for the HTTPS connections
        final UserSvcApi clientSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(UserSvcApi.class);

        // Thread to do the connection with the server
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "" , "Please wait ..." ,true);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                String gcmToken = preferences.getString(Preferences.TOKEN, "null");

                MobileClient client;
                try {
                    client = clientSvc.getUser(gcmToken);
                    LoginActivity.this.sessionManager.createLoginSession(username, password);
                } catch (Exception e) {
                    if (e instanceof RetrofitError) {
                        RetrofitError error = (RetrofitError) e;
                        if (error.getMessage().contains("HttpHostConnectException")) {
                            LoginActivity.this.showToast("Server is down!");
                        } else if (error.getMessage().contains("SecuredRestException")){
                            LoginActivity.this.showToast("Incorrect username or password");
                        }
                    }
                    dialog.dismiss();
                    return;
                }

                if (client != null) {
                    if (client.getRoles().contains(MobileClient.ROLE_SUBSCRIBER)) {
                        Intent i = new Intent(LoginActivity.this, SubscriberActivity.class);
                        i.putExtra(MobileClient.EXTRA_USER, client);
                        startActivity(i);
                    } else if (client.getRoles().contains(MobileClient.ROLE_PUBLISHER)) {
                        Intent i = new Intent(LoginActivity.this, PublisherActivity.class);
                        i.putExtra(MobileClient.EXTRA_USER, client);
                        startActivity(i);
                    }
                }
                dialog.dismiss();
            }
        });
        t.start();
    }

    private void showToast(final String message) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
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
