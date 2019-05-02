package src;

import java.time.LocalTime;

public class Slot {
	private LocalTime startTime;
	private LocalTime endTime;
	private String body;
	
	
	public Slot(LocalTime startTime, LocalTime endTime, String body) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.body = body;
	}
	
	public LocalTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	
	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
