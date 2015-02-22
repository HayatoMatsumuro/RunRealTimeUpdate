package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * 更新開始ダイアログのActivity
 * @author Hayato Matsumuro
 *
 */
public class UpdateDialogActivity extends Activity
{
	/**
	 * インテント タイトル
	 */
	static final String STR_INTENT_TITLE = "title";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		// 大会情報取得
		Intent intent = getIntent();

		// タイトル設定
		String title = intent.getStringExtra( STR_INTENT_TITLE );

		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setIcon( R.drawable.ic_launcher );
		dialog.setTitle( title );
		dialog.setPositiveButton
		(
			getString( R.string.str_dialog_msg_close ),
			new OnClickListener()
			{
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					// なにもしない
					finish();
				}
			}
		);

		dialog.show();

		return;
	}
}
