package ch.hevs.aislab.magpie.agent;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.hevs.aislab.magpie.behavior.BehaviorAgentMind;
import ch.hevs.aislab.magpie.environment.IEnvironment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class MagpieAgent implements IAgentBody, Serializable {

	private final String TAG = getClass().getName();

    public static final String BODY_KEY = "_body.ser";
    public static final String MIND_KEY = "_mind.ser";
    public static final String THEORY_KEY = "_theory.ser";
    public static final String ECKDTREE_KEY = "_indexer.ser";

    public static final String PROLOG_TYPE = "prolog";
    public static final String BEHAVIOR_TYPE = "behavior";

	/** Fields set by the developer */
    private String name;
	private ArrayList<String> interests;
	private transient IAgentMind myMind;

    /** Fields initialized in the constructor */
    private ConcurrentLinkedQueue<MagpieEvent> events;

    /** Fields set by the Environment */
    private transient int id;
    private transient IEnvironment mEnv;
    private String type;

	public MagpieAgent(String name, String ... interests) {
		this.name = name;
		this.interests = new ArrayList<>(interests.length);
		for (String interest : interests) {
			this.interests.add(interest);
		}
		this.events = new ConcurrentLinkedQueue<>();
	}

	public MagpieAgent(String name, ArrayList<String> interests) {
		this.name = name;
		this.interests = interests;
	}

	public void setMind(IAgentMind mind) {
        this.myMind = mind;
	}

    public IAgentMind getMind() {
        return myMind;
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

    /**
     * Method called automatically by the Environment to set the type of agent
     */
    public void setType() {
        if (myMind instanceof PrologAgentMind) {
            type = PROLOG_TYPE;
        } else if (myMind instanceof BehaviorAgentMind) {
            type = BEHAVIOR_TYPE;
        }
    }

    public String getType() {
        return type;
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
    public void senseEvent(MagpieEvent event) {
        Log.i(TAG, "Event type '" + event.getType() + "' perceived by agent " + this.name);
        this.events.add(event);
        Log.i(TAG, "Events in the body's queue after sensing: " + events.size());
    }

    @Override
    public void activate() {
        Log.i(TAG, "Agent '" + name + "' active");
        this.doPerception();
        this.doBehaviour();
    }

	@Override
	public void doPerception() {
		for(int i=0; i<this.events.size(); i++) {
			this.myMind.updatePerception(this.events.peek());
		}
		
		Log.i(TAG, "Events in the body's queue after perception: " + events.size());
	}

	@Override
	public void doBehaviour() {
		//do something with the environment
        for (int i=0; i<this.events.size(); i++) {
            MagpieEvent alert = this.myMind.produceAction(
                    this.events.poll().getTimeStamp()
            );
            if (alert != null) {
                this.mEnv.registerAlert(alert);
            }
        }
	}

	@Override
	public void setEnvironment(IEnvironment env) {
		this.mEnv = env;
	}
}
