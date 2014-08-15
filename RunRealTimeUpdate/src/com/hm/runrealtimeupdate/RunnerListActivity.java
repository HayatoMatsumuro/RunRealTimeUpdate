package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;
import com.hm.runrealtimeupdate.logic.SectionRunnerInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
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

public class RunnerListActivity extends Activity {

	public static final String STR_ACTIVITY_ID = "runnerListActivity";
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	/**
	 * 登録できる選手の数
	 */
	//TODO:
	//private static int INT_RUNNER_NUM_MAX = 30;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//TODO: レイアウトが変。白の部分が多い
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runnerlist);
		
		// 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 選手リスト設定
        ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerlist_body_contents_runnerlist_listview);
        
        // 選手リストのアイテム長押し
        runnerInfoListView.setTag(raceInfo);
        runnerInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				// 大会情報取得
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getTag();
				
				// 選手情報取得
				SectionRunnerElement element = (SectionRunnerElement)listView.getItemAtPosition(position);
				RunnerInfo runnerInfo = element.getRunnerInfo();
				
				if( runnerInfo == null ){
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
						getString(R.string.str_dialog_msg_NG) );
				
				return true;
			}
        	
        });
        
        // 選手リストの短押し
        runnerInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				// 選手情報取得
				ListView listView = (ListView)parent;
				SectionRunnerElement element = (SectionRunnerElement)listView.getItemAtPosition(position);
				RunnerInfo runnerInfo = element.getRunnerInfo();
				
				// 選手以外のクリックは、無視
				if( runnerInfo == null ){
					return;
				}
				
				// ダイアログの中身生成
				LayoutInflater factory = LayoutInflater.from( getParent() );
				final View inputView = factory.inflate(R.layout.dialog_runnerinfodetail, null);
				
				// ゼッケン番号
		     	TextView numberTextView = (TextView)inputView.findViewById(R.id.id_dialog_runnerinfodetail_contents_number_textview);
		     	numberTextView.setText("No. " + runnerInfo.getNumber());
		     	
		     	// 選手名
		     	TextView nameTextView = (TextView)inputView.findViewById(R.id.id_dialog_runnerinfodetail_contents_name_textview);
		     	nameTextView.setText(runnerInfo.getName());
		     		
		     	// 部門
		     	TextView sectionTextView = (TextView)inputView.findViewById(R.id.id_dialog_runnerinfodetail_contents_section_textview);
		     	sectionTextView.setText(runnerInfo.getSection());
		     	
				
				// タイムリスト
				TableLayout tableLayout = (TableLayout)inputView.findViewById(R.id.id_dialog_runnerinfodetail_contents_timelist_layout);
				
				for( RunnerInfo.TimeList timelist : runnerInfo.getTimeList() ){
					TableRow tableRow = new TableRow( getParent() );
					
					// 地点
					TextView pointTextView = new TextView( getParent() );
					pointTextView.setText(timelist.getPoint());
					pointTextView.setTextColor(Color.WHITE);
					
					// スプリット
					TextView splitTextView = new TextView( getParent() );
					splitTextView.setText(timelist.getSplit());
					splitTextView.setGravity(Gravity.CENTER);
					splitTextView.setTextColor(Color.WHITE);
					
					// ラップ
					TextView lapTextView = new TextView( getParent() );
		    		lapTextView.setText(timelist.getLap());
		    		lapTextView.setGravity(Gravity.CENTER);
		    		lapTextView.setTextColor(Color.WHITE);
		    		
		    		// カレントタイム
		    		TextView currentTimeView = new TextView( getParent() );
		    		currentTimeView.setText(timelist.getCurrentTime());
		    		currentTimeView.setGravity(Gravity.CENTER);
		    		currentTimeView.setTextColor(Color.WHITE);
					
		    		tableRow.addView(pointTextView);
		    		tableRow.addView(splitTextView);
		    		tableRow.addView(lapTextView);
		    		tableRow.addView(currentTimeView);
		    		
		    		tableLayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder( getParent() );
				dialog.setView(inputView);
				dialog.setPositiveButton(getString(R.string.str_btn_close), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自動生成されたメソッド・スタブ
						
					}
				});
				
				dialog.show();
			}
        	
		});
        
		return;
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		Intent intent = getIntent();
		String raceId = intent.getStringExtra(STR_INTENT_RACEID);
		
		// レイアウト
		RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerlist_body_contents_layout );
		RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerlist_body_message_layout );
		
		// 部門別選手リストの取得
		List<SectionRunnerInfo> sectionRunnerInfoList = Logic.getSectionRunnerInfo(getContentResolver(), raceId, getString(R.string.str_txt_section_no));
		
		if( sectionRunnerInfoList.isEmpty() ){
			// 選手情報なし
			contentsLayout.setVisibility( View.GONE );
			messageLayout.setVisibility( View.VISIBLE );
		}else{
			
			// 選手情報なし
			contentsLayout.setVisibility( View.VISIBLE );
			messageLayout.setVisibility( View.GONE );
						
			List<SectionRunnerElement> sectionRunnerElementList = createSectionRunnerElementList( sectionRunnerInfoList );
			
			// リストビュー更新
			ListView runnerInfoListView = ( ListView )findViewById( R.id.id_activity_runnerlist_body_contents_runnerlist_listview );
			RunnerListAdapter adapter = ( RunnerListAdapter )runnerInfoListView.getAdapter();
			
			if( adapter != null ){
				adapter.clear();
			}
			
			adapter = new RunnerListAdapter( this, sectionRunnerElementList );
	        runnerInfoListView.setAdapter( adapter );
		}
	}
	
	/**
	 * ダイアログのメッセージを作成する
	 * @param runnerInfoItem
	 * @return
	 */
	private String createDialogMessage( RunnerInfo runnerInfo ){
		StringBuilder builder = new StringBuilder();
		builder.append(runnerInfo.getName());
		builder.append("\n");
		builder.append(runnerInfo.getNumber());
		builder.append("\n");
		builder.append(runnerInfo.getSection());
		
		return builder.toString();
	}
	
	/**
	 * リストビュー用の選手一覧のリストを作成する
	 * @return　リストビュー用の選手一覧
	 */
	private List<SectionRunnerElement> createSectionRunnerElementList( List<SectionRunnerInfo> sectionRunnerInfoList ){
		
        // 表示用の部門別の選手情報設定
        List<SectionRunnerElement> sectionRunnerElementList = new ArrayList<SectionRunnerElement>();
        for( SectionRunnerInfo sectionRunnerInfo : sectionRunnerInfoList ){
        	
        	SectionRunnerElement sectionElement = new SectionRunnerElement();
        	sectionElement.setSection(sectionRunnerInfo.getSection());
        	sectionRunnerElementList.add(sectionElement);
        	
        	for( RunnerInfo runnerInfo : sectionRunnerInfo.getRunnerInfoList() ){
        		
        		SectionRunnerElement runnerElement = new SectionRunnerElement();
        		runnerElement.setRunnerInfo(runnerInfo);
        		sectionRunnerElementList.add(runnerElement);
        	}
        }
        
        return sectionRunnerElementList;
	}
	
	/**
	 * リストビュー用の選手一覧から選手数を取得する
	 * @param sectionRunnerElementList　リストビュー用の選手一覧
	 * @return　選手数
	 */
	//TODO: 選手登録できるかどうかの判定は、登録画面で行う
	/*
	private int getAllSectionRunner( List<SectionRunnerElement> sectionRunnerElementList ){
		
		int num = 0;
		
		for( SectionRunnerElement element : sectionRunnerElementList ){
			if( element.getSection() == null){
				num++;
			}
		}
		return num;
	}*/
	
	/**
	 * 選手削除ダイアログのボタン押しコールバック
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerDeleteButtonCallbackImpl implements InfoDialog.ButtonCallback<RunnerDeleteInfo>{

		@Override
		public void onClickPositiveButton(DialogInterface dialog, int which, RunnerDeleteInfo info) {
			// ポジティブボタン押し
			// 速報中でないなら削除
			RaceInfo raceInfo = info.getRaceInfo();
			SectionRunnerElement element = info.getSectionRunnerElement();
			if( !raceInfo.isRaceUpdate()){
				// 選手削除
    			Logic.deleteRunnerInfo( getContentResolver(), raceInfo.getRaceId(), element.getRunnerInfo().getNumber() );
    				
    			// 表示リストを更新する
    			ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerlist_body_contents_runnerlist_listview);
    			RunnerListAdapter adapter = (RunnerListAdapter)runnerInfoListView.getAdapter();
    			adapter.remove( element );
    			adapter.notifyDataSetChanged();
    			
    			Toast.makeText(RunnerListActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(RunnerListActivity.this, "速報中は削除できません", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onClickNegativeButton(DialogInterface dialog, int which, RunnerDeleteInfo info) {
			// 何もしない
		}
		
	}
	
	private class RunnerDeleteInfo{
		
		/**
		 * 大会情報
		 */
		private RaceInfo raceInfo;

		/**
		 * リストビューの要素
		 */
		private SectionRunnerElement sectionRunnerElement;
		
		public RaceInfo getRaceInfo() {
			return raceInfo;
		}

		public void setRaceInfo(RaceInfo raceInfo) {
			this.raceInfo = raceInfo;
		}

		public SectionRunnerElement getSectionRunnerElement() {
			return sectionRunnerElement;
		}

		public void setSectionRunnerElement(SectionRunnerElement sectionRunnerElement) {
			this.sectionRunnerElement = sectionRunnerElement;
		}
	}
	
	
	/**
	 * ランナーリストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerListAdapter extends ArrayAdapter<SectionRunnerElement>{

		LayoutInflater inflater;
    	
		public RunnerListAdapter(Context context, List<SectionRunnerElement> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_runnerinfo, parent, false);
			}
			
			RelativeLayout sectionLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_runnerinfo_section_layout );
			RelativeLayout runnerLayout = (RelativeLayout)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_layout );
			
			TextView runnerSectionTextView = (TextView)convertView.findViewById( R.id.id_list_item_runnerinfo_section_textview );
			TextView runnerNameTextView = (TextView)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_name_textview );
			TextView runnerNumberTextView = (TextView)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_number_textview );
			
			SectionRunnerElement item = getItem(position);
			
			// 部門
			if( item.getSection() != null ){
				sectionLayout.setVisibility( View.VISIBLE );
				runnerLayout.setVisibility( View.GONE );
				
				runnerSectionTextView.setText( item.getSection() );
				
			}else{
				sectionLayout.setVisibility( View.GONE );
				runnerLayout.setVisibility( View.VISIBLE );
				runnerNameTextView.setText( item.runnerInfo.getName() );
				runnerNumberTextView.setText( item.runnerInfo.getNumber() );
			}
			
			return convertView;
		}
		
	}
	
	private class SectionRunnerElement {
		
		private String section = null;
		
		private RunnerInfo runnerInfo = null;
		
		public String getSection() {
			return section;
		}

		public void setSection(String section) {
			this.section = section;
		}

		public RunnerInfo getRunnerInfo() {
			return runnerInfo;
		}

		public void setRunnerInfo(RunnerInfo runnerInfo) {
			this.runnerInfo = runnerInfo;
		}

	}
}
