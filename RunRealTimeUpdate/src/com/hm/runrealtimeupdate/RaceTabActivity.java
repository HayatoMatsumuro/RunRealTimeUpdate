package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * 大会画面のTabActivity
 * @author Hayato Matsumuro
 *
 */
public class RaceTabActivity extends TabActivity
{
	/**
	 * インテント  大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	/**
	 * インテント カレントタブ
	 */
	public static final String STR_INTENT_CURRENTTAB = "currenttab";

	/**
	 * インテント カレントタブ 詳細
	 */
	public static final int INT_INTENT_VAL_CURRENTTAB_DETAIL = 0;

	/**
	 * インテント カレントタブ 選手
	 */
	public static final int INT_INTENT_VAL_CURRENTTAB_RUNNER = 1;

	/**
	 * インテント カレントタブ 更新
	 */
	public static final int INT_INTENT_VAL_CURRENTTAB_UPDATE = 2;

	/**
	 * インテント カレントタブ 地点
	 */
	public static final int INT_INTENT_VAL_CURRENTTAB_PASS = 3;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.tabactivity_race );

		// 大会情報取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		// 大会情報が取得できないなら、エラー画面
		if( raceInfo == null )
		{
			Intent intentErr = new Intent( RaceTabActivity.this, ErrorActivity.class );
			intentErr.putExtra( ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。" );
			return;
		}

		// カレントタブ取得
		int currentTab = intent.getIntExtra( STR_INTENT_CURRENTTAB, INT_INTENT_VAL_CURRENTTAB_DETAIL );

		// 速報中テキスト
		int visibility = View.INVISIBLE;
		if( raceInfo.getRaceUpdate() == RaceInfo.INT_RACEUPDATE_OFF )
		{
			visibility = View.VISIBLE;
		}
		else
		{
			// TODO:予約中のときの表示
			visibility = View.GONE;
		}
		setVisibilityUpdateExe( visibility );

		// 大会一覧ボタン
		Button raceListButton = ( Button )findViewById( R.id.id_tabactivity_race_header_racelist_button );
		raceListButton.setOnClickListener(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// メイン画面遷移
					Intent intent = new Intent( RaceTabActivity.this, MainActivity.class );
					startActivity( intent );
				}
			}
		);

		// 選手登録ボタン
		Button runnerEntryButton = ( Button )findViewById( R.id.id_tabactivity_race_header_runnerentry_button );
		runnerEntryButton.setTag( raceId );
		runnerEntryButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					String raceId = ( String )v.getTag();

					TabHost tabHost = getTabHost();
					int currentTab = tabHost.getCurrentTab();

					// 選手登録画面遷移
					Intent intent = new Intent( RaceTabActivity.this, RunnerEntryActivity.class );
					intent.putExtra( RunnerEntryActivity.STR_INTENT_RACEID, raceId );
					intent.putExtra( RunnerEntryActivity.STR_INTENT_CURRENTTAB, currentTab );
					startActivity(intent);		
				}
			}
		);

		// タブ設定
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent tabIntent;

		// 大会詳細
		tabIntent = new Intent( this, RaceDetailActivity.class );
		tabIntent.putExtra( RaceDetailActivity.STR_INTENT_RACEID, raceId );
		spec = tabHost.newTabSpec( getString( R.string.str_tab_race_detail ) ).setIndicator( getString( R.string.str_tab_race_detail ) ).setContent( tabIntent );
		tabHost.addTab( spec );

		// 選手リスト
		tabIntent = new Intent( this, RunnerListActivity.class );
		tabIntent.putExtra( RunnerListActivity.STR_INTENT_RACEID, raceId );
		spec = tabHost.newTabSpec( getString( R.string.str_tab_race_runner ) ).setIndicator( getString( R.string.str_tab_race_runner ) ).setContent( tabIntent );
		tabHost.addTab( spec );

		// 速報リスト
		tabIntent = new Intent( this, UpdateListActivity.class );
		tabIntent.putExtra( UpdateListActivity.STR_INTENT_RACEID, raceId );
		spec = tabHost.newTabSpec( getString( R.string.str_tab_race_update ) ).setIndicator( getString( R.string.str_tab_race_update ) ).setContent( tabIntent );
		tabHost.addTab( spec );

		// 地点情報
		tabIntent = new Intent( this, PassListActivity.class );
		tabIntent.putExtra( PassListActivity.STR_INTENT_RACEID, raceId );
		spec = tabHost.newTabSpec( getString( R.string.str_tab_race_pass ) ).setIndicator( getString( R.string.str_tab_race_pass ) ).setContent( tabIntent );
		tabHost.addTab( spec );

		tabHost.setCurrentTab( currentTab );

		return;
	}

	/**
	 * 速報中テキストの表示状態設定
	 * @param visibility　表示状態
	 */
	public void setVisibilityUpdateExe( int visibility )
	{
		TextView updateExeTextView = ( TextView )findViewById( R.id.id_tabactivity_race_updateexe_textview );
		updateExeTextView.setVisibility( visibility );
		return;
	}
}
