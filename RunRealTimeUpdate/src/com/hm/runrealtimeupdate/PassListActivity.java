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
	public static final String STR_INTENT_RACEID = "raceid";

	/**
	 * NEWの表示時間( ms )
	 */
	private static final long LONG_RESENT_TIME = 300000;

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

					if( PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER.equals( element.getSts() ) )
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

		builder.append( element.getNumber() );
		builder.append( "\n" );
		builder.append( element.getName() );
		builder.append( "\n\n" );
		builder.append( element.getPoint() );
		builder.append( "\n\n" );
		builder.append( getString( R.string.str_dialog_msg_split ) );
		builder.append( element.getSplit() );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_lap ) );
		builder.append( element.getLap() );
		builder.append( "\n" );
		builder.append( getString( R.string.str_dialog_msg_currenttime ) );
		builder.append( element.getCurrentTime() );

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
		List<PassRunnerInfo> passRunnerInfoList = Logic.getPassRunnerInfoList( getContentResolver(), raceId, LONG_RESENT_TIME );

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
				element.setSts( PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION );
				element.setSection( passRunnerInfo.getSection() );
				passPointList.add( element );

				for( PassRunnerInfo.PassPointInfo passPointInfo:passRunnerInfo.getPassPointInfo() )
				{
					// 地点
					PassPointListElement pElement = new PassPointListElement();
					pElement.setSts( PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT );
					pElement.setPoint( passPointInfo.getPoint() );
					passPointList.add( pElement );

					// 選手
					for( PassRunnerInfo.PassPointInfo.PassPointRunnerInfo passPointRunnerInfo : passPointInfo.getPassPointRunnerInfoList() )
					{
						PassPointListElement rElement = new PassPointListElement();
						rElement.setSts( PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER );
						rElement.setName( passPointRunnerInfo.getName() );
						rElement.setNumber( passPointRunnerInfo.getNumber() );
						rElement.setPoint( passPointInfo.getPoint() );
						rElement.setSplit( passPointRunnerInfo.getSplit() );
						rElement.setLap( passPointRunnerInfo.getLap() );
						rElement.setCurrentTime( passPointRunnerInfo.getCurrentTime() );
						rElement.setRecentFlg( passPointRunnerInfo.isRecentFlg() );
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
		public PassPointAdapter( Context context, List<PassPointListElement> passPointListElement )
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

			if( element.getSts().equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION ) )
			{
				// 部門表示
				sectionLayout.setVisibility( View.VISIBLE );
				pointLayout.setVisibility( View.GONE );
				runnerInfoLayout.setVisibility( View.GONE );

				sectionTextView.setText( element.getSection() );
			}
			else if( element.getSts().equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT ) )
			{
				// 地点情報表示
				sectionLayout.setVisibility( View.GONE );
				pointLayout.setVisibility( View.VISIBLE );
				runnerInfoLayout.setVisibility( View.GONE );

				pointTextView.setText( element.getPoint() );
			}
			else
			{
				// ランナー情報表示
				sectionLayout.setVisibility( View.GONE );
				pointLayout.setVisibility( View.GONE );
				runnerInfoLayout.setVisibility( View.VISIBLE );

				nameTextView.setText( element.getName() );
				numberTextView.setText( element.getNumber() );
				splitTextView.setText( getString( R.string.str_txt_split ) + " " + element.getSplit() );
				currentTimeTextView.setText( getString( R.string.str_txt_currenttime ) + " " + element.getCurrentTime() );
				
				if( element.isRecentFlg() )
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
		public static final String STR_PASSPOINTLISTELEMENT_SECTION = "section";

		/**
		 * 通過地点リスト要素 地点
		 */
		public static final String STR_PASSPOINTLISTELEMENT_POINT = "point";

		/**
		 * 通過地点リスト要素 選手
		 */
		public static final String STR_PASSPOINTLISTELEMENT_RUNNER = "runner";

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
		private String lap;

		/**
		 * 現在の時刻
		 */
		private String currentTime;

		/**
		 * 最近の更新
		 */
		private boolean recentFlg;

		/**
		 * 状態を取得する
		 * @return 状態
		 */
		public String getSts()
		{
			return sts;
		}

		/**
		 * 状態を設定する
		 * @param sts 状態
		 */
		public void setSts( String sts )
		{
			this.sts = sts;
			return;
		}

		/**
		 * 部門を取得する
		 * @return 部門
		 */
		public String getSection()
		{
			return section;
		}

		/**
		 * 部門を設定する
		 * @param section 部門
		 */
		public void setSection( String section )
		{
			this.section = section;
			return;
		}

		/**
		 * 地点を取得する
		 * @return 地点
		 */
		public String getPoint()
		{
			return point;
		}

		/**
		 * 地点を設定する
		 * @param point 地点
		 */
		public void setPoint( String point )
		{
			this.point = point;
			return;
		}

		/**
		 * ゼッケン番号を取得する
		 * @return ゼッケン番号
		 */
		public String getNumber()
		{
			return number;
		}

		/**
		 * ゼッケン番号を設定する
		 * @param number ゼッケン番号
		 */
		public void setNumber( String number )
		{
			this.number = number;
			return;
		}

		/**
		 * 名前を取得する
		 * @return　名前
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * 名前を設定する
		 * @param name　名前
		 */
		public void setName( String name )
		{
			this.name = name;
			return;
		}

		/**
		 * スプリットを取得する
		 * @return スプリット
		 */
		public String getSplit()
		{
			return split;
		}

		/**
		 * スプリットを設定する
		 * @param split スプリット
		 */
		public void setSplit( String split )
		{
			this.split = split;
			return;
		}

		/**
		 * ラップを取得する
		 * @return ラップ
		 */
		public String getLap()
		{
			return lap;
		}

		/**
		 * ラップを設定する
		 * @param lap ラップ
		 */
		public void setLap( String lap )
		{
			this.lap = lap;
			return;
		}

		/**
		 * 現在の時刻を取得する
		 * @return 現在の時刻
		 */
		public String getCurrentTime()
		{
			return currentTime;
		}

		/**
		 * 現在の時刻を設定する
		 * @param currentTime 現在の時刻
		 */
		public void setCurrentTime( String currentTime )
		{
			this.currentTime = currentTime;
			return;
		}

		/**
		 * 最近の更新の判定をする
		 * @return true:最近/false:最近でない
		 */
		public boolean isRecentFlg()
		{
			return recentFlg;
		}

		/**
		 * 最近の更新を設定する
		 * @param recentFlg true:最近/false:最近でない
		 */
		public void setRecentFlg( boolean recentFlg )
		{
			this.recentFlg = recentFlg;
			return;
		}
	}
}
