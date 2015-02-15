package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.PassRunnerInfo;
import com.hm.runrealtimeupdate.logic.RaceInfo;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 地点リストのActivity
 * @author Hayato Matsumuro
 *
 */
public class PassListActivity extends Activity
{
	/**
	 * インテント 大会ID
	 */
	static final String STR_INTENT_RACEID = "raceid";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_passlist );

		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );

		// 大会情報が取得できないなら、エラー画面
		if( ( raceId == null ) || ( raceId.equals( "" ) ) )
		{
			Intent intentErr = new Intent( PassListActivity.this, ErrorActivity.class );
			intentErr.putExtra( ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。" );
			return;
		}

		// リストの選手情報タッチ
		ListView listView = ( ListView )findViewById( R.id.id_activity_passlist_body_contents_passlist_listview );
		listView.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View v, int position, long id )
				{
					ListView listView = ( ListView )parent;
					PassPointListElement element = ( PassPointListElement )listView.getItemAtPosition( position );

					if( PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER.equals( element.sts ) )
					{
						AlertDialog.Builder dialog = new AlertDialog.Builder( PassListActivity.this );
						dialog.setTitle( getString( R.string.str_dialog_title_updatedetail ) );
						dialog.setMessage( createDialogMessage( element ) );
						dialog.setNegativeButton
						(
							getString( R.string.str_dialog_msg_close ),
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
					}

					return;
				}
			}
		);

		return;
	}

	/**
	 * ダイアログメッセージを作成する
	 * @param element 地点情報リスト要素
	 * @return メッセージ
	 */
	private String createDialogMessage( PassPointListElement element )
	{
		StringBuilder builder = new StringBuilder();

		builder.append( element.number );
		builder.append( "\n" );
		builder.append( element.name );
		builder.append( "\n\n" );
		builder.append( element.point );
		builder.append( "\n\n" );
		builder.append( getString( R.string.str_dialog_msg_split ) );
		builder.append( element.split );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_lap ) );
		builder.append( element.lap );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_currenttime ) );
		builder.append( element.currentTime );

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

		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_passlist_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_passlist_body_message_layout );

		// 地点通過情報取得
		List<PassRunnerInfo> passRunnerInfoList = Logic.getPassRunnerInfoList( getContentResolver(), raceId, Common.LONG_RESENT_TIME );

		if( passRunnerInfoList.isEmpty() )
		{
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}
		else
		{
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );

			List<PassPointListElement> passPointList = new ArrayList<PassPointListElement>();

			for( PassRunnerInfo passRunnerInfo : passRunnerInfoList )
			{
				// 部門
				PassPointListElement element = new PassPointListElement();
				element.sts = PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION;
				element.section = passRunnerInfo.getSection();
				passPointList.add( element );

				for( PassRunnerInfo.PassPointInfo passPointInfo:passRunnerInfo.getPassPointInfo() )
				{
					// 地点
					PassPointListElement pElement = new PassPointListElement();
					pElement.sts = PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT;
					pElement.point = passPointInfo.getPoint();
					passPointList.add( pElement );

					// 選手
					for( PassRunnerInfo.PassPointInfo.PassPointRunnerInfo passPointRunnerInfo : passPointInfo.getPassPointRunnerInfoList() )
					{
						PassPointListElement rElement = new PassPointListElement();
						rElement.sts = PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER;
						rElement.name = passPointRunnerInfo.getName();
						rElement.number = passPointRunnerInfo.getNumber();
						rElement.point = passPointInfo.getPoint();
						rElement.split = passPointRunnerInfo.getSplit();
						rElement.lap = passPointRunnerInfo.getLap();
						rElement.currentTime = passPointRunnerInfo.getCurrentTime();
						rElement.recentFlg = passPointRunnerInfo.isRecentFlg();
						passPointList.add( rElement );
					}
				}
			}

			// リストビューの設定
			ListView listView = ( ListView )findViewById( R.id.id_activity_passlist_body_contents_passlist_listview );
			PassPointAdapter adapter = ( PassPointAdapter )listView.getAdapter();

			if( adapter != null )
			{
				adapter.clear();
			}

			adapter = new PassPointAdapter( this, passPointList );
			listView.setAdapter( adapter );
		}

		// 速報バーの表示更新
		( ( RaceTabActivity )getParent() ).setDispUpdateBar( raceInfo.updateSts );

		return;
	}

	/**
	 * 通過地点アダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class PassPointAdapter extends ArrayAdapter<PassPointListElement>
	{
		LayoutInflater m_Inflater;

		/**
		 * コンストラクタ
		 * @param context コンテキスト
		 * @param passPointListElement 通過地点リスト要素
		 */
		private PassPointAdapter( Context context, List<PassPointListElement> passPointListElement )
		{
			super( context, 0, passPointListElement );

			m_Inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			return;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null )
			{
				convertView = m_Inflater.inflate( R.layout.list_item_passinfo, parent, false );
			}

			RelativeLayout sectionLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_passinfo_section_layout );
			RelativeLayout pointLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_passinfo_point_layout );
			LinearLayout runnerInfoLayout = ( LinearLayout )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_layout );

			TextView sectionTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_section_textview );
			TextView pointTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_point_textview );
			TextView nameTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_runner_name_textview );
			TextView numberTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_runner_number_textview );
			TextView splitTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_sub_timelist_split_textview );
			TextView currentTimeTextView = ( TextView )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_sub_timelist_currenttime_textview );

			RelativeLayout newLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_passinfo_runnerinfo_sub_new_layout );
			PassPointListElement element = getItem( position );

			if( element.sts.equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION ) )
			{
				// 部門表示
				sectionLayout.setVisibility( View.VISIBLE );
				pointLayout.setVisibility( View.GONE );
				runnerInfoLayout.setVisibility( View.GONE );

				sectionTextView.setText( element.section );
			}
			else if( element.sts.equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT ) )
			{
				// 地点情報表示
				sectionLayout.setVisibility( View.GONE );
				pointLayout.setVisibility( View.VISIBLE );
				runnerInfoLayout.setVisibility( View.GONE );

				pointTextView.setText( element.point );
			}
			else
			{
				// ランナー情報表示
				sectionLayout.setVisibility( View.GONE );
				pointLayout.setVisibility( View.GONE );
				runnerInfoLayout.setVisibility( View.VISIBLE );

				nameTextView.setText( element.name );
				numberTextView.setText( element.number );
				splitTextView.setText( getString( R.string.str_txt_split ) + " " + element.split );
				currentTimeTextView.setText( getString( R.string.str_txt_currenttime ) + " " + element.currentTime );
				
				if( element.recentFlg )
				{
					newLayout.setVisibility( View.VISIBLE );
				}
				else
				{
					newLayout.setVisibility( View.GONE );
				}
			}

			return convertView;
		}
	}

	/**
	 * 通過地点リスト要素
	 * @author Hayato Matsumuro
	 *
	 */
	private class PassPointListElement
	{
		/**
		 * 通過地点リスト要素 部門
		 */
		private static final String STR_PASSPOINTLISTELEMENT_SECTION = "section";

		/**
		 * 通過地点リスト要素 地点
		 */
		private static final String STR_PASSPOINTLISTELEMENT_POINT = "point";

		/**
		 * 通過地点リスト要素 選手
		 */
		private static final String STR_PASSPOINTLISTELEMENT_RUNNER = "runner";

		/**
		 * 通過地点リスト要素 状態
		 */
		private String sts;

		/**
		 * 部門
		 */
		private String section;

		/**
		 * 地点
		 */
		private String point;

		/**
		 * ゼッケン番号
		 */
		private String number;

		/**
		 * 名前
		 */
		private String name;

		/**
		 * スプリット
		 */
		private String split;

		/**
		 * ラップ
		 */
		public String lap;

		/**
		 * 現在の時刻
		 */
		private String currentTime;

		/**
		 * 最近の更新
		 */
		private boolean recentFlg;
	}
}
