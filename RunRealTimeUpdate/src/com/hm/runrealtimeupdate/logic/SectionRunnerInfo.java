package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

public class SectionRunnerInfo {
	
	private String section;
	
	private List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public List<RunnerInfo> getRunnerInfoList() {
		return runnerInfoList;
	}

	public void setRunnerInfoList(List<RunnerInfo> runnerInfoList) {
		this.runnerInfoList = runnerInfoList;
	}
	
	
}
