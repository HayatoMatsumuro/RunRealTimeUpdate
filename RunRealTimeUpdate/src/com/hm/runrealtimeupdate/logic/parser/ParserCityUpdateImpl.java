package com.hm.runrealtimeupdate.logic.parser;

import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserCityUpdateImpl implements ParserUpdate
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
		return null;
	}

	@Override
	public List<ParserRunnerInfo> searchRunnerInfoByName( String url, String pass, String sei, String mei ) throws ParserException
	{
		return null;
	}

}
