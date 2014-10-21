package hevs.aislab.magpie.context;

public class ContextEntity {

protected static final String TAG = "Magpie-ContextEntity"; 
	
	protected final String service;
	
	protected ContextEntity(String service) {
		this.service = service;
	}
	
	public String getService() {
		return service;
	}
}
