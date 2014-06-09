package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ランナーズアップデートのパーサー
 * @author Hayato Matsumuro
 *
 */
public class ParserRunnersUpdate {

	private static final String STR_URL_RUNNERSUPDATE = "http://update.runnet.jp/";
	
	/**
	 * ランナーズアップデートのサイトから大会情報を取得する
	 * @param raceId 大会ID
	 * @return 大会情報
	 * @throws ParserException
	 */
	public static ParserRaceInfo getRaceInfo( String raceId ) throws ParserException{
		
		try {
			// ランネットサーバーアクセス
			String url = createRaceInfoURL( raceId );
			
			Connection connection = Jsoup.connect(url);
			if( connection == null ){
				throw new ParserException("接続に失敗しました。");
			}
			
			Document doc = connection.get();
			if( doc == null )
			{
				throw new ParserException("URLが不正です。");
			}
			
			// タイトルブロック取得
			Element titleBlockElement = doc.select("div#titleBlock").get(0);
			
			if( titleBlockElement == null ){
				throw new ParserException("取得に失敗しました。");
			}
			
			// タイトル取得
			Elements h2Elements = titleBlockElement.getElementsByTag("h2");
			String title = h2Elements.get(0).text();
			
			// 開催日、開催地取得
			Elements ddElements = titleBlockElement.getElementsByTag("dd");
			String date = ddElements.get(0).text();
			String location = ddElements.get(1).text();
			
			// 大会情報設定
			ParserRaceInfo parserRaceInfo = new ParserRaceInfo();
			parserRaceInfo.setName(title);
			parserRaceInfo.setDate(date);
			parserRaceInfo.setLocation(location);
			
			return parserRaceInfo;
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParserException("IOException.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Exception.");
		}
	}
	
	/**
	 * ランナーズアップデートのサイトからランナー情報を取得する
	 * @param raceId　大会ID
	 * @param no ゼッケンNo.
	 * @return
	 * @throws ParserException
	 */
	public static ParserRunnerInfo getRunnerInfo( String raceId, String no) throws ParserException{
		
		
		try {
			String url = createRunnerInfoURL(raceId, no);
			
			Connection connection = Jsoup.connect(url);
			if( connection == null ){
				throw new ParserException("接続に失敗しました。");
			}
			
			Document doc = connection.get();
			if( doc == null )
			{
				throw new ParserException("データ取得に失敗しました。");
			}
			
			// 個人情報取得
			Elements personalBlock = doc.select("div#personalBlock");
			if( ( personalBlock == null ) || ( personalBlock.isEmpty() ) ){
				throw new ParserException("データ取得に失敗しました。");
			}
			
			Element personalBlockElement = personalBlock.get(0);						
			if( personalBlockElement == null){
				throw new ParserException("ゼッケンNOが不正です。");
			}
			
			Elements ddElements = personalBlockElement.getElementsByTag("dd");
			if( ( ddElements == null ) || ( ddElements.isEmpty() )  ){
				throw new ParserException("ゼッケンNOが不正です。");
			}
			
			ParserRunnerInfo parserRunnerInfo = new ParserRunnerInfo();
			
			// 名前取得
			StringBuilder strName = new StringBuilder( ddElements.get(0).text() );
			strName.delete(0, 2);
			parserRunnerInfo.setName(strName.toString());
									
			// ゼッケンNO取得
			StringBuilder strNo = new StringBuilder( ddElements.get(1).text() );
			strNo.delete(0, 2);
			parserRunnerInfo.setNumber(strNo.toString());
									
			// 部門取得
			StringBuilder strSection = new StringBuilder( ddElements.get(2).text() );
			strSection.delete(0, 2);
			parserRunnerInfo.setSection(strSection.toString());
									
			// スプリット情報取得
			Elements mainBlock = doc.select("div#mainBlock");
			if( ( mainBlock == null ) || ( mainBlock.isEmpty() ) ){
				throw new ParserException("スプリットが不正です。");
			}
			
			Element mainBlockElement = mainBlock.get(0);						
			if( mainBlockElement == null){
				throw new ParserException("スプリットが不正です。");
			}
									
			Elements trElements = mainBlockElement.getElementsByTag("tr");
									
			trElements.remove(0);
									
			for(Element trElement:trElements){
				Elements tdElements = trElement.getElementsByTag("td");
										
				ParserRunnerInfo.TimeList timeList = new ParserRunnerInfo().new TimeList();
									
				timeList.setPoint(tdElements.get(0).text());
				timeList.setSplit(tdElements.get(1).text());
				timeList.setLap(tdElements.get(2).text());
				timeList.setCurrentTime(tdElements.get(3).text());
							
				parserRunnerInfo.addTimeList(timeList);
			}
			
			return parserRunnerInfo;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new ParserException("IOException.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Exception.");
		}
		
	}
	
	/**
	 * 大会情報を取得するURLを作成する
	 * @param raceId
	 * @return　大会情報アクセスURL
	 */
	private static String createRaceInfoURL( String raceId ){
		StringBuilder builder = new StringBuilder();
		builder.append(STR_URL_RUNNERSUPDATE);
		builder.append(raceId);
		
		return builder.toString();
	}
	
	/**
	 * ランナー情報を取得するURLを作成する
	 * @param raceId　大会ID
	 * @param no　ゼッケンNo.
	 * @return　選手情報アクセスURL
	 */
	private static String createRunnerInfoURL( String raceId, String no){
		
		StringBuilder builder = new StringBuilder();
		builder.append(STR_URL_RUNNERSUPDATE);
		builder.append(raceId);
		builder.append("/numberfile/");
		builder.append(no);
		builder.append(".html");
		
		return builder.toString();
	}
}
