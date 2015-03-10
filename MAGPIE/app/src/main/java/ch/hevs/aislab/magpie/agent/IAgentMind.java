package ch.hevs.aislab.magpie.agent;

import ch.hevs.aislab.indexer.StringECKDTreeIndexer;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IAgentMind {

    public String getTheory();
    public StringECKDTreeIndexer getECKDTree();
	public void updatePerception(MagpieEvent event);
	public MagpieEvent produceAction(long timestamp);
}
