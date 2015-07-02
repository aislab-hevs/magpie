package ch.hevs.aislab.magpie.environment;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class Environment implements IEnvironment {

	/**
	 * Used for debugging
	 */
	private final String TAG = getClass().getName();

	private static volatile Environment instance;

	private Map<Integer, MagpieAgent> mListOfAgents = new HashMap<Integer, MagpieAgent>();
	
	private Map<String, ContextEntity> mListOfContextEntities = new HashMap<String, ContextEntity>();

	/**
	 * Queue for storing the events produced by the ContextEntities 
	 */
	private ConcurrentLinkedQueue<MagpieEvent> mQueueOfEvents = new ConcurrentLinkedQueue<>();
	
	/**
	 * Queue for storing the alerts produced by the MagpieAgents
	 */
	private ConcurrentLinkedQueue<MagpieEvent> mQueueOfAlerts = new ConcurrentLinkedQueue<>();
	
	private AtomicInteger agentId = new AtomicInteger(0);

    private CountDownLatch alertLatch = null;

	/**
	 * Class running the Environment life-cycle in a thread
	 */
	private EnvironmentThread mEnvThread;

	private Environment() {
		//mListOfProvidedServices.put(Services.GPS_LOCATION, false);
		
		Log.i(TAG, "Starting the Environment's thread");
		mEnvThread = new EnvironmentThread();
		new Thread(mEnvThread).start();
	}

	public static Environment getInstance() {
		if (instance == null) {
			synchronized (Environment.class) {
				instance = new Environment();
			}
		}
        //Start the Environment life-cycle if it is not started
		/*
		if (instance.mEnvThread.isCancelled()) {
			instance.mEnvThread.setCancelled(false);
			new Thread(instance.mEnvThread).start();
		}
		*/
		return instance;
	}
	/**
	 * Used to stop the Environment life-cycle 
	 */
	/*
	public void shutDown() {
		mEnvThread.cancel();
	}
	*/

	@Override
	public void registerAgent(MagpieAgent agent) {

        agent.setType();
		agent.setId(agentId.incrementAndGet());
		agent.setEnvironment(this);
		mListOfAgents.put(agent.getId(), agent);
		
		// Register the interests of the new agent joining the environment
		ServiceActivator.registerInterests(agent);
		
		Log.i(TAG, "Agent '" + agent.getName() + "' with ID " + agent.getId() + " registered\n"
				+ "Total num. of agents: " + mListOfAgents.size());
	}

	@Override
	public void unregisterAgent(MagpieAgent agent) {
		mListOfAgents.remove(agent.getId());
	}

	@Override
	public void registerContextEntity(ContextEntity contextEntity) {
		mListOfContextEntities.put(contextEntity.getService(), contextEntity);
		Log.i(TAG, "New ContextEntity registered. Total: " + mListOfContextEntities.size());

	}

	@Override
	public void unregisterContextEntity(ContextEntity contextEntity) {
		
	}

	@Override
	public Map<Integer, MagpieAgent> getRegisteredAgents() {
		return mListOfAgents;
	}

	@Override
	public Map<String, ContextEntity> getRegisteredContextEntities() {
		return mListOfContextEntities;
	}

	@Override
	public ContextEntity getContextEntity(String service) {
		if (service.equals(Services.REST_CLIENT)) {
			return mListOfContextEntities.get(service);
		} else {
			return null;
		}
	}

	public void registeredAgents() {
		Log.i(TAG, "Number of registered agents: " + mListOfAgents.size());
	}

	/**
	 * Register a new event in the corresponding Environment queue
	 */
	public MagpieEvent registerEvent(MagpieEvent event) {
		mQueueOfEvents.add(event);

        //Wait until the Thread finishes to compute the alert
        alertLatch = new CountDownLatch(1);
        mEnvThread.newEventReceived();
        try {
            alertLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!mQueueOfAlerts.isEmpty()) {
            return mQueueOfAlerts.poll();
        } else {
            return null;
        }
    }
	
	/**
	 * Register a new alert in the corresponding Environment queue
	 */
	public void registerAlert(MagpieEvent event) {
		mQueueOfAlerts.add(event);
		Log.i(TAG, "Number of alerts produced by the Agents: " + mQueueOfAlerts.size());
	}

    // Methods to change the state of the services

	private class EnvironmentThread implements Runnable {

		private Lock mLock = new ReentrantLock();
		
		private Condition mCondition = mLock.newCondition();
		
		private EnvironmentThread() {
			//mCondition = mLock.newCondition();
		}
		
		/**
		 * This method runs the Environment life-cycle forever which may
		 * may consume a lot of battery. However this doesn't happen as 
		 * it applies the guarded suspension pattern, where the condition
		 * to wake up the thread is to receive a new event in the
		 * environment.
		 */
		@Override
		public void run() {
			while (true) {
				mLock.lock();
				while (Environment.this.mQueueOfEvents.isEmpty()) {
					Log.i(TAG, "Waiting for new Events");
					mCondition.awaitUninterruptibly();
				}
				mLock.unlock();
				MagpieEvent event = Environment.this.mQueueOfEvents.poll();
				for (MagpieAgent agent : mListOfAgents.values()) {
					/* Send the event only to the interested agents */
					if (agent.getInterests().contains(event.getType())) {
						Log.i(TAG, "Agent with Id " + agent.getId() + " and name " + agent.getName() + " activated");
						agent.senseEvent(event);
						agent.activate();
					}
				}

				/* Send the alerts produced by the agents */
				if (!mQueueOfAlerts.isEmpty()) {
                    // Just print the alert in the Logcat
                    LogicTupleEvent alert = (LogicTupleEvent) mQueueOfAlerts.peek();
                    Log.i(TAG, alert.toTuple());
				}

                alertLatch.countDown();
			}
		}
		
		private void newEventReceived() {
			mLock.lock();
			Log.i(TAG, "New Event received");
			mCondition.signal();
			mLock.unlock();
		}	
	}
}
