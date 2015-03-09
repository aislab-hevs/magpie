package ch.hevs.aislab.magpie.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.environment.Environment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class MagpieService extends Service {

	/** Used for debugging */
	private final String TAG = getClass().getName();

    /** Shared Preferences store the agents' names and the 'first time' boolean */
    private static final String MAGPIE_PREFS = "magpie_prefs";

    private static final String FIRST_TIME_KEY = "first_time";
    private static final String AGENTS_KEY = "agent_names";

	private final IBinder mBinder = new MagpieBinder();

	private Environment mEnvironment;

	private static Context mContext; // Can be in conflict with Context in MagpieApp

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");

		// Get the instance of the environment
        mEnvironment = Environment.getInstance();

        Log.i(TAG, "Agents onCreate(): " + mEnvironment.getRegisteredAgents().keySet().size());

        SharedPreferences settings = getSharedPreferences(MAGPIE_PREFS, MODE_PRIVATE);

        boolean firstTime = settings.getBoolean(FIRST_TIME_KEY, true);

        Log.i(TAG, "Service first time? " + firstTime);

        if (!firstTime) {
            Set<String> agentNames = settings.getStringSet(AGENTS_KEY, new HashSet<String>());
            Iterator<String> iterator = agentNames.iterator();
            while (iterator.hasNext()) {
                String agentName = iterator.next();

                ObjectInputStream ois = null;
                try {
                    FileInputStream fis = getApplicationContext()
                            .openFileInput(agentName + MagpieAgent.BODY_KEY);
                    ois = new ObjectInputStream(fis);
                    MagpieAgent agent = (MagpieAgent) ois.readObject();
                    Log.i(TAG, "INTEREST 0: " + agent.getInterests().get(0));
                    registerAgent(agent);
                } catch (FileNotFoundException ex) {
                    Log.e(TAG, "File '" + agentName + MagpieAgent.BODY_KEY + "' not found");
                } catch (StreamCorruptedException ex) {
                    Log.e(TAG, "StreamCorruptedException when trying to unserialize the agent");
                } catch (IOException ex) {
                    Log.e(TAG, "IOException with the ObjectInputStream");
                } catch (ClassNotFoundException ex) {
                    Log.e(TAG, "Class 'MagpieAgent' not found");
                }
            }
        }
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");

        SharedPreferences settings = getSharedPreferences(MAGPIE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        Set<String> agentNames = new HashSet<>();
        Iterator<Integer> iteratorIds = mEnvironment.getRegisteredAgents().keySet().iterator();

        Log.i(TAG, "Agents onDestroy(): " + mEnvironment.getRegisteredAgents().keySet().size());

        while (iteratorIds.hasNext()) {
            int id = iteratorIds.next();
            MagpieAgent agent = mEnvironment.getRegisteredAgents().get(id);
            String agentName = agent.getName();
            agentNames.add(agentName);

            ObjectOutputStream oos = null;
            try {
                // Serialize the body
                FileOutputStream fos = getApplicationContext()
                        .openFileOutput(agentName + MagpieAgent.BODY_KEY, MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(agent);
                // Serialize the mind
                // Serialize the KDTree
            } catch (FileNotFoundException ex) {
                Log.e(TAG, "File '" + agentName + MagpieAgent.BODY_KEY + "' not found");
            } catch (IOException ex) {
                Log.e(TAG, "IOException with the ObjectOutputStream");
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException ex) {
                        Log.e(TAG, "IOException when closing the ObjectOutputStream");
                    }
                }
            }
        }

        editor.putBoolean(FIRST_TIME_KEY, false);
        editor.putStringSet(AGENTS_KEY, agentNames);
        editor.commit();

        System.exit(0);
    }
	
	/**
	 * Binder object returned to the caller
	 */
	public class MagpieBinder extends Binder {
		public MagpieService getService() {
			return MagpieService.this;
		}
	}
	
	/**
	 * Factory method to make an intent to connect with this service
	 */
	public static Intent makeIntent(Context context) {
		mContext = context;
		return new Intent(context, MagpieService.class);
	}
	
	public static Context getContext() {
		return mContext;
	}
	
	/**
	 * Actions that can be performed in the Environment from an Activity
	 */
	public void registerAgent(MagpieAgent agent) {
		mEnvironment.registerAgent(agent);
	}
	
	public void registerEvent(MagpieEvent event) {
		mEnvironment.registerEvent(event);
	}
	
	public ContextEntity getContextEntity(String service) {
		return mEnvironment.getContextEntity(service);
	}
}
