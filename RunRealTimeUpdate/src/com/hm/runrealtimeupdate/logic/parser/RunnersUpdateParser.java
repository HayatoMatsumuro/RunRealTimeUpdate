package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ランナーズアップデートから情報を取得
 * @author Hayato Matsumuro
 *
 */
public class RunnersUpdateParser {

	/**
	 * 大会情報をHttpで取得
	 * @param url URL
	 * @return
	 */
	public static RaceInfo getRaceInfo( String url ) throws ParserException{
		
		try{
			Document doc = Jsoup.connect(url).get();
			
			if( doc == null )
			{
				throw new ParserException("URL Error.");
			}
			
			// タイトルブロック取得
			Element titleBlockElement = doc.select("div#titleBlock").get(0);
			
			if( titleBlockElement == null ){
				throw new ParserException("Page Error.");
			}
			
			// タイトル取得
			Elements h2Elements = titleBlockElement.getElementsByTag("h2");
			String title = h2Elements.get(0).text();
			
			// 開催日、開催地取得
			Elements ddElements = titleBlockElement.getElementsByTag("dd");
			String date = ddElements.get(0).text();
			String location = ddElements.get(1).text();
			
			RaceInfo raceInfo = new RaceInfo();
			
			raceInfo.setName(title);
			raceInfo.setDate(date);
			raceInfo.setLocation(location);
			
			return raceInfo;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParserException("IOException.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParserException("Exception.");
		}
	}
}
