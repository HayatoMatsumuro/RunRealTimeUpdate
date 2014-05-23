package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	/**
	 * 登録できる大会の最大数
	 */
	private static final int INT_RACEINFO_NUM_MAX = 5;
	
	/**
	 * 大会情報リスト
	 */
	private List<RaceInfoItem> m_RaceInfoList;
	
	/**
	 * 大会情報アダプタ
	 */
	private RaceInfoAdapter m_RaceInfoAdapter;
	
	/**
	 * 削除する大会ID
	 */
	private RaceInfoItem m_DeleteRaceInfoItem = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 大会情報
        List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getAllRaceInfo(getContentResolver());
        m_RaceInfoList = new ArrayList<RaceInfoItem>();
        
        // アダプタ設定
        for( DataBaseRaceInfo dbRaceInfo:dbRaceInfoList){
        	RaceInfoItem item = new RaceInfoItem();
        	item.setRaceId(dbRaceInfo.getRaceId());
        	item.setRaceName(dbRaceInfo.getRaceName());
        	item.setRaceDate(dbRaceInfo.getRaceDate());
        	item.setRaceLocation(dbRaceInfo.getRaceLocation());
        	item.setUpdateFlg(dbRaceInfo.getUpdateFlg());
        	
        	if( dbRaceInfo.getUpdateFlg().equals(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON) ){
        		item.setUpdateStr(getString(R.string.str_txt_updateexe));
        	}else{
        		item.setUpdateStr("");
        	}
        	m_RaceInfoList.add(item);
        }
        m_RaceInfoAdapter = new RaceInfoAdapter( this, m_RaceInfoList);
        ListView raceInfoListView = (ListView)findViewById(R.id.id_main_listview_race);
        raceInfoListView.setAdapter(m_RaceInfoAdapter);
        
        // リストのアイテム短押し
        raceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				RaceInfoItem item = m_RaceInfoList.get(position);
				Intent intent = new Intent( MainActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, item.getRaceId());
				startActivity(intent);
			}
		});
        
        // リストのアイテム長押し
        raceInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
				RaceInfoItem item = m_RaceInfoList.get(position);
				
				// 削除する大会ID設定
				m_DeleteRaceInfoItem = item;
				
				// 削除ダイアログ表示
				raceInfoDeleteDialog(item);
				
				return true;
			}
		});
        // 大会登録ボタン
        
        Button entryBtn = (Button)findViewById(R.id.id_main_btn_entry);
        entryBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会登録画面遷移
				Intent intent = new Intent(MainActivity.this, RaceEntryActivity.class);
				startActivity(intent);
			}
		});
        
        // ボタン状態更新
		entryButtonEnabled();
    }

    private void raceInfoDeleteDialog( RaceInfoItem raceInfoItem ){
    	// ダイアログ表示
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(getString(R.string.str_dialog_title_deleterace));
		dialog.setMessage(createDialogMessage(raceInfoItem));
		
		// 削除するボタン
		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// 大会削除
				DataBaseAccess.deleteRaceInfoByRaceId(getContentResolver(), m_DeleteRaceInfoItem.getRaceId());
				
				//　リストから大会情報削除
				m_RaceInfoList.remove(m_DeleteRaceInfoItem);

				// 表示リストを更新
				m_RaceInfoAdapter.notifyDataSetChanged();
				
				// ボタン状態更新
				entryButtonEnabled();
				
				Toast.makeText(MainActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
				
				//TODO: 速報中ならタイマーを停止するもしくは削除を禁止にする
				
				//TODO: 紐付く選手情報も削除する
			}

		});
		
		//　やめるボタン
		dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
		});
		
		dialog.show();
    }

    private String createDialogMessage( RaceInfoItem raceInfoItem ){
    	StringBuilder builder = new StringBuilder();
    	builder.append(getString(R.string.str_dialog_msg_name));
		builder.append("\n");
		builder.append(raceInfoItem.getRaceName());
		builder.append("\n");
		
		return builder.toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private class RaceInfoAdapter extends ArrayAdapter<RaceInfoItem>{

    	LayoutInflater inflater;
    	
		public RaceInfoAdapter(Context context, List<RaceInfoItem> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_raceinfo, parent, false);
			}
			
			TextView raceNameTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racename);
			TextView raceDateTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racedate);
			TextView raceLocationTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racelocation);
			TextView raceUpdateTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_update);
			
			RaceInfoItem item = getItem(position);
			
			raceNameTextView.setText(item.getRaceName());
			raceDateTextView.setText(item.getRaceDate());
			raceLocationTextView.setText(item.getRaceLocation());
			raceUpdateTextView.setText(item.getUpdateStr());
			
			return convertView;
			
		}
    	
    }
    private class RaceInfoItem extends DataBaseRaceInfo{
    	
    	private String updateStr;

		public String getUpdateStr() {
			return updateStr;
		}

		public void setUpdateStr(String updateStr) {
			this.updateStr = updateStr;
		}
    }
    
    private void entryButtonEnabled(){
    	Button btn = (Button)findViewById(R.id.id_main_btn_entry);
    	if( m_RaceInfoList.size() >= INT_RACEINFO_NUM_MAX){
    		btn.setEnabled(false);
    	}else{
    		btn.setEnabled(true);
    	}
    }
}
