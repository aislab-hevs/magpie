package ch.hevs.aislab.magpie.agent;


import ch.hevs.aislab.indexer.StringECKDTreeIndexer;

public interface IPrologAgentMind extends IAgentMind {

    String getTheory();
    StringECKDTreeIndexer getECKDTree();
}
