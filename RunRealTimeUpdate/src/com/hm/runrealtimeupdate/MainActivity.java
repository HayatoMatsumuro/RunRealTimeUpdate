package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * メイン画面のActivity
 * @author Hayato Matsumuro
 *
 */
public class MainActivity extends Activity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		ListView raceInfoListView = ( ListView )findViewById( R.id.id_activity_main_body_contents_racelist_listview );

		// リストのアイテム短押し
		raceInfoListView.setOnItemClickListener(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View v, int position, long id )
				{
					// 選択した大会情報を取得する
					ListView listView = ( ListView )parent;
					RaceInfo raceInfo = ( RaceInfo )listView.getItemAtPosition( position );

					// 画面遷移
					Intent intent = new Intent( MainActivity.this, RaceTabActivity.class );
					intent.putExtra( RaceTabActivity.STR_INTENT_RACEID, raceInfo.id );
					intent.putExtra( RaceTabActivity.STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_DETAIL );
					startActivity( intent );
				}
			}
		);

		// リストのアイテム長押し
		raceInfoListView.setOnItemLongClickListener(
			new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick( AdapterView<?> parent, View v, int position, long id )
				{
					// 選択した大会情報を取得する
					ListView listView = ( ListView )parent;
					RaceInfo raceInfo = ( RaceInfo )listView.getItemAtPosition( position );

					// 削除ダイアログ表示
					InfoDialog<RaceInfo> raceDeleteInfoDialog = new InfoDialog<RaceInfo>( raceInfo, new RaceDeleteButtonCallbackImpl() );
					raceDeleteInfoDialog.onDialog
					(
						MainActivity.this,
						getString( R.string.str_dialog_title_deleterace ),
						createDialogMessage( raceInfo ),
						getString( R.string.str_dialog_msg_DEL ),
						getString( R.string.str_dialog_msg_NG )
					);

					return true;
				}
			}
		);

		// 大会登録ボタン
		Button entryBtn = ( Button )findViewById( R.id.id_activity_main_header_entry_button );
		entryBtn.setOnClickListener(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// 大会登録画面遷移
					Intent intent = new Intent( MainActivity.this, RaceEntryActivity.class );
					startActivity( intent );
				}
			}
		);

		return;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// 大会情報
		List<RaceInfo> raceInfoList = Logic.getRaceInfoList( getContentResolver() );

		// 速報サービス停止状態ならば、データベースの速報状態を停止する
		RaceInfo chkRaceInfo = null;
		for( RaceInfo raceInfo : raceInfoList )
		{
			if( raceInfo.updateSts != RaceInfo.INT_UPDATESTS_OFF )
			{
				chkRaceInfo = raceInfo;
				break;
			}
		}

		// 予約中または速報中の大会あり
		if( chkRaceInfo != null )
		{
			// 速報中
			if( chkRaceInfo.updateSts == RaceInfo.INT_UPDATESTS_ON )
			{
				// アラーム停止中
				if( !CommonLib.isSetUpdateAlarm( MainActivity.this ) )
				{
					// 速報状態停止
					Logic.setUpdateOffRaceId( getContentResolver(), chkRaceInfo.id );
					chkRaceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;
				}
			}
			// 予約中
			else if( chkRaceInfo.updateSts == RaceInfo.INT_UPDATESTS_RESERVE )
			{
				// アラーム停止中
				if( !CommonLib.isUpdateReserveAlarm( MainActivity.this ) )
				{
					// 予約状態停止
					Logic.setUpdateOffRaceId( getContentResolver(), chkRaceInfo.id );
					chkRaceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;
				}
			}
		}

		// 大会情報リスト設定
		RaceListAdapter adapter = new RaceListAdapter( this, raceInfoList );
		ListView raceInfoListView = ( ListView )findViewById( R.id.id_activity_main_body_contents_racelist_listview );
		raceInfoListView.setAdapter( adapter );

		// 表示状態設定
		setViewBody( raceInfoListView.getCount() );

		return;
	}

	/**
	 * ボディの表示を設定する
	 * @param raceInfoNum 大会情報の数
	 */
	private void setViewBody( int raceInfoNum )
	{
		// 大会リストまたは大会未登録メッセージの設定
		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_main_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_main_body_message_layout );

		if( raceInfoNum == 0 )
		{
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}
		else
		{
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );
		}

		return;
	}

	/**
	 * ダイアログメッセージ作成
	 * @param raceInfo 大会情報
	 * @return ダイアログメッセージ
	 */
	private String createDialogMessage( RaceInfo raceInfo )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( getString( R.string.str_dialog_msg_name ) );
		builder.append( "\n" );
		builder.append( raceInfo.name );
		builder.append( "\n" );

		return builder.toString();
	}

	/**
	 * 大会削除ダイアログのボタン押しコールバック
	 * @author Hayato Matsumuro
	 *
	 */
	private class RaceDeleteButtonCallbackImpl implements InfoDialog.ButtonCallback<RaceInfo>
	{
		@Override
		public void onClickPositiveButton( DialogInterface dialog, int which, RaceInfo info )
		{
			// 速報中でない
			if( info.updateSts == RaceInfo.INT_UPDATESTS_OFF )
			{
				// 大会削除
				Logic.deleteRaceInfo( getContentResolver(), info.id );
				
				// リストから大会削除
				ListView raceInfoListView = ( ListView )findViewById( R.id.id_activity_main_body_contents_racelist_listview );
				RaceListAdapter adapter = ( RaceListAdapter )raceInfoListView.getAdapter();
				adapter.remove( info );
				adapter.notifyDataSetChanged();

				// 表示状態設定
				setViewBody( raceInfoListView.getCount() );

				Toast.makeText( MainActivity.this, "削除しました", Toast.LENGTH_SHORT ).show();
			}
			else
			{
				Toast.makeText( MainActivity.this, "速報中または予約中のため、削除できません。", Toast.LENGTH_SHORT ).show();
			}

			return;
		}

		@Override
		public void onClickNegativeButton( DialogInterface dialog, int which, RaceInfo info )
		{
			return;
		}
	}

	/**
	 * 大会リストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RaceListAdapter extends ArrayAdapter<RaceInfo>
	{
		/**
		 * レイアウトインフレータ
		 */
		LayoutInflater m_Inflater;

		/**
		 * コンストラクタ
		 * @param context　コンテキスト
		 * @param raceInfoList 大会情報リスト
		 */
		public RaceListAdapter( Context context, List<RaceInfo> raceInfoList )
		{
			super( context, 0, raceInfoList );

			m_Inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			return;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null )
			{
				convertView = m_Inflater.inflate( R.layout.list_item_raceinfo, parent, false );
			}

			// 大会情報取得
			RaceInfo raceInfo = getItem( position );

			// 大会名表示
			TextView raceNameTextView = ( TextView )convertView.findViewById( R.id.id_list_item_raceinfo_race_name_textview );
			raceNameTextView.setText( raceInfo.name );

			// 速報中の表示
			RelativeLayout raceUpdateLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_raceinfo_update_layout );
			RelativeLayout raceReserveLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_raceinfo_reserve_layout );

			switch( raceInfo.updateSts )
			{
			// 速報中
			case RaceInfo.INT_UPDATESTS_ON:
				raceUpdateLayout.setVisibility( View.VISIBLE );
				raceReserveLayout.setVisibility( View.GONE );
				break;
			// 予約中
			case RaceInfo.INT_UPDATESTS_RESERVE:
				raceUpdateLayout.setVisibility( View.GONE );

				try
				{
					String time = Logic.getStringReserveTime( MainActivity.this );
					TextView textView = ( TextView )convertView.findViewById( R.id.id_list_item_raceinfo_reserve_textview );
					textView.setText( time + " " + getString( R.string.str_txt_updatereserve ) );
					raceReserveLayout.setVisibility( View.VISIBLE );
				}
				catch( LogicException e )
				{
					e.printStackTrace();
					raceReserveLayout.setVisibility( View.GONE );
				}
				break;
			// 停止中
			case RaceInfo.INT_UPDATESTS_OFF:
			default:
				raceUpdateLayout.setVisibility( View.GONE );
				raceReserveLayout.setVisibility( View.GONE );
				break;
			}

			return convertView;
		}
	}
}
