package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class ParserRunnersUpdate
{
	/**
	 * ランナーズアップデートのサイトから大会情報を取得する
	 * @param url アップデートサイトのURL
	 * @param raceId 大会ID
	 * @return 大会情報
	 * @throws ParserException
	 */
	public static ParserRaceInfo getRaceInfo( String url, String raceId ) throws ParserException
	{
		try
		{
			// ランネットサーバーアクセス
			String raceInfoUrl = createRaceInfoURL( url, raceId );

			Connection connection = Jsoup.connect( raceInfoUrl );
			if( connection == null )
			{
				throw new ParserException( "接続に失敗しました。" );
			}

			Document doc = connection.get();
			if( doc == null )
			{
				throw new ParserException( "URLが不正です。" );
			}

			// タイトルブロック取得
			Element titleBlockElement = doc.select( "div#titleBlock" ).get( 0 );

			if( titleBlockElement == null )
			{
				throw new ParserException( "取得に失敗しました。" );
			}

			// タイトル取得
			Elements h2Elements = titleBlockElement.getElementsByTag( "h2" );
			String title = h2Elements.get( 0 ).text();

			// 開催日、開催地取得
			Elements ddElements = titleBlockElement.getElementsByTag( "dd" );
			String date = ddElements.get( 0 ).text();
			String location = ddElements.get( 1 ).text();

			// 大会情報設定
			ParserRaceInfo parserRaceInfo = new ParserRaceInfo();
			parserRaceInfo.name = title;
			parserRaceInfo.date = date;
			parserRaceInfo.location = location;

			return parserRaceInfo;

		}
		catch( IOException e )
		{
			e.printStackTrace();
			throw new ParserException( "IOException." );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw new ParserException( "Exception." );
		}
	}

	/**
	 * ランナーズアップデートのサイトからランナー情報を取得する
	 * @param url アップデートサイトURL
	 * @param raceId　大会ID
	 * @param no ゼッケンNo.
	 * @return 取得した選手情報
	 * @throws ParserException
	 */
	public static ParserRunnerInfo getRunnerInfo( String url, String raceId, String no) throws ParserException
	{
		try
		{
			String runnerInfoUrl = createRunnerInfoURL( url, raceId, no );

			Connection connection = Jsoup.connect( runnerInfoUrl );
			if( connection == null )
			{
				throw new ParserException( "接続に失敗しました。" );
			}

			Document doc = connection.get();
			if( doc == null )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			// 個人情報取得
			Elements personalBlock = doc.select( "div#personalBlock" );
			if( ( personalBlock == null ) || ( personalBlock.isEmpty() ) )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			Element personalBlockElement = personalBlock.get( 0 );
			if( personalBlockElement == null )
			{
				throw new ParserException( "ゼッケンNOが不正です。" );
			}

			Elements ddElements = personalBlockElement.getElementsByTag( "dd" );
			if( ( ddElements == null ) || ( ddElements.isEmpty() ) )
			{
				throw new ParserException( "ゼッケンNOが不正です。" );
			}

			ParserRunnerInfo parserRunnerInfo = new ParserRunnerInfo();

			// 名前取得
			StringBuilder strName = new StringBuilder( ddElements.get( 0 ).text() );
			strName.delete( 0, 2 );
			parserRunnerInfo.name = strName.toString();

			// ゼッケンNO取得
			StringBuilder strNo = new StringBuilder( ddElements.get( 1 ).text() );
			strNo.delete( 0, 2 );
			parserRunnerInfo.number = strNo.toString();

			// 部門取得
			StringBuilder strSection = new StringBuilder( ddElements.get(2).text() );
			strSection.delete( 0, 2 );
			parserRunnerInfo.section = strSection.toString();

			// スプリット情報取得
			Elements mainBlock = doc.select( "div#mainBlock" );
			if( ( mainBlock == null ) || ( mainBlock.isEmpty() ) )
			{
				throw new ParserException( "スプリットが不正です。" );
			}

			Element mainBlockElement = mainBlock.get(0);
			if( mainBlockElement == null )
			{
				throw new ParserException( "スプリットが不正です。" );
			}

			Elements trElements = mainBlockElement.getElementsByTag( "tr" );
			trElements.remove( 0 );

			for( Element trElement : trElements )
			{
				Elements tdElements = trElement.getElementsByTag( "td" );

				ParserRunnerInfo.TimeInfo timeInfo = new ParserRunnerInfo().new TimeInfo();

				timeInfo.point = tdElements.get( 0 ).text();
				timeInfo.split = tdElements.get( 1 ).text();
				timeInfo.lap = tdElements.get( 2 ).text();
				timeInfo.currentTime = tdElements.get( 3 ).text();

				parserRunnerInfo.timeList.add( timeInfo );
			}

			return parserRunnerInfo;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			throw new ParserException( "IOException." );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw new ParserException( "Exception." );
		}
	}

	/**
	 * 名前から選手情報を検索し取得する
	 * @param url アップデートサイトURL
	 * @param sei 姓
	 * @param mei 名
	 * @return 選手情報リスト
	 * @throws ParserException 
	 */
	public static List<ParserRunnerInfo> searchRunnerInfoByName( String url, String raceId, String sei, String mei ) throws ParserException
	{
		List<ParserRunnerInfo> parserRunnerInfoList = null;

		String runnerinfoUrl = createRunnerInfoByNameURL( url, raceId );

		try
		{
			Connection connection = Jsoup.connect( runnerinfoUrl );
			if( connection == null )
			{
				throw new ParserException( "接続に失敗しました。" );
			}

			Document doc = connection.data( "name1", sei, "name2", mei ).post();
			if( doc == null )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			// メインブロック取得
			Elements mainBlock = doc.select("div#mainBlock");
			if( ( mainBlock == null ) || ( mainBlock.isEmpty() ) )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			Element mainBlockElement = mainBlock.get(0);
			if( mainBlockElement == null)
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			// 選手情報を取得
			Elements trElements = mainBlockElement.getElementsByTag( "tr" );
			if( trElements == null )
			{
				throw new ParserException( "選手情報取得に失敗しました。" );
			}
			if( trElements.isEmpty() ){
				throw new ParserException( "選手情報がありません。" );
			}

			trElements.remove( 0 );

			int size = trElements.size();

			parserRunnerInfoList = new ArrayList<ParserRunnerInfo>();

			for( int i = 0; i < size; i++ )
			{
				Element trElement = trElements.get( i );
				if( trElement == null)
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				Elements tdElements = trElement.getElementsByTag( "td" );
				if( tdElements == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				// 名前要素取得
				Element tdNameElement = tdElements.get( 0 );
				if( tdNameElement == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				Elements aNameElements = tdNameElement.getElementsByTag( "a" );
				if( aNameElements == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				Element aNameElement = aNameElements.get( 0 );
				if( aNameElement == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}
				String name = aNameElement.text();

				// ゼッケン番号要素取得
				Element tdNumberElement = tdElements.get( 1 );
				if( tdNumberElement == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				Elements aNumberElements = tdNumberElement.getElementsByTag( "a" );
				if( aNumberElements == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}

				Element aNumberElement = aNumberElements.get( 0 );
				if( aNumberElement == null )
				{
					throw new ParserException( "選手情報が不正です。" );
				}
				String number = aNumberElement.text();

				ParserRunnerInfo pRunnerInfo = new ParserRunnerInfo();
				pRunnerInfo.name = name;
				pRunnerInfo.number = number;
				parserRunnerInfoList.add( pRunnerInfo );
			}
		}
		catch( ParserException e )
		{
			e.printStackTrace();
			throw new ParserException( "Exception." );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			throw new ParserException( "IOException." );
		}

		return parserRunnerInfoList;
	}

	/**
	 * 大会情報を取得するURLを作成する
	 * @param url アップデートサイトURL
	 * @param raceId 大会ID
	 * @return　大会情報アクセスURL
	 */
	private static String createRaceInfoURL( String url, String raceId )
	{
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append(raceId);
		return builder.toString();
	}

	/**
	 * ランナー情報を取得するURLを作成する
	 * @param url アップデートサイトURL
	 * @param raceId　大会ID
	 * @param no　ゼッケンNo.
	 * @return　選手情報アクセスURL
	 */
	private static String createRunnerInfoURL( String url, String raceId, String no )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( url );
		builder.append( raceId );
		builder.append( "/numberfile/" );
		builder.append( no );
		builder.append( ".html" );

		return builder.toString();
	}

	private static String createRunnerInfoByNameURL( String url, String raceId )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( url );
		builder.append( raceId );
		builder.append( "/php/name.php" );
		return builder.toString();
	}
}
