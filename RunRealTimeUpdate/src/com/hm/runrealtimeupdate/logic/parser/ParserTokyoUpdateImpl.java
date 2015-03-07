package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParserTokyoUpdateImpl implements IParserUpdate
{
	@Override
	public ParserRaceInfo getRaceInfo( String url, String pass ) throws ParserException
	{
		try
		{
			Connection connection = Jsoup.connect( url );

			if( connection == null )
			{
				throw new ParserException( "接続に失敗しました。" );
			}

			Document doc = connection.get();
			if( doc == null )
			{
				throw new ParserException( "URLが不正です。" );
			}

			// 大会情報はhtmlから取得できない
			// 接続できれば、大会登録可能とする
			// 大会情報設定
			ParserRaceInfo parserRaceInfo = new ParserRaceInfo();

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

	@Override
	public ParserRunnerInfo getRunnerInfo( String url, String pass, String no ) throws ParserException
	{
		try
		{
			String runnerInfoUrl = createRunnerInfoURL( url, no );

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
			Elements contentsBlock = doc.select( "div#contentsBlock" );
			if( ( contentsBlock == null ) || ( contentsBlock.isEmpty() ) )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			Element contentsBlockElement = contentsBlock.get( 0 );
			if( contentsBlockElement == null )
			{
				throw new ParserException( "ゼッケンNOが不正です。" );
			}

			Elements ddElements = contentsBlockElement.getElementsByTag( "dd" );
			if( ( ddElements == null ) || ( ddElements.isEmpty() ) )
			{
				throw new ParserException( "ゼッケンNOが不正です。" );
			}

			ParserRunnerInfo parserRunnerInfo = new ParserRunnerInfo();

			// ゼッケンNo取得
			StringBuilder strNo = new StringBuilder( ddElements.get( 2 ).text() );
			parserRunnerInfo.number = strNo.toString();

			// 名前取得
			StringBuilder strName = new StringBuilder( ddElements.get( 3 ).text() );
			parserRunnerInfo.name = strName.toString();

			// 部門取得
			StringBuilder strSection = new StringBuilder( ddElements.get( 4 ).text() );
			parserRunnerInfo.section = strSection.toString();

			// スプリット情報取得
			Elements listAdWrapper = contentsBlock.select( "#listArea .sarchList" );
			if( ( listAdWrapper == null ) || ( listAdWrapper.isEmpty() ) )
			{
				throw new ParserException( "スプリットが不正です。" );
			}

			Element mainBlockElement = listAdWrapper.get(0);
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

				if( tdElements.size() == 1 )
				{
					break;
				}

				String[] splits = tdElements.get( 1 ).text().split("　");
				timeInfo.split = splits[ 0 ];
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

	@Override
	public List<ParserRunnerInfo> searchRunnerInfoByName( String url, String pass, String sei, String mei ) throws ParserException
	{
		List<ParserRunnerInfo> parserRunnerInfoList = null;

		String runnerinfoUrl = createRunnerInfoByNameURL( url );

		try
		{
			Connection connection = Jsoup.connect( runnerinfoUrl );
			if( connection == null )
			{
				throw new ParserException( "接続に失敗しました。" );
			}

			Document doc = connection.data( "name1", sei, "name2", mei ).get();
			if( doc == null )
			{
				throw new ParserException( "データ取得に失敗しました。" );
			}

			// メインブロック取得
			Elements mainBlock = doc.select("div#contentsBlock");
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
			Elements aElements = mainBlockElement.getElementsByTag( "a" );
			if( aElements == null )
			{
				throw new ParserException( "選手情報取得に失敗しました。" );
			}
			if( aElements.isEmpty() ){
				throw new ParserException( "選手情報がありません。" );
			}

			Element aElement = aElements.get( 0 );
			if( aElement == null )
			{
				throw new ParserException( "選手情報が不正です。" );
			}
			String number = aElement.text();

			// 数値変換できないならば、情報不正
			try
			{
				Integer.parseInt( number );
			}
			catch( NumberFormatException e )
			{
				throw new ParserException( "選手情報が不正です。" );
			}

			ParserRunnerInfo pRunnerInfo = new ParserRunnerInfo();
			pRunnerInfo.name = " ";
			pRunnerInfo.number = number;

			parserRunnerInfoList = new ArrayList<ParserRunnerInfo>();
			parserRunnerInfoList.add( pRunnerInfo );

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
	 * ランナー情報を取得するURLを作成する
	 * @param url アップデートサイトURL
	 * @param no　ゼッケンNo.
	 * @return　選手情報アクセスURL
	 */
	private static String createRunnerInfoURL( String url, String no )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( url );
		builder.append( "numberfile/" );
		builder.append( no );
		builder.append( ".html" );

		return builder.toString();
	}

	private static String createRunnerInfoByNameURL( String url )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( url );
		builder.append( "php/name.php" );
		return builder.toString();
	}
}
