package ch.hevs.aislab.magpie.debs.activity;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.background.ContactsListAdapter;
import ch.hevs.aislab.magpie.debs.credentials.SessionManager;
import ch.hevs.aislab.magpie.debs.model.MobileClient;
import ch.hevs.aislab.magpie.debs.model.SubscriptionStatus;
import ch.hevs.aislab.magpie.debs.retrofit.PublisherSvcApi;
import ch.hevs.aislab.magpie.debs.retrofit.SecuredRestBuilder;
import ch.hevs.aislab.magpie.debs.retrofit.UnsafeHttpsClient;
import ch.hevs.aislab.magpie.debs.retrofit.UserSvcApi;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

public class ContactsFragment extends ListFragment {

    private MobileClient user;
    private boolean isPublisher;
    private ContactsListAdapter contactsAdapter;

    private Collection<MobileClient> contactsAccepted;
    private Collection<MobileClient> contactsPending;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        user = getArguments().getParcelable(MobileClient.EXTRA_USER);

        if (user.getRoles().contains(MobileClient.ROLE_PUBLISHER)) {
            isPublisher = true;
        } else {
            isPublisher = false;
        }

        contactsAdapter = new ContactsListAdapter(getActivity(), isPublisher);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        downloadContacts();
    }

    public void downloadContacts() {

        contactsAdapter.flushList();
        final long subId = user.getId();

        SessionManager session = new SessionManager(getActivity());
        final String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final UserSvcApi userSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(UserSvcApi.class);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ContactsFragment.this.contactsAccepted = userSvc.getContactsAccepted(subId);
                ContactsFragment.this.contactsPending = userSvc.getContactsPending(subId);
                ContactsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadContacts();
                        showHeaderView();
                        ContactsFragment.this.setListAdapter(contactsAdapter);
                    }
                });
            }
        });
        t.start();
    }

    public void revokeSubscription(final MobileClient subscriber) {

        SessionManager session = new SessionManager(getActivity());
        final String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);
        String password = session.getUserDetails().get(SessionManager.KEY_PASSWORD);

        final PublisherSvcApi publisherSvc = new SecuredRestBuilder()
                .setLoginEndpoint(UserSvcApi.SERVICE_URL + UserSvcApi.TOKEN_PATH)
                .setUsername(username)
                .setPassword(password)
                .setClientId(UserSvcApi.CLIENT_ID)
                .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                .setEndpoint(UserSvcApi.SERVICE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(PublisherSvcApi.class);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result = publisherSvc.revokeSubscription(user.getId(), subscriber.getId());
                ContactsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            Toast.makeText(ContactsFragment.this.getActivity(),
                                    subscriber.getFullName() + " will no longer receive alerts from you",
                                    Toast.LENGTH_LONG)
                                    .show();
                            contactsAdapter.removeItem(subscriber);
                        }
                    }
                });
            }
        });
        t.start();
    }

    private void loadContacts() {
        for (MobileClient contact: contactsAccepted) {
            contact.setStatus(SubscriptionStatus.ACCEPTED);
            contactsAdapter.addItem(contact);
        }
        for (MobileClient contact : contactsPending) {
            contact.setStatus(SubscriptionStatus.PENDING);
            contactsAdapter.addItem(contact);
        }
    }

    private void showHeaderView() {
        if ((!ContactsFragment.this.contactsAccepted.isEmpty()) ||
                (!ContactsFragment.this.contactsPending.isEmpty())) {
            TextView wellcomeMsgTxtView = (TextView) LayoutInflater
                    .from(ContactsFragment.this.getActivity())
                    .inflate(R.layout.wellcome_header_view, getListView(), false);
            String fullName = user.getFullName();
            if (isPublisher) {
                wellcomeMsgTxtView.setText("Hello " + fullName + ",\nyour subscribers are:");
            } else {
                wellcomeMsgTxtView.setText("Hello " + fullName + ",\nyou are subscribed to:");
            }

            if (getListView().getHeaderViewsCount() == 0) {
                getListView().addHeaderView(wellcomeMsgTxtView);
            }
        }
    }
}
