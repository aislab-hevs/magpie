package ch.hevs.aislab.magpie.environment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ch.hevs.aislab.indexer.StringECKDTreeIndexer;
import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.android.MagpieService;
import ch.hevs.aislab.magpie.behavior.Behavior;
import ch.hevs.aislab.magpie.behavior.BehaviorAgentMind;
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
	public static final int NEW_EVENT = 100;
	public static final int RECREATE_AGENTS = 200;
	public static final int STORE_AGENTS = 300;

	private MagpieService mMagpieService;

	private Map<Integer, MagpieAgent> mListOfAgents = new HashMap<Integer, MagpieAgent>();
	
	private Map<String, ContextEntity> mListOfContextEntities = new HashMap<String, ContextEntity>();

	/**
	 * Queue for storing the alerts produced by the MagpieAgents
	 */
	private ConcurrentLinkedQueue<MagpieEvent> mQueueOfAlerts = new ConcurrentLinkedQueue<>();
	
	private AtomicInteger agentId = new AtomicInteger(0);


	public Environment(Looper looper, MagpieService magpieService) {
		super(looper);
		this.mMagpieService = magpieService;
	}

	@Override
	public void handleMessage(Message request) {
		int code = request.what;
		// For the moment the received messages are events
		switch (code) {
			case NEW_EVENT:
				processEvent(request);
				break;
			case RECREATE_AGENTS:
				recreateAgents(request);
				break;
			case STORE_AGENTS:
				storeAgents();
				break;
			default:
				Log.e(TAG, "Message with code " + code + " not understood");
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

	public void setBehaviorsContext(Context context, Set<String> searchedAgentNames) {
		for (MagpieAgent agent : mListOfAgents.values()) {
			if (agent.getType().equals(MagpieAgent.BEHAVIOR_TYPE)) {
				String agentName = agent.getName();
				for (String searchedAgentName : searchedAgentNames) {
					if (agentName.equals(searchedAgentName)) {
						BehaviorAgentMind agentMind = (BehaviorAgentMind) agent.getMind();
						List<Behavior> agentBehaviors = agentMind.getBehaviors();
						for (Behavior b : agentBehaviors) {
							b.setContext(context);
						}
					}
				}
			}
		}
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
				reply.what = Environment.NEW_EVENT;
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

	private void recreateAgents(Message request) {

		final Messenger replyMessenger = request.replyTo;

		Bundle bundle = request.getData();
		Set<String> agentNames = (HashSet<String>) bundle.get(MagpieActivity.AGENT_NAMES);
		for (String agentName : agentNames) {
			// Deserialize the body
			MagpieAgent agent = (MagpieAgent) deserialize(agentName + MagpieAgent.BODY_KEY);
			if (agent.getType().equals(MagpieAgent.PROLOG_TYPE)) {
				// Deserialize the mind's theory
				String theory = (String) deserialize(agentName + MagpieAgent.THEORY_KEY);
				// Deserialize the KDTree
				StringECKDTreeIndexer indexer = (StringECKDTreeIndexer) deserialize(agentName + MagpieAgent.ECKDTREE_KEY);
				// Register the mind into the body
				PrologAgentMind mind = new PrologAgentMind(theory, indexer);
				agent.setMind(mind);
			} else if (agent.getType().equals(MagpieAgent.BEHAVIOR_TYPE)) {
				BehaviorAgentMind mind = (BehaviorAgentMind) deserialize(agentName + MagpieAgent.MIND_KEY);
				for (Behavior b : mind.getBehaviors()) {
					b.setAgent(agent);
				}
				agent.setMind(mind);
			}
			registerAgent(agent);
		}

		Message reply = Message.obtain();
		reply.what = Environment.RECREATE_AGENTS;
		try {
			replyMessenger.send(reply);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void storeAgents() {
		Set<String> agentNames = new HashSet<>();
		Iterator<Integer> iteratorIds = mListOfAgents.keySet().iterator();

		while (iteratorIds.hasNext()) {
			int id = iteratorIds.next();
			MagpieAgent agent = mListOfAgents.get(id);
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
	}

	private Object deserialize(String fileName) {
		Object newInstance = null;
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = mMagpieService.getApplicationContext()
					.openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			newInstance = ois.readObject();
		} catch (FileNotFoundException ex) {
			Log.e(TAG, "File '" + fileName + "' not found in deserialization");
		} catch (StreamCorruptedException ex) {
			Log.e(TAG, "StreamCorruptedException when deserializing the object");
		} catch (IOException ex) {
			Log.e(TAG, "IOException with the ObjectInputStream");
		} catch (ClassNotFoundException ex) {
			Log.wtf(TAG, "Class Object not found");
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException ex) {
					Log.e(TAG, "IOException when closing the ObjectInputStream");
				}
			}
		}
		return newInstance;
	}

	private void serialize(String fileName, Object object) {
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = mMagpieService.getApplicationContext()
					.openFileOutput(fileName, mMagpieService.MODE_PRIVATE);
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
}
