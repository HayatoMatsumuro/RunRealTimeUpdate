package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;
import com.hm.runrealtimeupdate.logic.SectionRunnerInfo;

import android.app.Activity;
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
        ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerlist_runnerlist_listview);
        
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
				
				// 大会情報取得
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getTag();
				
				// 選手情報取得
				SectionRunnerElement element = (SectionRunnerElement)listView.getItemAtPosition(position);
				if( element.getRunnerInfo() == null ){
					return;
				}
				
				(( RunnerActivityGroup )getParent()).showRunnerInfoDetailActivity( raceInfo.getRaceId(), element.getRunnerInfo().getNumber() );
			}
        	
		});
        
		return;
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		Intent intent = getIntent();
		String raceId = intent.getStringExtra(STR_INTENT_RACEID);
		// リストビュー更新
		ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerlist_runnerlist_listview);
		RunnerListAdapter adapter = (RunnerListAdapter)runnerInfoListView.getAdapter();
		
		if( adapter != null ){
			adapter.clear();
		}
		
		List<SectionRunnerElement> sectionRunnerElementList = createSectionRunnerElementList( raceId );
        adapter = new RunnerListAdapter(this, sectionRunnerElementList);
        runnerInfoListView.setAdapter(adapter);
        
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
	 * @param raceId 大会ID
	 * @return　リストビュー用の選手一覧
	 */
	private List<SectionRunnerElement> createSectionRunnerElementList( String raceId ){
		
		List<SectionRunnerInfo> sectionRunnerInfoList = Logic.getSectionRunnerInfo(getContentResolver(), raceId, getString(R.string.str_txt_section_no));
        
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
    			ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerlist_runnerlist_listview);
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
			
			TextView runnerNameTextView = (TextView)convertView.findViewById(R.id.id_list_item_runnerinfo_runner_name_textview);
			
			RelativeLayout runnerRelative = (RelativeLayout)convertView.findViewById(R.id.id_list_item_runnerinfo_runner_layout);
			TextView runnerNoTextView = (TextView)convertView.findViewById(R.id.id_list_item_runnerinfo_runner_number_textview);
			TextView runnerSectionTextView = (TextView)convertView.findViewById(R.id.id_list_item_runnerinfo_section_textview);
			
			SectionRunnerElement item = getItem(position);
			
			// 部門
			if( item.getSection() != null ){
				runnerSectionTextView.setText(item.getSection());
				runnerSectionTextView.setVisibility(View.VISIBLE);
				runnerRelative.setVisibility(View.GONE);
				
			}else{
				runnerNameTextView.setText(item.runnerInfo.getName());
				runnerNoTextView.setText(item.runnerInfo.getNumber());
				runnerSectionTextView.setVisibility(View.GONE);
				runnerRelative.setVisibility(View.VISIBLE);
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
