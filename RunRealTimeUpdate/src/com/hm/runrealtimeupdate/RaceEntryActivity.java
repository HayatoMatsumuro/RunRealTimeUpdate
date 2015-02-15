package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
					finish();

					return;
				}
			}
		);

		// 都市型ボタン
		Button cityButton = ( Button )findViewById( R.id.id_activity_raceentry_header_city_button );
		cityButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// 都市型マラソン大会登録画面遷移
					Intent intent = new Intent( RaceEntryActivity.this, RaceEntryCityActivity.class );
					startActivity( intent );
				}
			}
		);

		// 大会数
		int raceNum = Logic.getRaceInfoList( getContentResolver() ).size();

		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_raceentry_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_raceentry_body_message_layout );
		if( raceNum >= Common.INT_RACEINFO_NUM_MAX )
		{
			// 最大を上回っていたら、メッセージを表示
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );

			// 都市型ボタンを無効化
			cityButton.setEnabled( false );
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
					String inputPass = urlEdit.getText().toString();
					if( inputPass == null || inputPass.equals("") )
					{
						Toast.makeText( RaceEntryActivity.this, "urlを入力してください。", Toast.LENGTH_SHORT ).show();

						return;
					}

					// 取得したパスのフォーマット
					String pass = formatPass( inputPass );

					// 大会情報取得タスクの起動
					RaceInfoLoaderTask task = new RaceInfoLoaderTask( RaceEntryActivity.this, new RaceEntryButtonCallbackImpl() );
					RaceInfoLoaderTask.TaskParam param = task.new TaskParam();
					param.url = getString( R.string.str_txt_defaulturl );
					param.pass = pass;
					param.parserUpdate = getString( R.string.str_txt_defaultupdateparser );
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
	 * @param inputPass　入力されたパス
	 * @return　フォーマット後のパス
	 */
	private String formatPass( String inputPass )
	{
		String raceId = inputPass;

		// 最後が/だった場合は取り除く
		String lastStr = inputPass.substring( inputPass.length() - 1, inputPass.length() );
		if( lastStr.equals( "/" ) )
		{
			raceId = inputPass.substring( 0, inputPass.length() - 1 );
		}

		return raceId;
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
			if( Logic.checkEntryRaceId( getContentResolver(), info.id ) )
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
