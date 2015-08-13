package ch.hevs.aislab.magpie.debs.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.model.RequestSubscriptionResult;
import ch.hevs.aislab.magpie.debs.retrofit.SubscriberSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

public class RequestSubscriptionFragment extends Fragment implements View.OnClickListener {

    private long subscriberId;

    private EditText pubUsernameEditTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request_subscription, container, false);

        pubUsernameEditTxt = (EditText) v.findViewById(R.id.pubUsernameEditTxt);

        Button btn = (Button) v.findViewById(R.id.requestSubsBtn);
        btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        MobileClient mc = getActivity().getIntent().getParcelableExtra(MobileClient.EXTRA_USER);
        subscriberId = mc.getId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.requestSubsBtn:
                String pubUsername = pubUsernameEditTxt.getText().toString();
                if (pubUsername.isEmpty()) {
                    Toast.makeText(getActivity(), "Please, specify a username", Toast.LENGTH_LONG).show();
                } else if (pubUsername.equals(subscriberId)) {
                    Toast.makeText(getActivity(), "You can't subscribe to yourself", Toast.LENGTH_LONG).show();
                } else {
                    doSubscriptionRequest(pubUsername);
                }
                break;
            default:
                break;
        }
    }

    private void doSubscriptionRequest(final String pubUsername) {
        SessionManager session = new SessionManager(getActivity());
        String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final SubscriberSvcApi subscriberSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(SubscriberSvcApi.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final RequestSubscriptionResult answer =
                        subscriberSvc.doSubscriptionByUsername(subscriberId, pubUsername);
                RequestSubscriptionFragment.this.getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                switch (answer) {
                                    case STATUS_PENDING:
                                        showToastMessage("A previous request sent to " + pubUsername + " is pending confirmation");
                                        pubUsernameEditTxt.setText("");
                                        break;
                                    case STATUS_ALREADY_SUBSCRIBED:
                                        showToastMessage("You are already subscribed to " + pubUsername);
                                        pubUsernameEditTxt.setText("");
                                        break;
                                    case STATUS_OK:
                                        showToastMessage("The request was sent to " + pubUsername);
                                        pubUsernameEditTxt.setText("");
                                        break;
                                    default:
                                        showToastMessage("Answer is " + answer);
                                }

                            }

                            private void showToastMessage(String message) {
                                Toast.makeText(RequestSubscriptionFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });
        t.start();
    }
}
