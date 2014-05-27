package com.hm.runrealtimeupdate.logic.dbaccess;

public class DataBaseRaceInfo {

	private String raceId;
	
	private String raceDate;
	
	private String raceLocation;
	
	private String raceName;
	
	private String updateFlg;

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

	public String getUpdateFlg() {
		return updateFlg;
	}

	public void setUpdateFlg(String updateFlg) {
		this.updateFlg = updateFlg;
	}
}
