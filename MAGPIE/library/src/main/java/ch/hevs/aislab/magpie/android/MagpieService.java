package ch.hevs.aislab.magpie.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.behavior.BehaviorAgentMind;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.environment.Environment;

public class MagpieService extends Service {

    /** Used for debugging */
    private final String TAG = getClass().getName();

    /** Shared Preferences store the names of the MagpieActivities that bounded to this Service */
    static final String MAGPIE_PREFS = "magpie_prefs";
    static final String MASTER_KEY = "MagpieActivitiesInApplication";

    /** Objects returned to the MagpieActivity for communicating with the Service and the Environment */
    private final IBinder mBinder = new MagpieBinder();
    private Messenger requestMessenger;

    /** Looper associated with the HandlerThread */
    private volatile Looper mServiceLooper;

    private volatile Environment mEnvironment;

    /** Last binding activity */
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        // Create and start a background HandlerThread since by default a Service
        // runs in the UI Thread, which we don't want to block
        HandlerThread thread = new HandlerThread("EnvironmentService");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mEnvironment = new Environment(mServiceLooper, this);

        requestMessenger = new Messenger(mEnvironment);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");

        String action = intent.getAction();
        if (action.equals(MagpieActivity.ACTION_ONE_WAY_COMM)) {
            return mBinder;
        } else if (action.equals(MagpieActivity.ACTION_TWO_WAY_COMM)) {
            return requestMessenger.getBinder();
        } else {
            Log.e(TAG, "MagpieService received an intent without an action");
            return null;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MagpieService - onDestroy()");
        
        Set<String> agentNames = new HashSet<>();
        Iterator<Integer> iteratorIds = mEnvironment.getRegisteredAgents().keySet().iterator();

        Log.i(TAG, "Agents onDestroy(): " + mEnvironment.getRegisteredAgents().keySet().size());

        while (iteratorIds.hasNext()) {
            int id = iteratorIds.next();
            MagpieAgent agent = mEnvironment.getRegisteredAgents().get(id);
            String agentName = agent.getName();

            if (agent.getMind() instanceof PrologAgentMind) {

                PrologAgentMind mind = (PrologAgentMind) agent.getMind();
                // Serialize the body
                serialize(agentName + MagpieAgent.BODY_KEY, agent);
                Log.i(TAG, "Agent body serialized OK!");
                // Serialize the mind's theory
                serialize(agentName + MagpieAgent.THEORY_KEY, mind.getTheory());
                Log.i(TAG, "Agent theory serialized OK!");
                // Serialize the KDTree
                serialize(agentName + MagpieAgent.ECKDTREE_KEY, mind.getECKDTree());
                Log.i(TAG, "KD Tree serialized OK!");
                agentNames.add(agentName);

            } else if (agent.getMind() instanceof BehaviorAgentMind) {

                Log.i(TAG, "Agent '" + agentName + "' has a BehaviorAgentMind");
                BehaviorAgentMind mind = (BehaviorAgentMind) agent.getMind();
                serialize(agentName + MagpieAgent.BODY_KEY, agent);
                serialize(agentName + MagpieAgent.MIND_KEY, mind);
                agentNames.add(agentName);

            }
        }

        mEnvironment = null;
    }

    private void serialize(String fileName, Object object) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = getApplicationContext()
                    .openFileOutput(fileName, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "File '" + fileName + "' not found in serialization");
        } catch (IOException ex) {
            Log.e(TAG, "IOException with the ObjectOutputStream");
            ex.printStackTrace();
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
    public void registerAgent(MagpieAgent agent, String activityName) {
        mEnvironment.registerAgent(agent);

        SharedPreferences settings = getSharedPreferences(MAGPIE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        Set<String> magpieActivities = settings.getStringSet(MASTER_KEY, new HashSet<String>());
        magpieActivities.add(activityName);
        editor.putStringSet(MASTER_KEY, magpieActivities);

        Set<String> agentNamesFromActivity = settings.getStringSet(activityName, new HashSet<String>());
        agentNamesFromActivity.add(agent.getName());
        editor.putStringSet(activityName, agentNamesFromActivity);

        editor.apply();

    }

    public void setBehaviorsContext(Context context, Set<String> agentNames) {
        mEnvironment.setBehaviorsContext(context, agentNames);
    }

    public void registerAgent(MagpieAgent agent) {
        mEnvironment.registerAgent(agent);
    }

    public ContextEntity getContextEntity(String service) {
        return mEnvironment.getContextEntity(service);
    }
}
