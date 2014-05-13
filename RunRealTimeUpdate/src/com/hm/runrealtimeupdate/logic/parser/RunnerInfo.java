package com.hm.runrealtimeupdate.logic.parser;

import java.util.ArrayList;
import java.util.List;

public class RunnerInfo {
	
	/**
	 * 名前
	 */
	private String name;

	/**
	 * ゼッケン番号
	 */
	private String number;
	
	/**
	 * 部門
	 */
	private String section;
	
	/**
	 * タイムリスト
	 */
	private List<TimeList> timeList;

	/**
	 * コンストラクタ
	 */
	public RunnerInfo(){
		this.name = null;
		this.number = null;
		this.section = null;
		this.timeList = new ArrayList<TimeList>();
	}
	
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
	

	public List<TimeList> getTimeList() {
		return timeList;
	}
	
	public void addTimeList( TimeList timeList){
		this.timeList.add(timeList);
	}
	
	public class TimeList{
		
		/**
		 * 計測ポイント
		 */
		private String point;

		/**
		 * スプリット
		 */
		private String split;
		
		/**
		 * ラップ
		 */
		private String lap;
		
		/**
		 * 通過時刻
		 */
		private String currentTime;
		
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
	}
}
