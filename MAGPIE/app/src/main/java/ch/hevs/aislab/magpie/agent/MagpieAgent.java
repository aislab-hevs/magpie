package ch.hevs.aislab.magpie.agent;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.hevs.aislab.magpie.environment.IEnvironment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

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
		for (String interest : interests) {
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
    public void activate() {
        Log.i(TAG, "Agent " + name + " active");
        this.doPerception();
        this.doBehaviour();
    }

	@Override
	public void doPerception() {
		for(int i=0; i<this.events.size(); i++) {
			this.mind.updatePerception(this.events.peek());
		}
		
		Log.i(TAG, "Events in the agent queue after perception:" + events.size());
	}

	@Override
	public void doBehaviour() {
		//do something with the environment
        for (int i=0; i<this.events.size(); i++) {
            MagpieEvent alert = this.mind.produceAction(
                    this.events.poll().getTimeStamp()
            );
            if (alert != null) {
                this.env.registerAlert(alert);
            }
        }
	}

	@Override
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}
}
