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
import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;
import ch.hevs.aislab.magpie.event.RuleSetEvent;
import ch.hevs.aislab.magpie.event.UpdateMindModelEvent;
import ch.hevs.aislab.magpie.support.Rule;

public class PrologAgentMind implements IAgentMind {

	private final String  TAG = getClass().getName();

    private static final String EC_PREDICATES = "ec_predicates";
    private static final String EC_FOR_INDEXING4 = "ec_for_indexing4";
	
	private Prolog prolog;
	private HashMap<Long,Rule> rulesMap = new HashMap<Long, Rule>();
	
	// Used for debugging (print the prolog output in Android's LogCat)
	private String prologOutput;

    /**
     * Create a mind that works with EC and the indexer
     *
     * @param context
     */
    public PrologAgentMind(Context context) {

        prolog = new Prolog();
        try {
            prolog.addTheory(parseTheory(context, EC_PREDICATES));
            prolog.addTheory(parseTheory(context, EC_FOR_INDEXING4));
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a resource file into a Theory object
     *
     * @param context
     * @param file
     * @return
     */
    private Theory parseTheory(Context context, String file) {
        int resourceId = context.getResources().getIdentifier(
                file,
                "raw",
                context.getPackageName()
        );
        InputStream is = context.getResources().openRawResource(resourceId);

        try {
            return new Theory(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a mind with a custom Prolog theory
     *
     * @param theory
     */
    public PrologAgentMind(String theory){
        Log.i(TAG, "Theory loaded:\n" + theory);

        prologOutput="";
        try {
            prolog = new Prolog();
            prolog.setTheory(new Theory(theory));

			/* Listener for testing purposes */
            prolog.addOutputListener(new OutputListener() {
                @Override
                public void onOutput(OutputEvent e) {
                    prologOutput += e.getMsg();
                }
            });
        } catch (InvalidTheoryException e) {
            e.printStackTrace();
        }
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
				//Log.i(TAG, "Tuple received: " + ((LogicTupleEvent)event).toTuple());
				prolog.solve("perceive("+((LogicTupleEvent)event).toTuple()+").");
				Log.i(TAG, prologOutput);
				prologOutput="";
			} catch (MalformedGoalException e) {
				e.printStackTrace();
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
					Map.Entry<Long, Rule> pair = (Map.Entry<Long, Rule>) it0.next();
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
	public MagpieEvent produceAction() {
		Log.i(TAG, "produceAction()");
		
		LogicTupleEvent action = null;
		
		try {
			SolveInfo sol = prolog.solve("act(A).");
			if (sol.isSuccess()) {
				Log.i(TAG, sol.getSolution().toString());
				action = new LogicTupleEvent(sol.getSolution());
			} 
		} catch (MalformedGoalException e) {
			Log.e(TAG, "MalformedGoalException");
		} catch (NoSolutionException e) {
			Log.e(TAG, "NoSolutionException");
		}
		
		return action;
	}

	@Override
	public void update() {		
		try {
			SolveInfo info = prolog.solve("update.");
			Log.i(TAG, "The update is " + info.isSuccess());
			Log.i(TAG, "Theory after update:\n" + prolog.getTheory().toString() );
		} catch (MalformedGoalException e) {
			e.printStackTrace();
		}
	}
}
