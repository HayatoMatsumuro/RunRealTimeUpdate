package com.hm.runrealtimeupdate.logic.parser;

import java.util.List;

public interface ParserUpdate
{
	/**
	 * アップデートのサイトから大会情報を取得する
	 * @param url アップデートサイトのURL
	 * @param raceId 大会ID
	 * @return 大会情報
	 * @throws ParserException
	 */
	public ParserRaceInfo getRaceInfo( String url, String raceId ) throws ParserException;

	/**
	 * アップデートのサイトからランナー情報を取得する
	 * @param url アップデートサイトURL
	 * @param raceId　大会ID
	 * @param no ゼッケンNo.
	 * @return 取得した選手情報
	 * @throws ParserException
	 */
	public ParserRunnerInfo getRunnerInfo( String url, String raceId, String no) throws ParserException;

	/**
	 * 名前から選手情報を検索し取得する
	 * @param url アップデートサイトURL
	 * @param sei 姓
	 * @param mei 名
	 * @return 選手情報リスト
	 * @throws ParserException 
	 */
	public List<ParserRunnerInfo> searchRunnerInfoByName( String url, String raceId, String sei, String mei ) throws ParserException;
}
