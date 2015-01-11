package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;
import com.hm.runrealtimeupdate.logic.SectionRunnerInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 選手リスト画面のActivity
 * @author Hayato Matsumuro
 *
 */
public class RunnerListActivity extends Activity
{
	/**
	 * インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_runnerlist );

		// 選手リスト設定
		ListView runnerInfoListView = ( ListView )findViewById( R.id.id_activity_runnerlist_body_contents_runnerlist_listview );

		// 選手リストのアイテム長押し
		runnerInfoListView.setOnItemLongClickListener(
			new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick( AdapterView<?> parent, View v, int position, long id )
				{
					// 大会情報取得
					ListView listView = ( ListView )parent;
					RaceInfo raceInfo = ( RaceInfo )listView.getTag();

					// 選手情報取得
					SectionRunnerElement element = ( SectionRunnerElement )listView.getItemAtPosition( position );
					RunnerInfo runnerInfo = element.getRunnerInfo();

					if( runnerInfo == null )
					{
						return true;
					}

					// 削除ダイアログ表示
					RunnerDeleteInfo runnerDeleteInfo = new RunnerDeleteInfo();
					runnerDeleteInfo.setRaceInfo(raceInfo);
					runnerDeleteInfo.setSectionRunnerElement(element);

					InfoDialog<RunnerDeleteInfo> runnerDeleteDialog = new InfoDialog<RunnerDeleteInfo>( runnerDeleteInfo, new RunnerDeleteButtonCallbackImpl() );
					runnerDeleteDialog.onDialog(
						getParent(),
						getString( R.string.str_dialog_title_deleterunner ),
						createDialogMessage( runnerInfo ),
						getString( R.string.str_dialog_msg_DEL ),
						getString( R.string.str_dialog_msg_NG )
					);

					return true;
				}
			}
		);

		// 選手リストの短押し
		runnerInfoListView.setOnItemClickListener(
			new OnItemClickListener()
			{
				@SuppressLint("InflateParams")
				@Override
				public void onItemClick( AdapterView<?> parent, View v, int position, long id )
				{
					// 選手情報取得
					ListView listView = ( ListView )parent;
					SectionRunnerElement element = ( SectionRunnerElement )listView.getItemAtPosition( position );
					RunnerInfo runnerInfo = element.getRunnerInfo();

					// 選手以外のクリックは、無視
					if( runnerInfo == null )
					{
						return;
					}

					// ダイアログの中身生成
					// TODO: inflate の引数がnullだと警告が発生。暫定対策
					LayoutInflater factory = LayoutInflater.from( getParent() );
					final View inputView = factory.inflate( R.layout.dialog_runnerinfodetail, null );

					// ゼッケン番号
					TextView numberTextView = ( TextView )inputView.findViewById( R.id.id_dialog_runnerinfodetail_number_text_textview );
					numberTextView.setText( runnerInfo.number );

					// 選手名
					TextView nameTextView = ( TextView )inputView.findViewById( R.id.id_dialog_runnerinfodetail_name_text_textview );
					nameTextView.setText( runnerInfo.name );

					// 部門
					TextView sectionTextView = ( TextView )inputView.findViewById( R.id.id_dialog_runnerinfodetail_section_text_textview );
					sectionTextView.setText( runnerInfo.section );

					// タイムリスト
					TableLayout tableLayout = ( TableLayout )inputView.findViewById( R.id.id_dialog_runnerinfodetail_timelist_layout );

					for( RunnerInfo.TimeInfo timeInfo : runnerInfo.timeInfoList )
					{
						TableRow tableRow = new TableRow( getParent() );

						// 地点
						TextView pointTextView = new TextView( getParent() );
						pointTextView.setText( timeInfo.point );
						pointTextView.setPadding( 1, 1, 1, 1 );

						// スプリット
						TextView splitTextView = new TextView( getParent() );
						splitTextView.setText( timeInfo.split );
						splitTextView.setGravity( Gravity.CENTER );
						splitTextView.setPadding( 1, 1, 1, 1 );

						// ラップ
						TextView lapTextView = new TextView( getParent() );
						lapTextView.setText( timeInfo.lap );
						lapTextView.setGravity( Gravity.CENTER );
						lapTextView.setPadding( 1, 1, 1, 1 );

						// カレントタイム
						TextView currentTimeView = new TextView( getParent() );
						currentTimeView.setText( timeInfo.currentTime );
						currentTimeView.setGravity( Gravity.CENTER );
						lapTextView.setPadding( 1, 1, 1, 1 );

						tableRow.addView( pointTextView );
						tableRow.addView( splitTextView );
						tableRow.addView( lapTextView );
						tableRow.addView( currentTimeView );

						tableLayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
					}

					// ダイアログ表示
					AlertDialog.Builder dialog = new AlertDialog.Builder( getParent() );
					dialog.setView( inputView );
					dialog.setPositiveButton(
						getString( R.string.str_btn_close ),
						new OnClickListener()
						{
							@Override
							public void onClick( DialogInterface dialog, int which )
							{
								// 何もしない
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

	@Override
	protected void onResume()
	{
		super.onResume();

		// 大会ID取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		 // 選手リスト設定
		ListView runnerInfoListView = ( ListView )findViewById( R.id.id_activity_runnerlist_body_contents_runnerlist_listview );
		runnerInfoListView.setTag( raceInfo );

		setViewBody( raceId );

		// 速報バーの表示更新
		( ( RaceTabActivity )getParent() ).setDispUpdateBar( raceInfo.updateSts );

		return;
	}

	private void setViewBody( String raceId )
	{
		// 部門別選手リストの取得
		List<SectionRunnerInfo> sectionRunnerInfoList = Logic.getSectionRunnerInfo( getContentResolver(), raceId, getString( R.string.str_txt_section_no ) );

		// 選手リストまたは選手未登録メッセージの設定
		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerlist_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerlist_body_message_layout );

		if( !sectionRunnerInfoList.isEmpty() )
		{
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );

			// 選手情報あり
			List<SectionRunnerElement> sectionRunnerElementList = createSectionRunnerElementList( sectionRunnerInfoList );

			// リストビュー更新
			ListView runnerInfoListView = ( ListView )findViewById( R.id.id_activity_runnerlist_body_contents_runnerlist_listview );
			RunnerListAdapter adapter = ( RunnerListAdapter )runnerInfoListView.getAdapter();

			if( adapter != null )
			{
				adapter.clear();
			}

			adapter = new RunnerListAdapter( this, sectionRunnerElementList );
		    runnerInfoListView.setAdapter( adapter );

			adapter.notifyDataSetChanged();
		}
		else
		{
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}

		return;
	}

	/**
	 * ダイアログのメッセージを作成する
	 * @param runnerInfoItem
	 * @return
	 */
	private String createDialogMessage( RunnerInfo runnerInfo )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( runnerInfo.name );
		builder.append( "\n" );
		builder.append( runnerInfo.number );
		builder.append( "\n" );
		builder.append( runnerInfo.section );

		return builder.toString();
	}

	/**
	 * リストビュー用の選手一覧のリストを作成する
	 * @return　リストビュー用の選手一覧
	 */
	private List<SectionRunnerElement> createSectionRunnerElementList( List<SectionRunnerInfo> sectionRunnerInfoList )
	{
		// 表示用の部門別の選手情報設定
		List<SectionRunnerElement> sectionRunnerElementList = new ArrayList<SectionRunnerElement>();
		for( SectionRunnerInfo sectionRunnerInfo : sectionRunnerInfoList )
		{
			SectionRunnerElement sectionElement = new SectionRunnerElement();
			sectionElement.setSection( sectionRunnerInfo.section );
			sectionRunnerElementList.add( sectionElement );

			for( RunnerInfo runnerInfo : sectionRunnerInfo.runnerInfoList )
			{
				SectionRunnerElement runnerElement = new SectionRunnerElement();
				runnerElement.setRunnerInfo( runnerInfo );
				sectionRunnerElementList.add( runnerElement );
			}
		}

        return sectionRunnerElementList;
	}

	/**
	 * 選手削除ダイアログのボタン押しコールバック
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerDeleteButtonCallbackImpl implements InfoDialog.ButtonCallback<RunnerDeleteInfo>{

		@Override
		public void onClickPositiveButton( DialogInterface dialog, int which, RunnerDeleteInfo info )
		{
			// ポジティブボタン押し
			// 速報中でないなら削除
			RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), info.getRaceInfo().id );
			SectionRunnerElement element = info.getSectionRunnerElement();
			if( raceInfo.updateSts == RaceInfo.INT_UPDATESTS_ON )
			{
				Toast.makeText( RunnerListActivity.this, "速報中は削除できません", Toast.LENGTH_SHORT ).show();
			}
			else
			{
				// 選手削除
				Logic.deleteRunnerInfo( getContentResolver(), raceInfo.id, element.getRunnerInfo().number );

				setViewBody( raceInfo.id );

				Toast.makeText( RunnerListActivity.this, "削除しました", Toast.LENGTH_SHORT ).show();
			}

			return;
		}

		@Override
		public void onClickNegativeButton( DialogInterface dialog, int which, RunnerDeleteInfo info )
		{
			// 何もしない
			return;
		}
	}

	/**
	 * 選手削除情報
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerDeleteInfo
	{
		/**
		 * 大会情報
		 */
		private RaceInfo raceInfo;

		/**
		 * リストビューの要素
		 */
		private SectionRunnerElement sectionRunnerElement;

		/**
		 * 大会情報を取得する
		 * @return 大会情報
		 */
		public RaceInfo getRaceInfo()
		{
			return raceInfo;
		}

		/**
		 * 大会情報を設定する
		 * @param raceInfo 大会情報
		 */
		public void setRaceInfo(RaceInfo raceInfo)
		{
			this.raceInfo = raceInfo;
			return;
		}

		/**
		 * 部門選手要素を取得する
		 * @return 部門選手要素
		 */
		public SectionRunnerElement getSectionRunnerElement()
		{
			return sectionRunnerElement;
		}

		/**
		 * 部門選手要素を設定する
		 * @param sectionRunnerElement 部門選手要素
		 */
		public void setSectionRunnerElement( SectionRunnerElement sectionRunnerElement )
		{
			this.sectionRunnerElement = sectionRunnerElement;
			return;
		}
	}

	/**
	 * ランナーリストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerListAdapter extends ArrayAdapter<SectionRunnerElement>
	{

		/**
		 * レイアウトインフライヤー
		 */
		LayoutInflater m_Inflater;

		/**
		 * コンストラクタ
		 * @param context コンテキスト
		 * @param sectionRunnerElement 部門選手要素
		 */
		public RunnerListAdapter( Context context, List<SectionRunnerElement> sectionRunnerElement )
		{
			super( context, 0, sectionRunnerElement );

			this.m_Inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			return;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null )
			{
				convertView = this.m_Inflater.inflate( R.layout.list_item_runnerinfo, parent, false );
			}

			RelativeLayout sectionLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_runnerinfo_section_layout );
			RelativeLayout runnerLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_runnerinfo_runner_layout );

			TextView runnerSectionTextView = ( TextView )convertView.findViewById( R.id.id_list_item_runnerinfo_section_textview );
			TextView runnerNameTextView = ( TextView )convertView.findViewById( R.id.id_list_item_runnerinfo_runner_name_textview );
			TextView runnerNumberTextView = ( TextView )convertView.findViewById( R.id.id_list_item_runnerinfo_runner_number_textview );

			SectionRunnerElement item = getItem( position );

			// 部門
			if( item.getSection() != null )
			{
				sectionLayout.setVisibility( View.VISIBLE );
				runnerLayout.setVisibility( View.GONE );

				runnerSectionTextView.setText( item.getSection() );
			}
			else
			{
				sectionLayout.setVisibility( View.GONE );
				runnerLayout.setVisibility( View.VISIBLE );
				runnerNameTextView.setText( item.runnerInfo.name );
				runnerNumberTextView.setText( item.runnerInfo.number );
			}

			return convertView;
		}
	}

	/**
	 * 部門選手要素
	 * @author Hayato Matsumuro
	 *
	 */
	private class SectionRunnerElement
	{
		/**
		 * 部門
		 */
		private String section = null;

		/**
		 * 選手情報
		 */
		private RunnerInfo runnerInfo = null;

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
		 * 選手情報を取得する
		 * @return 選手情報
		 */
		public RunnerInfo getRunnerInfo()
		{
			return runnerInfo;
		}

		/**
		 * 選手情報を設定する
		 * @param runnerInfo 選手情報
		 */
		public void setRunnerInfo( RunnerInfo runnerInfo )
		{
			this.runnerInfo = runnerInfo;
			return;
		}
	}
}
