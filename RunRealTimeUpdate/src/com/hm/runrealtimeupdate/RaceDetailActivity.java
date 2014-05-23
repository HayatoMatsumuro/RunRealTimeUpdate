package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.DataBaseRunnerInfo;

import android.app.Activity;
import android.app.AlertDialog;
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
	
	public static String STR_INTENT_RACEID = "raceid";
	
	/**
	 * 削除するランナーのポジション
	 */
	private RunnerInfoItem m_DeleteRunnerInfoItem;
	
	/**
	 * 選手情報リスト
	 */
	private List<RunnerInfoItem> m_RunnerInfoItemList;
	
	/**
	 * 選手情報アダプタ
	 */
	private RunnerInfoAdapter m_RunnerInfoAdapter;

	/**
	 * 速報フラグ
	 */
	//private String m_UpdateStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racedetail);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);

        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_racedetail_txt_racename);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 大会日
        TextView raceDateTextView = (TextView)findViewById(R.id.id_racedetail_txt_racedate);
        raceDateTextView.setText(dbRaceInfo.getRaceDate());
        
        // 開催地
        TextView raceLocationTextView = (TextView)findViewById(R.id.id_racedetail_txt_racelocation);
        raceLocationTextView.setText(dbRaceInfo.getRaceLocation());
        
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
        List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(getContentResolver(), raceId);
        m_RunnerInfoItemList = new ArrayList<RunnerInfoItem>();
        
        // アダプタ設定
        for( DataBaseRunnerInfo info:dbRunnerInfoList ){
        	RunnerInfoItem item = new RunnerInfoItem();
        	item.setRaceId(info.getRaceId());
        	item.setName(info.getName());
        	item.setNumber(info.getNumber());
        	item.setSection(info.getSection());
        	m_RunnerInfoItemList.add(item);
        }
        m_RunnerInfoAdapter = new RunnerInfoAdapter(this, m_RunnerInfoItemList);
        
        ListView runnerInfoListView = (ListView)findViewById(R.id.id_racedetail_listview_runner);
        runnerInfoListView.setAdapter(m_RunnerInfoAdapter);
        
        // リストのアイテム長押し
        runnerInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
				RunnerInfoItem item = m_RunnerInfoItemList.get(position);
				
				// 削除する選手情報設定
				m_DeleteRunnerInfoItem = item;
				
				// 削除ダイアログ表示
				runnerInfoDeleteDialog(item);
				
				return true;
			}
        	
        });
        
        // 選手登録ボタン
        Button runnerEntryButton = (Button)findViewById(R.id.id_racedetail_btn_runnerentry);
        runnerEntryButton.setTag(raceId);
        runnerEntryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String raceId = (String)v.getTag();
				
				// 選手登録画面遷移
				Intent intent = new Intent(RaceDetailActivity.this, RunnerEntryActivity.class);
				intent.putExtra(RunnerEntryActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
			}
		});
        
        // 速報開始停止ボタン
        Button updateButton = (Button)findViewById(R.id.id_racedetail_btn_updatestartstop);
        
        // 速報開始停止ボタンのタグ設定
        UpdateButtonTag updateButtonTag = new UpdateButtonTag();
        updateButtonTag.setRaceId(raceId);
        updateButtonTag.setUpdateFlg(dbRaceInfo.getUpdateFlg());
        updateButton.setTag(updateButtonTag);
        
        // 速報開始停止ボタンの表示設定
        if( dbRaceInfo.getUpdateFlg().equals(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON) ){
        	updateButton.setText(getString(R.string.str_btn_updatestop));
        } else {
        	updateButton.setText(getString(R.string.str_btn_updatestart));
        	
        	// 他の大会が速報中の場合は、ボタンを無効化する
        	List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getUpdateExeRaceInfo(getContentResolver());
        	if( !dbRaceInfoList.isEmpty() ){
        		updateButton.setEnabled(false);
            };
        }
        
        // 速報開始停止ボタンの処理設定
        updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				UpdateButtonTag updateButtonTag = (UpdateButtonTag)v.getTag();
				
				if( updateButtonTag.getUpdateFlg().equals( DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF )){
					// 速報開始ボタン押し
					
					// データベース変更
					DataBaseAccess.setRaceUpdate(
							getContentResolver(),
							updateButtonTag.getRaceId(),
							DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON);
					
					// 速報開始
					Intent intent = new Intent(RaceDetailActivity.this, UpdateService.class);
					intent.putExtra(UpdateService.STR_INTENT_RACEID, updateButtonTag.getRaceId());
					startService(intent);
					
					// タグ設定
					updateButtonTag.setUpdateFlg(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON);
					v.setTag(updateButtonTag);
					
					// 表示変更
					((Button)v).setText(getString(R.string.str_btn_updatestop));
					
					
				} else {
					// 速報停止ボタン押し
					
					// データベース変更
					DataBaseAccess.setRaceUpdate(
							getContentResolver(),
							updateButtonTag.getRaceId(),
							DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
					
					// TODO: 速報停止
					
					// タグ設定
					updateButtonTag.setUpdateFlg(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
					v.setTag(updateButtonTag);
					
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
				intent.putExtra( UpdateListActivity.STR_INTENT_RACEID, raceId );
				
				startActivity(intent);
			}
		});
	}
	
	private void runnerInfoDeleteDialog( RunnerInfoItem runnerInfoItem ){
		
		// ダイアログ表示
		AlertDialog.Builder dialog = new AlertDialog.Builder(RaceDetailActivity.this);
		dialog.setTitle(getString(R.string.str_dialog_title_deleterunner));
		dialog.setMessage(createDialogMessage(runnerInfoItem));
		
		// 削除するボタン
		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// 選手削除
				DataBaseAccess.deleteRunnerInfoByNo(getContentResolver(), m_DeleteRunnerInfoItem.getRaceId(), m_DeleteRunnerInfoItem.getNumber());
				
				// リストから選手情報を削除する
				m_RunnerInfoItemList.remove(m_DeleteRunnerInfoItem);
				m_RunnerInfoAdapter.remove(m_DeleteRunnerInfoItem);
				
				// 表示リストを更新する
				m_RunnerInfoAdapter.notifyDataSetChanged();
				
				Toast.makeText(RaceDetailActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
			}
		});
		
		// やめるボタン
		dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自動生成されたメソッド・スタブ
			}
		});
		
		dialog.show();

	}
	
	/**
	 * ダイアログのメッセージを作成する
	 * @param runnerInfoItem
	 * @return
	 */
	private String createDialogMessage( RunnerInfoItem runnerInfoItem ){
		StringBuilder builder = new StringBuilder();
		builder.append(runnerInfoItem.getName());
		builder.append("\n");
		builder.append(runnerInfoItem.getNumber());
		builder.append("\n");
		builder.append(runnerInfoItem.getSection());
		
		return builder.toString();
	}
	
	private class RunnerInfoAdapter extends ArrayAdapter<RunnerInfoItem>{

		LayoutInflater inflater;
    	
		public RunnerInfoAdapter(Context context, List<RunnerInfoItem> objects) {
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
			
			RunnerInfoItem item = getItem(position);
				
			runnerNameTextView.setText(item.getName());
			runnerNoTextView.setText(item.getNumber());
			runnerSectionTextView.setText(item.getSection());
			
			return convertView;
		}
		
	}
	
	private class RunnerInfoItem extends DataBaseRunnerInfo{
		
	}
	
	private class UpdateButtonTag {
		
		private String raceId;

		private String updateFlg;
		
		public String getRaceId() {
			return raceId;
		}

		public void setRaceId(String raceId) {
			this.raceId = raceId;
		}

		public String getUpdateFlg() {
			return updateFlg;
		}

		public void setUpdateFlg(String updateFlg) {
			this.updateFlg = updateFlg;
		}
	}
}
