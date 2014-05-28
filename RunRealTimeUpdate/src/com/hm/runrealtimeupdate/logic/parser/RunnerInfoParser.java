package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ランナーズアップデートのランナーの画面のパース
 * @author Hayato Matsumuro
 *
 */
public class RunnerInfoParser {
	
	public static ParserRunnerInfo getRunnerInfo( String url, String raceId, String no) throws ParserException{
		
		ParserRunnerInfo runnerInfo = new ParserRunnerInfo();
		
		String runnerInfoURL = createRunnerInfoURL(url,raceId,no);
		
		Document doc = null;
		try {
			doc = Jsoup.connect(runnerInfoURL).get();
			
			if(doc == null){
				throw new ParserException("ゼッケンNo. が不正です。");
			}
			
			// 個人情報取得
			Element personalBlockElement = doc.select("div#personalBlock").get(0);
						
			if( personalBlockElement == null){
				throw new ParserException("ゼッケンNOが不正です。");
			}

			Elements ddElements = personalBlockElement.getElementsByTag("dd");
			
			// 名前取得
			StringBuilder strName = new StringBuilder( ddElements.get(0).text() );
			strName.delete(0, 2);
			runnerInfo.setName(strName.toString());
						
			// ゼッケンNO取得
			StringBuilder strNo = new StringBuilder( ddElements.get(1).text() );
			strNo.delete(0, 2);
			runnerInfo.setNumber(strNo.toString());
						
			// 部門取得
			StringBuilder strSection = new StringBuilder( ddElements.get(2).text() );
			strSection.delete(0, 2);
			runnerInfo.setSection(strSection.toString());
						
			// スプリット情報取得
			Element mainBlockElement = doc.select("div#mainBlock").get(0);
						
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
				
				runnerInfo.addTimeList(timeList);
			}
							
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			throw new ParserException("IOException");
		}
		return runnerInfo;
		
	}
	
	private static String createRunnerInfoURL( String url, String raceId, String no){
		
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append(raceId);
		builder.append("/numberfile/");
		builder.append(no);
		builder.append(".html");
		
		return builder.toString();
	}

}
