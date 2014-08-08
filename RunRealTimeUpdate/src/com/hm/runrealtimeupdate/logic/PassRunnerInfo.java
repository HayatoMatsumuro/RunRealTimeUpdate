package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

public class PassRunnerInfo {
	
	private String section;
	
	private List<PassPointInfo> passPointInfo = new ArrayList<PassPointInfo>();
	
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public List<PassPointInfo> getPassPointInfo() {
		return passPointInfo;
	}

	public void setPassPointInfo(List<PassPointInfo> passPointInfo) {
		this.passPointInfo = passPointInfo;
	}
	
	public class PassPointInfo{
		
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
			
			public long getSplitLong(){
				
				try {
					long time = getLongTime(split);
					return time;
				} catch (PassPointException e) {
					
					e.printStackTrace();
					return 0;
				}
			}
			
			public boolean isRecentFlg() {
				return recentFlg;
			}

			public void setRecentFlg(boolean recentFlg) {
				this.recentFlg = recentFlg;
			}
			
			/**
			 * 秒単位の時間を取得する
			 * @param timeStr　HH:MM:SS 形式
			 * @return 時間
			 * @throws PassPointException 時間取得失敗
			 */
			private long getLongTime( String timeStr ) throws PassPointException{
				
				try{
					String split[] = timeStr.split(":");
					int h = Integer.parseInt(split[0]);
					int m = Integer.parseInt(split[1]);
					int s = Integer.parseInt(split[2]);
					
					return h*3600 + m*60 + s;
				}catch( Exception e){
					
					throw new PassPointException();
				}	
			}
		}
		
		@SuppressWarnings("serial")
		private class PassPointException extends Exception{
			
		}
	}
}
