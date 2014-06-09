package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class RaceDetailActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	/**
	 * 登録できる選手の数
	 */
	private static int INT_RUNNER_NUM_MAX = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racedetail);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);

        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_racedetail_txt_racename);
        raceNameTextView.setText(raceInfo.getRaceName());
        
        // 大会日
        TextView raceDateTextView = (TextView)findViewById(R.id.id_racedetail_txt_racedate);
        raceDateTextView.setText(raceInfo.getRaceDate());
        
        // 開催地
        TextView raceLocationTextView = (TextView)findViewById(R.id.id_racedetail_txt_racelocation);
        raceLocationTextView.setText(raceInfo.getRaceLocation());
        
        // 戻るボタン
        Button backButton = (Button)findViewById(R.id.id_racedetail_btn_back);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// メイン画面遷移
				Intent intent = new Intent(RaceDetailActivity.this, MainActivity.class);
				startActivity(intent);
				
			}
		});
        
        // 選手情報
        List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList(getContentResolver(), raceInfo.getRaceId());
        
        // 選手リスト設定
        ListView runnerInfoListView = (ListView)findViewById(R.id.id_racedetail_listview_runner);
        RunnerListAdapter adapter = new RunnerListAdapter(this, runnerInfoList);
        runnerInfoListView.setAdapter(adapter);
        
        // 選手リストのアイテム長押し
        runnerInfoListView.setTag(raceInfo);
        runnerInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				// 大会情報取得
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getTag();
				
				// 選手情報取得
				RunnerInfo runnerInfo = (RunnerInfo)listView.getItemAtPosition(position);
				
				// アダプタを取得する
				RunnerListAdapter adapter = ( RunnerListAdapter )listView.getAdapter();
				
				// 削除ダイアログ表示
				RunnerDeleteDialog dialog
						= new RunnerDeleteDialog(
								RaceDetailActivity.this,
								getContentResolver(),
								raceInfo,
								runnerInfo,
								adapter,
								(Button)findViewById(R.id.id_racedetail_btn_runnerentry));
				dialog.onDialog();
				
				return true;
			}
        	
        });
        
        // 選手登録ボタン
        Button runnerEntryButton = (Button)findViewById(R.id.id_racedetail_btn_runnerentry);
        runnerEntryButton.setTag(raceInfo);
        runnerEntryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				
				// 選手登録画面遷移
				Intent intent = new Intent(RaceDetailActivity.this, RunnerEntryActivity.class);
				intent.putExtra(RunnerEntryActivity.STR_INTENT_RACEID, raceInfo.getRaceId());
				startActivity(intent);
			}
		});
        
        // 選手登録ボタンのフォーカス設定
        if( runnerInfoList.size() >= INT_RUNNER_NUM_MAX){
        	runnerEntryButton.setEnabled(false);
        }else{
        	runnerEntryButton.setEnabled(true);
        }
        
        // 速報開始停止ボタン
        Button updateButton = (Button)findViewById(R.id.id_racedetail_btn_updatestartstop);
        
        // 速報開始停止ボタンの表示設定
        RaceInfo updateRaceInfo = Logic.getUpdateRaceId( getContentResolver());
        
        if( updateRaceInfo == null ){
        	// 速報中の大会なし
        	updateButton.setText(getString(R.string.str_btn_updatestart));
        	updateButton.setEnabled(true);
        }else if( updateRaceInfo.getRaceId().equals(raceInfo.getRaceId())){
        	// 選択中の大会IDと速報中の大会が一致
        	updateButton.setText(getString(R.string.str_btn_updatestop));
        	updateButton.setEnabled(true);
        }else{
        	// 他の大会が速報中
        	updateButton.setText(getString(R.string.str_btn_updatestart));
        	updateButton.setEnabled(false);
        }
        
        // 速報開始停止ボタンの処理設定
        updateButton.setTag(raceInfo);
        updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 速報が自動停止した場合は速報が停止しているが表示は「速報停止」となっている
				// 動作的に問題ないため、そのままとする
				RaceInfo raceInfo = ( RaceInfo )v.getTag();
				
				if( !raceInfo.isRaceUpdate() ){
					// 速報開始ボタン押し
					
					// 大会を速報状態にする
					Logic.setUpdateOnRaceId(getContentResolver(), raceInfo.getRaceId());
					
					raceInfo.setRaceUpdate(true);
					
					// 速報開始
					Intent intent = new Intent(RaceDetailActivity.this, UpdateService.class);
					intent.putExtra(UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId());
					startService(intent);
					
					// 表示変更
					((Button)v).setText(getString(R.string.str_btn_updatestop));
					
					
				} else {
					// 速報停止ボタン押し
					
					// データベース変更
					Logic.setUpdateOffRaceId(getContentResolver(), raceInfo.getRaceId());

					raceInfo.setRaceUpdate(false);
					
					// 速報停止
					Intent intent = new Intent(RaceDetailActivity.this, UpdateService.class);
					intent.putExtra(UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId());
					stopService(intent);
					
					// 表示変更
					((Button)v).setText(getString(R.string.str_btn_updatestart));
				}
			}
		});
        
        // 速報リストボタン
        Button updatelistButton = (Button)findViewById(R.id.id_racedetail_btn_updatelist);
        updatelistButton.setTag(raceId);
        updatelistButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String raceId = (String)v.getTag();
				
				Intent intent = new Intent( RaceDetailActivity.this, UpdateListActivity.class );
				intent.putExtra(UpdateListActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
			}
		});
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
    	private Context m_Context;
    	
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
    	private RunnerInfo m_RunnerInfo;
    	
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
    	 * @param contentResolver コンテントリゾルバ
    	 * @param raceInfo 大会情報
    	 * @param runnerInfo 選手情報
    	 * @param adapter 大会リストアダプタ
    	 * @param button 登録ボタンのリソースID
    	 */
    	RunnerDeleteDialog( Context context, ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo, RunnerListAdapter adapter, Button button ){
    		
    		// 初期化
    		m_Context = context;
    		m_ContentResolver = contentResolver;
    		m_RaceInfo = raceInfo;
    		m_RunnerInfo = runnerInfo;
    		m_Adapter = adapter;
    		m_EntryButton = button;
    		
    	}
    	
    	public void onDialog(){
    		// ダイアログ表示
    		AlertDialog.Builder dialog = new AlertDialog.Builder( m_Context );
    		dialog.setTitle( getString( R.string.str_dialog_title_deleterunner ) );
    		dialog.setMessage( createDialogMessage( m_RunnerInfo ) );
    		
    		// 削除するボタン
    		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {
    					
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				
    				// 選手削除
        			Logic.deleteRunnerInfo( m_ContentResolver, m_RaceInfo.getRaceId(), m_RunnerInfo.getNumber() );
        				
        			// 表示リストを更新する
        			if( m_Adapter != null ){
            			m_Adapter.remove(m_RunnerInfo);
            			m_Adapter.notifyDataSetChanged();
        			}
        				
        			// 削除したら選手登録はできるので、ボタンを有効にする
        			if( m_EntryButton != null ){
            			m_EntryButton.setEnabled(true);
        			}
        			
        			Toast.makeText(RaceDetailActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
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
	private class RunnerListAdapter extends ArrayAdapter<RunnerInfo>{

		LayoutInflater inflater;
    	
		public RunnerListAdapter(Context context, List<RunnerInfo> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_runnerinfo, parent, false);
			}
			
			TextView runnerNameTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_name);
			TextView runnerNoTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_no);
			TextView runnerSectionTextView = (TextView)convertView.findViewById(R.id.id_runnerinfo_txt_section);
			
			RunnerInfo item = getItem(position);
				
			runnerNameTextView.setText(item.getName());
			runnerNoTextView.setText(item.getNumber());
			runnerSectionTextView.setText(item.getSection());
			
			return convertView;
		}
		
	}
}
