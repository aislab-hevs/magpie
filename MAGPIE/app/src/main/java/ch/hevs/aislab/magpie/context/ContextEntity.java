package ch.hevs.aislab.magpie.context;

/**
 * A context entity is an abstraction that encapsulates a source of 
 * information from the external world or from the phone itself, i.e.
 * a Bluetooth sensor. A context entity provides a particular service
 * in form of MagpieEvents that are registered in the Environment.
 * 
 * @author abrugues
 *
 */
public abstract class ContextEntity {

	/**
	 * Used for debugging
	 */
	protected static final String TAG = "Magpie-ContextEntity"; 
	
	protected final String service;
	
	protected ContextEntity(String service) {
		this.service = service;
	}
	
	public String getService() {
		return service;
	}
}
