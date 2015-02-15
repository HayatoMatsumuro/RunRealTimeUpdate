package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * 大会登録タスク
 * @author Hayato Matsumuro
 *
 */
class RaceInfoLoaderTask extends AsyncTask<RaceInfoLoaderTask.TaskParam, Void, RaceInfo>
{
	/**
	 * 進捗ダイアログ
	 */
	private ProgressDialog m_ProgressDialog = null;

	/**
	 * コンテキスト
	 */
	private Context m_Context;

	/**
	 * ポジティブボタン押しコールバック
	 */
	private InfoDialog.ButtonCallback<RaceInfo> m_Callback;

	/**
	 * 大会名( 取得できない場合 )
	 */
	private String m_RaceName = null;

	/**
	 * 大会ID( 取得できない場合 )
	 */
	private String m_RaceId = null;

	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 * @param callback コールバック
	 */
	public RaceInfoLoaderTask( Context context, InfoDialog.ButtonCallback<RaceInfo> callback )
	{
		m_Context = context;
		m_Callback = callback;
	}

	/**
	 * コンストラクタ
	 * @param raceName 大会名
	 * @param raceId 大会ID
	 */
	public RaceInfoLoaderTask( Context context, InfoDialog.ButtonCallback<RaceInfo> callback, String raceName, String raceId )
	{
		m_Context = context;
		m_Callback = callback;
		m_RaceName = raceName;
		m_RaceId = raceId;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		// 進捗ダイアログ作成
		m_ProgressDialog = new ProgressDialog( m_Context );
		m_ProgressDialog.setTitle( m_Context.getResources().getString( R.string.str_dialog_title_progress_raceinfo ) );
		m_ProgressDialog.setMessage( m_Context.getResources().getString( R.string.str_dialog_msg_get ) );
		m_ProgressDialog.setCancelable( true );
		m_ProgressDialog.setButton
		(
			DialogInterface.BUTTON_NEGATIVE,
			m_Context.getResources().getString( R.string.str_dialog_msg_cancel ),
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					cancel( true );

					return;
				}
			}
		);

		m_ProgressDialog.show();

		return;
	}

	@Override
	protected RaceInfo doInBackground( RaceInfoLoaderTask.TaskParam... params )
	{
		RaceInfo raceInfo = null;

		try
		{
			// 大会情報取得
			String url = params[0].url;
			String pass = params[0].pass;
			String parserUpdate = params[0].parserUpdate;
			raceInfo = Logic.getNetRaceInfo( url, pass, parserUpdate );
		}
		catch ( LogicException e )
		{
			e.printStackTrace();
		}

		return raceInfo;
	}

	@Override
	protected void onPostExecute( RaceInfo raceInfo )
	{
		if( m_ProgressDialog != null )
		{
			// ダイアログ削除
			m_ProgressDialog.dismiss();
		}

		if( raceInfo == null )
		{
			// 大会情報取得失敗
			Toast.makeText( m_Context, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT ).show();
			return;
		}

		// 大会名が取得出来ないならば、デフォルト大会名を設定する
		if( raceInfo.name == null || raceInfo.name.equals("") )
		{
			raceInfo.name = m_RaceName;
		}

		// 大会IDが取得出来ないならば、デフォルト大会IDを設定する
		if( raceInfo.id == null || raceInfo.id.equals("") )
		{
			raceInfo.id = m_RaceId;
		}

		// 情報ダイアログ表示
		InfoDialog<RaceInfo> raceEntryInfoDialog = new InfoDialog<RaceInfo>( raceInfo, m_Callback );
		raceEntryInfoDialog.onDialog(
			m_Context,
			m_Context.getString( R.string.str_dialog_title_race ),
			createDialogMessage( raceInfo ),
			m_Context.getString( R.string.str_dialog_msg_OK ),
			m_Context.getString( R.string.str_dialog_msg_NG )
		);

		return;
	}

	/**
	 * 登録ダイアログのメッセージを作成する
	 * @param raceInfo 大会情報
	 * @return メッセージ
	 */
	private String createDialogMessage( RaceInfo raceInfo )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( m_Context.getString( R.string.str_dialog_msg_name ) );
		builder.append( "\n" );
		builder.append( raceInfo.name );
		builder.append( "\n");

		if( raceInfo.date != null )
		{
			builder.append( m_Context.getString( R.string.str_dialog_msg_date ) );
			builder.append( "\n" );
			builder.append( raceInfo.date );
			builder.append( "\n" );
		}

		if( raceInfo.location != null )
		{
			builder.append( m_Context.getString( R.string.str_dialog_msg_location ) );
			builder.append( "\n" );
			builder.append( raceInfo.location );
			builder.append( "\n" );
		}

		return builder.toString();
	}

	/**
	 * タスクパラメータ
	 * @author Hayato Matsumuro
	 *
	 */
	class TaskParam
	{
		/**
		 * アップデートサイトURL
		 */
		String url;

		/**
		 * パス
		 */
		String pass;

		/**
		 * アップデートパーサークラス名
		 */
		String parserUpdate;
	}
}
