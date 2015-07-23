package ch.hevs.aislab.magpie.broker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.hevs.aislab.magpie.broker.model.MobileClient;

@Repository
public interface MobileClientRepo extends CrudRepository<MobileClient, Long>{

	MobileClient findByUsername(String username);
	
}
