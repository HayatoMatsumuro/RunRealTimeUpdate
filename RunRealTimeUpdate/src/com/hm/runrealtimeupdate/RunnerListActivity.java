package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;
import com.hm.runrealtimeupdate.logic.SectionRunnerInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private static int INT_RUNNER_NUM_MAX = 30;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runnerlist);
		
		// 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 選手リスト設定
        ListView runnerInfoListView = (ListView)findViewById(R.id.id_runnerlist_listview_runner);
        
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
				
				// アダプタを取得する
				RunnerListAdapter adapter = ( RunnerListAdapter )listView.getAdapter();
				
				// 削除ダイアログ表示
				RunnerDeleteDialog dialog
						= new RunnerDeleteDialog(
								RunnerListActivity.this,
								getParent(),
								getContentResolver(),
								raceInfo,
								element,
								adapter,
								(Button)findViewById(R.id.id_runnerlist_btn_runnerentry));
				dialog.onDialog();
				
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
        
        // 選手登録ボタン
        Button runnerEntryButton = (Button)findViewById(R.id.id_runnerlist_btn_runnerentry);
        runnerEntryButton.setTag(raceInfo);
        runnerEntryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				
				if(!raceInfo.isRaceUpdate()){
					// 選手登録画面遷移
					(( RunnerActivityGroup )getParent()).showRunnerEntryActivity(raceInfo.getRaceId());
				}else{
					Toast.makeText(RunnerListActivity.this, "速報中は登録できません", Toast.LENGTH_SHORT).show();
				}
				
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
		ListView runnerInfoListView = (ListView)findViewById(R.id.id_runnerlist_listview_runner);
		RunnerListAdapter adapter = (RunnerListAdapter)runnerInfoListView.getAdapter();
		
		if( adapter != null ){
			adapter.clear();
		}
		
		List<SectionRunnerElement> sectionRunnerElementList = createSectionRunnerElementList( raceId );
        adapter = new RunnerListAdapter(this, sectionRunnerElementList);
        runnerInfoListView.setAdapter(adapter);
     
        // 選手登録ボタンのフォーカス設定
        Button runnerEntryButton = (Button)findViewById(R.id.id_runnerlist_btn_runnerentry);
        int runnerNum = getAllSectionRunner( sectionRunnerElementList );
        
        if( runnerNum >= INT_RUNNER_NUM_MAX){
           	runnerEntryButton.setEnabled(false);
        }else{
           	runnerEntryButton.setEnabled(true);
        }
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
	private int getAllSectionRunner( List<SectionRunnerElement> sectionRunnerElementList ){
		
		int num = 0;
		
		for( SectionRunnerElement element : sectionRunnerElementList ){
			if( element.getSection() == null){
				num++;
			}
		}
		return num;
	}
	
	/**
	 * 選手削除ダイアログ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerDeleteDialog{
		
		/**
    	 * コンテキスト
    	 */
		// TODO: 暫定
    	@SuppressWarnings("unused")
		private Context m_Context;
    	
    	/**
    	 * ダイアログのコンテキスト
    	 */
    	private Context m_DialogContext;
    	
    	/**
    	 * コンテントリゾルバ
    	 */
    	private ContentResolver m_ContentResolver;
    	
    	/**
    	 * 大会情報
    	 */
    	private RaceInfo m_RaceInfo;
    	
    	/**
    	 * 選手情報
    	 */
    	private SectionRunnerElement m_Element;
    	
    	/**
    	 * 選手情報アダプタ
    	 */
    	private RunnerListAdapter m_Adapter;
    	
    	/**
    	 * 選手登録のボタン
    	 */
    	private Button m_EntryButton;
    	
    	/**
    	 * コンストラクタ
    	 * @param context コンテキスト
    	 * @param dialogContext ダイアログのコンテキスト
    	 * @param contentResolver コンテントリゾルバ
    	 * @param raceInfo 大会情報
    	 * @param runnerInfo 選手情報
    	 * @param adapter 大会リストアダプタ
    	 * @param button 登録ボタンのリソースID
    	 */
    	RunnerDeleteDialog( Context context, Context dialogContext, ContentResolver contentResolver, RaceInfo raceInfo, SectionRunnerElement element, RunnerListAdapter adapter, Button button ){
    		
    		// 初期化
    		m_Context = context;
    		m_DialogContext = dialogContext;
    		m_ContentResolver = contentResolver;
    		m_RaceInfo = raceInfo;
    		m_Element = element;
    		m_Adapter = adapter;
    		m_EntryButton = button;
    		
    	}
    	
    	public void onDialog(){
    		// ダイアログ表示
    		AlertDialog.Builder dialog = new AlertDialog.Builder( m_DialogContext );
    		dialog.setTitle( getString( R.string.str_dialog_title_deleterunner ) );
    		dialog.setMessage( createDialogMessage( m_Element.getRunnerInfo() ) );
    		
    		// 削除するボタン
    		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {
    					
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				
    				// 速報中でないなら削除
    				if( !m_RaceInfo.isRaceUpdate()){
    					// 選手削除
            			Logic.deleteRunnerInfo( m_ContentResolver, m_RaceInfo.getRaceId(), m_Element.getRunnerInfo().getNumber() );
            				
            			// 表示リストを更新する
            			if( m_Adapter != null ){
                			m_Adapter.remove(m_Element);
                			m_Adapter.notifyDataSetChanged();
            			}
            				
            			// 削除したら選手登録はできるので、ボタンを有効にする
            			if( m_EntryButton != null ){
                			m_EntryButton.setEnabled(true);
            			}
            			
            			Toast.makeText(RunnerListActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
    				}else{
    					Toast.makeText(RunnerListActivity.this, "速報中は削除できません", Toast.LENGTH_SHORT).show();
    				}
    			}
    		});
    		
    		// やめるボタン
    		dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// なにもしない
    			}
    		});
    		
    		dialog.show();
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
			
			TextView runnerNameTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_name);
			
			RelativeLayout runnerRelative = (RelativeLayout)convertView.findViewById(R.id.id_runnerinfo_relative_runner);
			TextView runnerNoTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_no);
			TextView runnerSectionTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_section);
			
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
