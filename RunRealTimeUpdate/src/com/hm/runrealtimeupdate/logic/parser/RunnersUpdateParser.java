package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * �����i�[�Y�A�b�v�f�[�g��������擾
 * @author Hayato Matsumuro
 *
 */
public class RunnersUpdateParser {

	/**
	 * ������Http�Ŏ擾
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
			
			// �^�C�g���u���b�N�擾
			Element titleBlockElement = doc.select("div#titleBlock").get(0);
			
			if( titleBlockElement == null ){
				throw new ParserException("Page Error.");
			}
			
			// �^�C�g���擾
			Elements h2Elements = titleBlockElement.getElementsByTag("h2");
			String title = h2Elements.get(0).text();
			
			// �J�Ó��A�J�Òn�擾
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
