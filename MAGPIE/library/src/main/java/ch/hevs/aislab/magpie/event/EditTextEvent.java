package ch.hevs.aislab.magpie.event;

public class EditTextEvent extends MagpieEvent {

	int value;
	
	public EditTextEvent(String type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
