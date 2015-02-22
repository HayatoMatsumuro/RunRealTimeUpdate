package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.UpdateInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 更新リスト画面のActivity
 * @author Hayato Matsumuro
 *
 */
public class UpdateListActivity extends Activity
{
	/**
	 * インテント 大会ID
	 */
	static final String STR_INTENT_RACEID = "raceid";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);

		// 大会Id取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );

		// 大会情報が取得できないなら、エラー画面
		if( ( raceId == null ) || ( raceId.equals( "" ) ) )
		{
			Intent intentErr = new Intent( UpdateListActivity.this, ErrorActivity.class );
			intentErr.putExtra( ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。" );
			return;
		}

		// リストの選手情報タッチ
		ListView updateListView = ( ListView )findViewById( R.id.id_activity_updatelist_body_contents_update_listview );
		updateListView.setOnItemClickListener(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
					ListView listView = ( ListView )parent;
					UpdateInfo updateInfo = ( UpdateInfo )listView.getItemAtPosition( position );

					AlertDialog.Builder dialog = new AlertDialog.Builder( UpdateListActivity.this );
					dialog.setTitle( getString( R.string.str_dialog_title_updatedetail ) );
					dialog.setMessage( createDialogMessage( updateInfo ) );
					dialog.setNegativeButton( getString( R.string.str_dialog_msg_close ),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick( DialogInterface arg0, int arg1 )
							{
								return;
							}
						}
					);

					dialog.show();

					return;
				}
			}
		);

		return;
	}

	private String createDialogMessage( UpdateInfo updateInfo )
	{
		StringBuilder builder = new StringBuilder();

		builder.append( updateInfo.number );
		builder.append( "\n" );
		builder.append( updateInfo.name );
		builder.append( "\n\n" );
		builder.append( updateInfo.point );
		builder.append( "\n\n" );
		builder.append( getString( R.string.str_dialog_msg_split ) );
		builder.append( updateInfo.split );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_lap ) );
		builder.append( updateInfo.lap );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_currenttime ) );
		builder.append( updateInfo.currentTime );

		return builder.toString();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// 大会Id取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		RelativeLayout contentsLayout = ( RelativeLayout )findViewById(R.id.id_activity_updatelist_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_updatelist_body_message_layout );

		// 速報データ取得
		List<UpdateInfo> updateInfoList = Logic.getUpdateInfoList( getContentResolver(), raceId, Common.LONG_RESENT_TIME );

		if( updateInfoList.isEmpty() ){
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}
		else
		{
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );

			ListView updateListView = ( ListView )findViewById( R.id.id_activity_updatelist_body_contents_update_listview );
			UpdateDataAdapter adapter = ( UpdateDataAdapter )updateListView.getAdapter();

			if( adapter != null )
			{
				adapter.clear();
			}

			// リストビュー設定
			adapter = new UpdateDataAdapter(this, updateInfoList);
			updateListView.setAdapter(adapter);
		}

		// 速報バーの表示更新
		( ( RaceTabActivity )getParent() ).setDispUpdateBar( raceInfo.updateSts );
	
		return;
	}

	/**
	 * 速報データリストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class UpdateDataAdapter extends ArrayAdapter<UpdateInfo>
	{
		/**
		 * レイアウトインフライヤー
		 */
		private LayoutInflater m_Inflater;

		/**
		 * コンストラクタ
		 * @param context コンテキスト
		 * @param updateInfoList 更新情報リスト
		 */
		private UpdateDataAdapter( Context context, List<UpdateInfo> updateInfoList )
		{
			super( context, 0, updateInfoList );

			m_Inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			return;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null )
			{
				convertView = m_Inflater.inflate( R.layout.list_item_updatedata, parent, false );
			}

			TextView infoTextView = ( TextView )convertView.findViewById( R.id.id_list_item_updatedata_updateinfo_info_textview );
			TextView splitTextView = ( TextView )convertView.findViewById( R.id.id_list_item_updatedata_sub_timelist_split_textview );
			TextView currentTimeTextView = ( TextView )convertView.findViewById( R.id.id_list_item_updatedata_sub_timelist_currenttime_textview );
			RelativeLayout newLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_updatedata_sub_new_layout );			
			UpdateInfo updateInfo = getItem( position );

			String infoStr = updateInfo.name + getString( R.string.str_txt_updaterunner ) + updateInfo.point + " " + getString( R.string.str_txt_updatepass );
			String splitStr = getString( R.string.str_txt_split ) + " " + updateInfo.split;
			String currentTimeStr = getString( R.string.str_txt_currenttime ) + " " + updateInfo.currentTime;

			infoTextView.setText( infoStr );
			splitTextView.setText( splitStr );
			currentTimeTextView.setText( currentTimeStr );

			// New 表示
			if( updateInfo.recentFlg )
			{
				newLayout.setVisibility( View.VISIBLE );
			}
			else
			{
				newLayout.setVisibility( View.GONE );
			}

			return convertView;
		}
	}
}
