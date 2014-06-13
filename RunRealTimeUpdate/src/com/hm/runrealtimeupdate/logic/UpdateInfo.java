package com.hm.runrealtimeupdate.logic;

public class UpdateInfo {
	
	private String name;
	
	private String number;
	
	private String section;
	
	private String point;
	
	private String split;
	
	private String lap;
	
	private String currentTime;

	private boolean recentFlg;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getLap() {
		return lap;
	}

	public void setLap(String lap) {
		this.lap = lap;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	
	public boolean isRecentFlg() {
		return recentFlg;
	}

	public void setRecentFlg(boolean recentFlg) {
		this.recentFlg = recentFlg;
	}
	
	public long getSplitLong(){
		
		try {
			long time = getLongTime(split);
			return time;
		} catch (UpdateInfoException e) {
			
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 秒単位の時間を取得する
	 * @param timeStr　HH:MM:SS 形式
	 * @return 時間
	 * @throws PassPointException 時間取得失敗
	 */
	private long getLongTime( String timeStr ) throws UpdateInfoException{
		
		try{
			String split[] = timeStr.split(":");
			int h = Integer.parseInt(split[0]);
			int m = Integer.parseInt(split[1]);
			int s = Integer.parseInt(split[2]);
			
			return h*3600 + m*60 + s;
		}catch( Exception e){
			
			throw new UpdateInfoException();
		}
		
	}
	
	@SuppressWarnings("serial")
	private class UpdateInfoException extends Exception{
	}
}
