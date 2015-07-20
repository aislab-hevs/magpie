package ch.hevs.aislab.magpie.publisher;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.ConnectException;

import ch.hevs.aislab.magpie.publisher.model.Publisher;
import ch.hevs.aislab.magpie.publisher.retrofit.CustomErrorHandler;
import ch.hevs.aislab.magpie.publisher.retrofit.PublisherSvcApi;
import retrofit.RestAdapter;


public class MainActivity extends ActionBarActivity {

    private EditText firstNameEditTxt;
    private EditText lastNameEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstNameEditTxt = (EditText) findViewById(R.id.firstNameEditTxt);
        lastNameEditTxt = (EditText) findViewById(R.id.lastNameEditTxt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when register button is pressed
     */
    public void registerUser(View view) {
        // Name introduced by the user
        final String firstName = firstNameEditTxt.getText().toString();
        final String lastName = lastNameEditTxt.getText().toString();
        // Check that the values are not empty
        if (firstName.isEmpty() || lastName.isEmpty()) {
            MainActivity.this.showToast("Type first and last name");
            return;
        }
        // Adapter for the HTTP connections
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://10.0.2.2:8080")
                .setErrorHandler(new CustomErrorHandler(this))
                .build();

        final PublisherSvcApi service  = restAdapter.create(PublisherSvcApi.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.addPublisher(new Publisher(firstName, lastName));
                    MainActivity.this.showToast("Hello " + firstName + " " + lastName + "!");
                } catch (ConnectException e) {
                    MainActivity.this.showToast("ERROR: server is down");
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
}
