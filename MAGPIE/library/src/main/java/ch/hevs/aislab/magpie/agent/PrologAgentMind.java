package ch.hevs.aislab.magpie.agent;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import ch.hevs.aislab.indexer.StringECKDTreeIndexer;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;
import ch.hevs.aislab.magpie.event.RuleSetEvent;
import ch.hevs.aislab.magpie.event.UpdateMindModelEvent;
import ch.hevs.aislab.magpie.support.Rule;
import hevs.aislab.magpie.R;

public class PrologAgentMind implements IPrologAgentMind {

	private final String  TAG = getClass().getName();

	private Prolog prolog;
    private StringECKDTreeIndexer index;

	private HashMap<Long,Rule> rulesMap = new HashMap<>();

    /**
     * Create a mind that works with EC and the indexer
     *
     * @param context
     */
    public PrologAgentMind(Context context, int resourceId) {

        // Assign the indexer and a new prolog engine
        index = new StringECKDTreeIndexer();
        prolog = new Prolog();

        // Register the indexer in tuProlog
        JavaLibrary lib = (JavaLibrary) prolog.getLibrary("alice.tuprolog.lib.JavaLibrary");
        try {
            lib.register(new Struct("indexer"), index);
        } catch (InvalidObjectIdException ex) {
            ex.printStackTrace();
        }

        // Add to the prolog engine the theories from the files
        try {
            prolog.addTheory(parseTheory(context, R.raw.ec_predicates));
            prolog.addTheory(parseTheory(context, R.raw.ec_for_indexing4));
            prolog.addTheory(parseTheory(context, R.raw.agent_cycle));
            prolog.addTheory(parseTheory(context, resourceId));
        } catch (InvalidTheoryException ex) {
            Log.e(TAG, "Prolog theory is not valid: " + ex.getMessage());
        }

        // Optional: Add the output listener for debugging
        startPrologOutput();
    }

    /**
     * Constructor used in MagpieService to recreate the mind
     */
    public PrologAgentMind(String theory, StringECKDTreeIndexer indexer) {

        index = indexer;
        prolog = new Prolog();

        JavaLibrary lib = (JavaLibrary) prolog.getLibrary("alice.tuprolog.lib.JavaLibrary");
        try {
            lib.register(new Struct("indexer"), indexer);
        } catch (InvalidObjectIdException ex) {
            ex.printStackTrace();
        }

        try {
            prolog.addTheory(new Theory(theory));
        } catch (InvalidTheoryException ex) {
            Log.e(TAG, "Prolog theory is not valid: " + ex.getMessage());
        }

        startPrologOutput();
    }

    /**
     * Create a mind with a custom Prolog theory
     *
     * @param theory
     */
    public PrologAgentMind(String theory){
        Log.i(TAG, "Theory loaded:\n" + theory);

        try {
            prolog = new Prolog();
            prolog.addTheory(new Theory(theory));
        } catch (InvalidTheoryException ex) {
            Log.e(TAG, "Prolog theory is not valid: " + ex.getMessage());
        }

        startPrologOutput();
    }

    /**
     * Used to print the engine's theory in the logcat
     * @param str
     */
    private static void longInfo(String str) {
        if(str.length() > 4000) {
            Log.i("PrologAgentMind", str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else {
            Log.i("PrologAgentMind", str);
        }
    }

    /**
     * Converts a resource file into a Theory object
     *
     * @param context
     * @param resourceId
     * @return
     */
    private Theory parseTheory(Context context, int resourceId) {

        InputStream is = context.getResources().openRawResource(resourceId);

        try {
            return new Theory(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Adds a listener for testing purposes
     */
    private void startPrologOutput() {

        prolog.addOutputListener(new OutputListener() {
            @Override
            public void onOutput(OutputEvent ev) {
                Log.i("Prolog Output: ", ev.getMsg());
            }
        });
    }

    @Override
    public String getTheory() {
        return prolog.getTheory().toString();
    }

    @Override
    public StringECKDTreeIndexer getECKDTree() {
        return index;
    }

	/**
	 * This perception in the Prolog agent is going to be
	 * processed only in the case it is a LogicTuple. Events
	 * that are not LogicTuples cannot be handled by this
	 * particular mind. We suggest to use the SubsumptionMind
	 * to work with events that are not logic tuples.
	 */
	@Override
	public void updatePerception(MagpieEvent event) {		
		if(event instanceof LogicTupleEvent) {
			try {
                LogicTupleEvent ev = (LogicTupleEvent) event;
				SolveInfo infoPerceive = prolog.solve("perceive(" + ev.toTuple() + "," + ev.getTimeStamp() + ").");

                // Print the perception and its solution
                Log.i(TAG, "perceive(" + ev.toTuple() + "," + ev.getTimeStamp() + ").");
                Log.i(TAG, "perceive solution: " + infoPerceive.toString());

			} catch (MalformedGoalException ex) {
				Log.e(TAG, "MalformedGoalException: " + ex.getMessage());
			}
		} if (event instanceof RuleSetEvent) {
			Log.i(TAG, "New RuleSetEvent received");
			
			Collection<Rule> rules = ((RuleSetEvent) event).getRules();
			
			if (rulesMap.isEmpty()) { // When there are no rules from the server 
				Iterator<Rule> it = rules.iterator();
				while (it.hasNext()) {
					Rule r = it.next();
					Log.i(TAG, "Prolog rule received by the agent: " + r.getPrologRule());
					try {
						prolog.addTheory(new Theory(r.getPrologRule()));
					} catch (InvalidTheoryException e) {
						Log.e(TAG, "'" + r.getPrologRule() + "' is not a valid Prolog rule");
					}
					rulesMap.put(r.getId(), r);			
				}
			} else { // Subsequent times
				Iterator<Entry<Long,Rule>> it0 = rulesMap.entrySet().iterator();
				while (it0.hasNext()) {
					Map.Entry<Long, Rule> pair = it0.next();
					String prologRule = pair.getValue().getPrologRule();
					prologRule = prologRule.substring(0, prologRule.length()-1);
					try {
						prolog.solve("retract(" + prologRule + ").");
					} catch (MalformedGoalException e) {
						e.printStackTrace();
					}
				}
				rulesMap.clear();
				Iterator<Rule> it1 = rules.iterator();
				while (it1.hasNext()) {
					Rule r = it1.next();
					try {
						prolog.addTheory(new Theory(r.getPrologRule()));
					} catch (InvalidTheoryException e) {
						Log.e(TAG, "'" + r.getPrologRule() + "' is not a valid Prolog rule");
					}
					rulesMap.put(r.getId(), r);
				}
			}
			
		} if (event instanceof UpdateMindModelEvent) {
			//stub, here you would change the behaviour of the agent
			//with the new theory			
		}
	}

    @Override
	public MagpieEvent produceAction(long timeStamp) {
		
		LogicTupleEvent action = null;
		timeStamp = timeStamp + 1;

		try {
			SolveInfo infoAct = prolog.solve("act(A," + timeStamp + ").");

            //Print the act and its solution
            Log.i(TAG, "act(A," + timeStamp + ").");
            Log.i(TAG, "act solution: " + infoAct.toString());

			if (infoAct.isSuccess()) {
				action = new LogicTupleEvent(infoAct.getSolution());
			} 
		} catch (MalformedGoalException ex) {
			Log.e(TAG, "MalformedGoalException: " + ex.getMessage());
		} catch (NoSolutionException ex) {
			Log.e(TAG, "NoSolutionException: " + ex.getMessage());
		}

		return action;
	}
}