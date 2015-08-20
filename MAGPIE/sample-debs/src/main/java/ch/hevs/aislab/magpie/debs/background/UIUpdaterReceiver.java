package ch.hevs.aislab.magpie.debs.background;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.activity.ContactsFragment;
import ch.hevs.aislab.magpie.debs.activity.PublisherActivity;

public class UIUpdaterReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_UI =
            "ch.hevs.aislab.magpie.debs.background.UPDATE_UI";

    private Context mContext;

    public UIUpdaterReceiver(Context context) {
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mContext instanceof PublisherActivity) {
            PublisherActivity pubActivity = (PublisherActivity) mContext;
            Fragment frag =
                    pubActivity.getFragmentManager().findFragmentById(R.id.pubContentFrame);
            if (frag instanceof ContactsFragment) {
                ContactsFragment contactsFrag = (ContactsFragment) frag;
                if (contactsFrag.isVisible()) {
                    contactsFrag.downloadContacts();
                }
            }
        }
    }
}
