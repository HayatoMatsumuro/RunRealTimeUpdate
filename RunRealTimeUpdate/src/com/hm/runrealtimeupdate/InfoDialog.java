package com.hm.runrealtimeupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

class InfoDialog<T>
{
	/**
	 * ボタン押し時に、引数として渡す情報
	 */
	private T m_Info;

	/**
	 * ボタン押しで、コールするイベント
	 */
	private ButtonCallback<T> m_ButtonCallback;

	/**
	 * コンストラクタ
	 */
	InfoDialog( T info, ButtonCallback<T> buttonEvent )
	{
		m_Info = info;
		m_ButtonCallback = buttonEvent;
	}

	/**
	 * ダイアログを表示
	 * @param context コンテキスト
	 * @param title ダイアログタイトル
	 * @param message ダイアログメッセージ
	 * @param positive ポジティブボタンのメッセージ
	 * @param negative ネガティブボタンのメッセージ
	 */
	void onDialog( Context context, String title, String message, String positive, String negative )
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder( context );
		dialog.setTitle( title );
		dialog.setMessage( message );

		// ポジティブボタン
		dialog.setPositiveButton(
			positive,
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					m_ButtonCallback.onClickPositiveButton( dialog, which, m_Info );	
				}
			}
		);

		// ネガティブボタン
		dialog.setNegativeButton(
			negative,
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					m_ButtonCallback.onClickNegativeButton( dialog, which, m_Info );	
				}
			}
		);

		dialog.show();
	}

	/**
	 * ダイアログボタン押しイベント
	 * @author Hayato Matsumuro
	 *
	 * @param <T>
	 */
	interface ButtonCallback<T>
	{
		public void onClickPositiveButton( DialogInterface dialog, int which, T info );

		public void onClickNegativeButton( DialogInterface dialog, int which, T info );
	}
}
