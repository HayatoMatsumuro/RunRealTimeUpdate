package com.hm.runrealtimeupdate.logic;

public class RaceInfo {

	private String raceId;
	
	private String raceName;
	
	private String raceDate;
	
	private String raceLocation;
	
	private boolean raceUpdate;

	public String getRaceId() {
		return raceId;
	}

	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public String getRaceDate() {
		return raceDate;
	}

	public void setRaceDate(String raceDate) {
		this.raceDate = raceDate;
	}

	public String getRaceLocation() {
		return raceLocation;
	}

	public void setRaceLocation(String raceLocation) {
		this.raceLocation = raceLocation;
	}
	
	public boolean isRaceUpdate() {
		return raceUpdate;
	}

	public void setRaceUpdate(boolean raceUpdate) {
		this.raceUpdate = raceUpdate;
	}
}
