package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * 部門選手情報
 * @author Hayato Matsumuro
 *
 */
public class SectionRunnerInfo
{
	/**
	 * 部門
	 */
	public String section;

	/**
	 * 選手情報リスト
	 */
	public List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();
}
