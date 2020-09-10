package activeSegmentation;
//Enumeration over cell event outcome
public enum EventType {

	EVENT_TRUE (1),  
	EVENT_FALSE(2);
	
	private final int eventType;

	EventType(int EventType) {
		this.eventType = EventType;
	}

	public int getEventType() {
		return this.eventType;
	}
}