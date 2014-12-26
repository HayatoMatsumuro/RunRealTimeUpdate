package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 大会登録画面のActivity
 * @author Hayato Matsumuro
 *
 */
public class RaceEntryActivity extends Activity
{
	/**
	 * インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	/**
	 * 登録できる大会の最大数
	 */
	private static final int INT_RACEINFO_NUM_MAX = 5;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_raceentry );

		// 大会ID取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );

		// 戻るボタン
		Button backBtn = ( Button )findViewById( R.id.id_activity_raceentry_header_back_button );
		backBtn.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// 大会登録画面遷移
					Intent intent = new Intent( RaceEntryActivity.this, MainActivity.class );
					startActivity( intent );

					return;
				}
			}
		);

		// 大会数
		int raceNum = Logic.getRaceInfoList( getContentResolver() ).size();

		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_raceentry_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_raceentry_body_message_layout );
		if( raceNum >= INT_RACEINFO_NUM_MAX )
		{
			// 最大を上回っていたら、メッセージを表示
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}
		else
		{
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );
		}

		// 大会ID入力
		EditText urlEdit = ( EditText )findViewById( R.id.id_activity_raceentry_body_contents_urlform_inputurl_edittext );
		urlEdit.setText( raceId );

		// 決定ボタン
		Button decideBtn = ( Button )findViewById( R.id.id_activity_raceentry_body_contenturl_inputform_decide_button );
		decideBtn.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// URL入力エディットボックスから入力値取得
					EditText urlEdit = ( EditText )findViewById( R.id.id_activity_raceentry_body_contents_urlform_inputurl_edittext );

					// 何も入力してないならば、以降の処理をしない
					String inputRaceId = urlEdit.getText().toString();
					if( inputRaceId == null || inputRaceId.equals("") )
					{
						Toast.makeText( RaceEntryActivity.this, "urlを入力してください。", Toast.LENGTH_SHORT ).show();

						return;
					}

					// 取得した大会IDのフォーマット
					String raceId = formatRaceId( inputRaceId );

					// 大会情報取得タスクの起動
					RaceInfoLoaderTask task = new RaceInfoLoaderTask();
					RaceInfoLoaderTask.TaskParam param = task.new TaskParam();
					param.setUrl( getString( R.string.str_txt_defaulturl ) );
					param.setRaceId( raceId );
					task.execute( param );

					return;
				}
			}
		);

		// QRコードボタン
		Button qrBtn = ( Button )findViewById( R.id.id_activity_raceentry_body_contents_qr_button );
		qrBtn.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// QR検索画面
					Intent intent = new Intent( RaceEntryActivity.this, RaceEntryQRActivity.class );
					startActivity( intent );

					return;
				}
			}
		);

		return;
	}

	/**
	 * 入力された大会IDのフォーマットをする
	 *  最後の"/" を削除する
	 * @param inputRaceId　入力された大会ID
	 * @return　大会ID
	 */
	private String formatRaceId( String inputRaceId )
	{
		String raceId = inputRaceId;

		// 最後が/だった場合は取り除く
		String lastStr = inputRaceId.substring( inputRaceId.length() - 1, inputRaceId.length() );
		if( lastStr.equals( "/" ) )
		{
			raceId = inputRaceId.substring( 0, inputRaceId.length() - 1 );
		}

		return raceId;
	}

	/**
	 * 大会情報取得タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class RaceInfoLoaderTask extends AsyncTask<RaceInfoLoaderTask.TaskParam, Void, RaceInfo>
	{
		/**
		 * 進捗ダイアログ
		 */
		private ProgressDialog m_ProgressDialog = null;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// 進捗ダイアログ作成
			m_ProgressDialog = new ProgressDialog( RaceEntryActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_raceinfo ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton
			(
				DialogInterface.BUTTON_NEGATIVE,
				getResources().getString( R.string.str_dialog_msg_cancel ),
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
		protected RaceInfo doInBackground( TaskParam... params )
		{
			RaceInfo raceInfo = null;

			try
			{
				// 大会情報取得
				String url = params[0].getUrl();
				String raceId = params[0].getRaceId();
				raceInfo = Logic.getNetRaceInfo( url, raceId );

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
				Toast.makeText( RaceEntryActivity.this, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT ).show();
				return;
			}
			else
			{
				InfoDialog<RaceInfo> raceEntryInfoDialog = new InfoDialog<RaceInfo>( raceInfo, new RaceEntryButtonCallbackImpl() );
				raceEntryInfoDialog.onDialog(
					RaceEntryActivity.this,
					getString( R.string.str_dialog_title_race ),
					createDialogMessage( raceInfo ),
					getString( R.string.str_dialog_msg_OK ),
					getString( R.string.str_dialog_msg_NG )
				);
			}

			return;
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();

			// ダイアログ削除
			if( m_ProgressDialog != null )
			{
				m_ProgressDialog.dismiss();
			}

			Toast.makeText( RaceEntryActivity.this, "大会情報取得をキャンセルしました。", Toast.LENGTH_SHORT ).show();

			return;
		}

		/**
		 * タスクパラメータ
		 * @author Hayato Matsumuro
		 *
		 */
		public class TaskParam{

			/**
			 * アップデートサイトURL
			 */
			private String url;

			/**
			 * 大会ID
			 */
			private String raceId;

			/**
			 * アップデートサイトURLを取得する
			 * @return アップデートサイトURL
			 */
			public String getUrl()
			{
				return url;
			}

			/**
			 * アップデートサイトURLを設定する
			 * @param url アップデートサイトURL
			 */
			public void setUrl( String url )
			{
				this.url = url;

				return;
			}

			/**
			 * 大会IDを取得する
			 * @return 大会ID
			 */
			public String getRaceId()
			{
				return raceId;
			}

			/**
			 * 大会IDを設定する
			 * @param raceId 大会ID
			 */
			public void setRaceId( String raceId )
			{
				this.raceId = raceId;

				return;
			}
		}
	}

	/**
	 * 登録ダイアログのメッセージを作成する
	 * @param raceInfo 大会情報
	 * @return メッセージ
	 */
	private String createDialogMessage( RaceInfo raceInfo )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( getString( R.string.str_dialog_msg_name ) );
		builder.append( "\n" );
		builder.append( raceInfo.getRaceName() );
		builder.append( "\n");
		builder.append( getString( R.string.str_dialog_msg_date ) );
		builder.append( "\n" );
		builder.append( raceInfo.getRaceDate() );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_location ) );
		builder.append( "\n" );
		builder.append( raceInfo.getRaceLocation() );

		return builder.toString();
	}

	/**
	 * 大会登録ダイアログのボタン押しのコールバック
	 * @author Hayato Matsumuro
	 *
	 */
	private class RaceEntryButtonCallbackImpl implements InfoDialog.ButtonCallback<RaceInfo>
	{
		@Override
		public void onClickPositiveButton( DialogInterface dialog, int which, RaceInfo info )
		{
			if( Logic.checkEntryRaceId( getContentResolver(), info.getRaceId() ) )
			{
				// すでに大会が登録済み
				Toast.makeText( RaceEntryActivity.this, "この大会はすでに登録済みです。", Toast.LENGTH_SHORT ).show();
			}
			else
			{
				// データベース登録
				Logic.entryRaceInfo( getContentResolver(), info );

				Toast.makeText( RaceEntryActivity.this, "登録しました", Toast.LENGTH_SHORT ).show();

				// メイン画面に遷移
				Intent intent = new Intent( RaceEntryActivity.this, MainActivity.class );
				startActivity( intent );
			}

			return;
		}

		@Override
		public void onClickNegativeButton( DialogInterface dialog, int which, RaceInfo info )
		{
			return;
		}
	}
}
