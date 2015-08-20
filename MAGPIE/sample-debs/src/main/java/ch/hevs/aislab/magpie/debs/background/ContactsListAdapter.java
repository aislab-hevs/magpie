package ch.hevs.aislab.magpie.debs.background;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.activity.AlertsActivity;
import ch.hevs.aislab.magpie.debs.activity.ContactsFragment;
import ch.hevs.aislab.magpie.debs.activity.PublisherActivity;
import ch.hevs.aislab.magpie.debs.activity.SubscriberActivity;
import ch.hevs.aislab.magpie.debs.model.MobileClient;

public class ContactsListAdapter extends BaseAdapter {

    private List<MobileClient> contacts = new ArrayList<>();
    private Context mContext;
    boolean isPublisher;


    public ContactsListAdapter(Context mContext, boolean isPublisher) {
        this.mContext = mContext;
        this.isPublisher = isPublisher;
    }

    public void addItem(MobileClient contact) {
        contacts.add(contact);
        notifyDataSetChanged();
    }

    public void removeItem(MobileClient contact) {
        contacts.remove(contact);
        notifyDataSetChanged();
    }

    public void flushList() {
        contacts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public MobileClient getItem(int pos) {
        return contacts.get(pos);
    }

    @Override
    public long getItemId(int id) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_item, parent, false);
        }

        final MobileClient contact = this.getItem(position);
        TextView contactUsernameTxtView = (TextView) convertView.findViewById(R.id.contactUsernameTxtView);
        contactUsernameTxtView.setText(contact.getUsername());

        TextView fullNameTxtView = (TextView) convertView.findViewById(R.id.fullNameTxtView);
        fullNameTxtView.setText(contact.getFullName());

        TextView statusTxtView = (TextView) convertView.findViewById(R.id.statusTxtView);
        statusTxtView.setText(contact.getStatus().toString().toLowerCase());

        if (isPublisher) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(contact);
                    return true;
                }
            });
        } else {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AlertsActivity.class);
                    intent.putExtra(MobileClient.EXTRA_USER, contact);
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    private void showDialog(final MobileClient contact) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setTitle(contact.getFullName())
                .setItems(R.array.contact_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ContactsFragment frag = (ContactsFragment) ((PublisherActivity) mContext)
                                        .getFragmentManager()
                                        .findFragmentById(R.id.pubContentFrame);
                                contacts.clear();
                                frag.revokeSubscription(contact);
                                frag.downloadContacts();
                                break;
                            default:
                                // The other options
                        }
                    }
                });
        alertDialogBuilder.create().show();
    }
}
