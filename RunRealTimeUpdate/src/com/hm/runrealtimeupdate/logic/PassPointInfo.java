package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

public class PassPointInfo {

	private int passPointNo;

	private String point;
	
	private List<PassPointRunnerInfo> passPointRunnerInfoList = new ArrayList<PassPointRunnerInfo>();
	
	
	public int getPassPointNo() {
		return passPointNo;
	}

	public void setPassPointNo(int passPointNo) {
		this.passPointNo = passPointNo;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}
	
	public List<PassPointRunnerInfo> getPassPointRunnerInfoList() {
		return passPointRunnerInfoList;
	}

	public void setPassPointRunnerInfoList(List<PassPointRunnerInfo> passPointRunnerInfoList) {
		this.passPointRunnerInfoList = passPointRunnerInfoList;
	}



	public class PassPointRunnerInfo{
		
		private String name;

		private String number;
		
		private String split;
		
		private String lap;
		
		private String currentTime;
		
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
