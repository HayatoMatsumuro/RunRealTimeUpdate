package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * 更新開始ダイアログのActivity
 * @author Hayato Matsumuro
 *
 */
public class UpdateStartDialogActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setIcon( R.drawable.ic_launcher );
		dialog.setTitle( "速報を開始しました。" );
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
