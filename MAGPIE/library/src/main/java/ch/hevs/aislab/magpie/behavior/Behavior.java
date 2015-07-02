package ch.hevs.aislab.magpie.behavior;


import android.content.Context;

import java.io.Serializable;

import ch.hevs.aislab.magpie.agent.MagpieAgent;

public abstract class Behavior implements IBehavior, Serializable, Comparable<Behavior> {

    private String myName;

    private transient MagpieAgent myAgent;

    private transient Context myContext;

    private int priority;

    public Behavior() {
        myName = getClass().getName();

        int dotIndex = myName.lastIndexOf('.');
        int dollarIndex = myName.lastIndexOf('$');
        int lastIndex = (dotIndex > dollarIndex ? dotIndex : dollarIndex);

        if (lastIndex != -1) {
            myName = myName.substring(lastIndex + 1);
        }
    }

    public Behavior(MagpieAgent a) {
        this();
        myAgent = a;
    }

    public final String getBehaviorName() {
        return myName;
    }

    public void setAgent(MagpieAgent a) {
        myAgent = a;
    }

    public MagpieAgent getAgent() {
        return myAgent;
    }

    public void setContext(Context context) {
        myContext = context;
    }

    public Context getContext() {
        return myContext;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Behavior other) {
        final int SMALLER = -1;
        final int EQUAL = 0;
        final int BIGGER = 1;

        if (this.priority < other.priority) {
            return SMALLER;
        } else if (this.priority > other.priority) {
            return BIGGER;
        }

        return EQUAL;
    }

    /**
     * Two behaviors are considered equals if they have the same name and the same priority
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Behavior) {
            Behavior other = (Behavior) obj;
            return (this.priority == other.priority) &&
                    (this.myName.equals(other.myName));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
