package ch.hevs.aislab.magpie.broker.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.hevs.aislab.magpie.broker.model.GlucoseAlert;

@Repository
public interface GlucoseAlertRepo extends CrudRepository<GlucoseAlert, Long>{

	Collection<GlucoseAlert> findByPublisherId(long publisherId);
}
