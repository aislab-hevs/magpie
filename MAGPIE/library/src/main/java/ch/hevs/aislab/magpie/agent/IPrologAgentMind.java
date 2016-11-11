package ch.hevs.aislab.magpie.agent;


import ch.hevs.aislab.indexer.ECKDTreeIndexer;

public interface IPrologAgentMind extends IAgentMind {

    String getTheory();
    ECKDTreeIndexer getECKDTree();
}
