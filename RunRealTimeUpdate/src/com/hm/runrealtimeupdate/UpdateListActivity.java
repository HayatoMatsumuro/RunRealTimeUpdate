package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.UpdateInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateListActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
		
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_updatelist_txt_racename);
        raceNameTextView.setText(Logic.getSelectRaceInfo().getRaceName());
        
        // 速報データ取得
        List<UpdateInfo> updateInfoList = Logic.getUpdateInfoList(getContentResolver());
        
        // リストの表示データ設定
        //List<UpdateInfo> updateInfoList = new ArrayList<UpdateInfo>();
        //
        /*
        for(DataBaseUpdateData dbUpdateData : updateDataList){
        	UpdateDataDisp disp = new UpdateDataDisp();
        	String mainStr = dbUpdateData.getNumber() + " " + dbUpdateData.getSection() + " "+ dbUpdateData.getName() + " 選手 " + dbUpdateData.getPoint();
        	String subStr = dbUpdateData.getSplit();
        	disp.setMainStr(mainStr);
        	disp.setSubStr(subStr);
        	
        	updateDataDispList.add(disp);
        }*/
        
        // リストビュー設定
        UpdateDataAdapter adapter = new UpdateDataAdapter(this, updateInfoList);
        
        ListView updateListView = (ListView)findViewById(R.id.id_updatelist_listview_runner);
        updateListView.setAdapter(adapter);
        
        // 大会詳細ボタン
        Button raceDetailButton = (Button)findViewById(R.id.id_updatelist_btn_detail);
        raceDetailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 大会詳細画面遷移
				Intent intent = new Intent(UpdateListActivity.this, RaceDetailActivity.class);
				startActivity(intent);
				
			}
		});
        
        // 通過情報ボタン
        Button passListButton = (Button)findViewById(R.id.id_updatelist_btn_passlist);
        passListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 通過情報画面遷移
				Intent intent = new Intent(UpdateListActivity.this, PassListActivity.class);
				intent.putExtra(PassListActivity.STR_INTENT_RACEID, Logic.getSelectRaceInfo().getRaceId());
				startActivity(intent);
				
			}
		});
        return;
	}
	
	private class UpdateDataAdapter extends ArrayAdapter<UpdateInfo>{
		
		LayoutInflater inflater;
		
		public UpdateDataAdapter(Context context, List<UpdateInfo> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_updatedata, parent, false);
			}
			
			TextView mainTextView = (TextView)convertView.findViewById(R.id.id_updatedata_txt_main);
			TextView subTextView = (TextView)convertView.findViewById(R.id.id_updatedata_txt_sub);
			
			
        	
			UpdateInfo updateInfo = getItem(position);
			String mainStr = updateInfo.getNumber() + " " + updateInfo.getSection() + " "+ updateInfo.getName() + " 選手 " + updateInfo.getPoint();
        	String subStr = updateInfo.getSplit();
			mainTextView.setText(mainStr);
			subTextView.setText(subStr);
			
			return convertView;
		}
		
		
	}
}
