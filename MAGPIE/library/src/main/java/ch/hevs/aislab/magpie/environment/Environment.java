package ch.hevs.aislab.magpie.environment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class Environment extends Handler implements IEnvironment {

	/**
	 * Used for debugging
	 */
	private final String TAG = getClass().getName();

	/**
	 * Codes for the Messages that the Environment can process
	 */
	public static final int EVENT = 100;

	private Map<Integer, MagpieAgent> mListOfAgents = new HashMap<Integer, MagpieAgent>();
	
	private Map<String, ContextEntity> mListOfContextEntities = new HashMap<String, ContextEntity>();

	/**
	 * Queue for storing the alerts produced by the MagpieAgents
	 */
	private ConcurrentLinkedQueue<MagpieEvent> mQueueOfAlerts = new ConcurrentLinkedQueue<>();
	
	private AtomicInteger agentId = new AtomicInteger(0);


	public Environment(Looper looper) {
		super(looper);
	}

	@Override
	public void handleMessage(Message request) {
		int code = request.what;
		// For the moment the received messages are events
		switch (code) {
			case Environment.EVENT:
				processEvent(request);
				break;
			default:
				Log.i(TAG, "Message with code " + code + " not understood");
				break;
		}
	}

	@Override
	public void registerAgent(MagpieAgent agent) {

        agent.setType();
		agent.setId(agentId.incrementAndGet());
		agent.setEnvironment(this);
		mListOfAgents.put(agent.getId(), agent);
		
		// Register the interests of the new agent joining the environment
		ServiceActivator.registerInterests(this, agent);
		
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

		if (Looper.myLooper() == Looper.getMainLooper()) {
			Log.i("RequestHandler", "In the Environment getRegisteredAgents() we are in UI Thread");
		} else {
			Log.i("RequestHandler", "This is not the UI Thread in the Environment getRegisteredAgents()");
		}

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

	/**
	 * Register a new alert in the corresponding Environment queue
	 */
	public void registerAlert(MagpieEvent event) {
		mQueueOfAlerts.add(event);
		Log.i(TAG, "Number of alerts produced by the Agents: " + mQueueOfAlerts.size());
	}

	private void processEvent(Message request) {
		/**
		 * Get the Event that is inside the message, and the Messenger to reply in the case that an
		 * alert is produced
		 */
		Bundle bundleEvent = request.getData();
		LogicTupleEvent event = bundleEvent.getParcelable(MagpieActivity.MAGPIE_EVENT);

		final Messenger replyMessenger = request.replyTo;

		for (MagpieAgent agent : mListOfAgents.values()) {
			// Send the event only to the interested agents
			if (agent.getInterests().contains(event.getType())) {
				Log.i(TAG, "Agent with Id " + agent.getId() + " and name " + agent.getName() + " activated");
				agent.senseEvent(event);
				agent.activate();
			}
		}

		if (!mQueueOfAlerts.isEmpty()) {
			Iterator<MagpieEvent> alertsIt = mQueueOfAlerts.iterator();
			while (alertsIt.hasNext()) {
				// Take the alert
				LogicTupleEvent alert = (LogicTupleEvent) alertsIt.next();

				// Prepare the message containg the alert to be sent to the MagpieActivity
				Message reply = Message.obtain();
				Bundle alertBundle = new Bundle();
				alertBundle.putParcelable(MagpieActivity.MAGPIE_EVENT, alert);
				reply.setData(alertBundle);

				// Send back the alert
				try {
					replyMessenger.send(reply);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
