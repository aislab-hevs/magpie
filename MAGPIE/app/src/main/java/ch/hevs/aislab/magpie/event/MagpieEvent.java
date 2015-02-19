package ch.hevs.aislab.magpie.event;

public class MagpieEvent {

	private long timeStamp;
	protected String type;
	 
	
	protected MagpieEvent() {
		this.timeStamp = System.currentTimeMillis();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

	public String getType() { 
		return type;
	}
}
