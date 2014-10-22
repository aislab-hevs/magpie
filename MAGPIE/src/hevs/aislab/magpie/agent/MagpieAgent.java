package hevs.aislab.magpie.agent;

import hevs.aislab.magpie.environment.IEnvironment;
import hevs.aislab.magpie.event.MagpieEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

public class MagpieAgent implements IAgentBody {

	private final String TAG = getClass().getName();

	private String name;
	private int id;
	private ArrayList<String> interests;
	private IAgentMind mind;
	private ConcurrentLinkedQueue<MagpieEvent> events;
	private IEnvironment env;

	public MagpieAgent(String name, String ... interests) {
		this.name = name;
		this.interests = new ArrayList<String>(interests.length);
		for (String interest: interests) {
			this.interests.add(interest);
		}
		this.events = new ConcurrentLinkedQueue<MagpieEvent>();
		// Assign the mind too
		//this.mind = new PrologAgentMind();
	}

	public MagpieAgent(String name, ArrayList<String> interests) {
		this.name = name;
		this.interests = interests;
	}

	public void senseEvent(MagpieEvent event) {
		Log.i(TAG, "Event type '" + event.getType() + "' perceived by agent " + this.name);
		/*
		Toast.makeText(
				MagpieService.getContext(), 
				"Event type '" + event.getType() + "' perceived by agent " + this.name, 
				Toast.LENGTH_LONG)
				.show();
		*/
		this.events.add(event);
		Log.i(TAG, "Events in the agent queue after sensing: " + events.size());
	}

	public void setMind(IAgentMind mind){
		this.mind = mind;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getInterests() {
		return interests;
	}
	
	public void setInterests(String...interests) {
		this.interests.clear();
		for (String interest : interests) {
			this.interests.add(interest);
		}
	}

	@Override
	public void doPerception() {
		for(int i=0;i<this.events.size(); i++) {
			this.mind.updatePerception(this.events.poll());
		}
		
		Log.i(TAG, "Events in the agent queue after perception:" + events.size());
	}

	@Override
	public void doBehaviour() {
		//do something with the environment
		MagpieEvent alert = this.mind.produceAction();	
		if (alert != null) {
			this.env.registerAlert(alert);
		}			
	}

	public void doAction() {
		//it may be that the mind needs to update.
		this.mind.update();
	}

	public void activate() {
		Log.i(TAG, "Agent " + name + " active");
		this.doPerception();
		this.doBehaviour();
		this.doAction();
	}

	@Override
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}
}
