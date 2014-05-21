package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.DataBaseUpdateData;

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

	public static String STR_INTENT_RACEID = "raceid";
	
	private UpdateDataAdapter m_UpdateDataAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
		
		// 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);
		
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_updatelist_txt_racename);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 速報データ取得
        List<DataBaseUpdateData> updateDataList = DataBaseAccess.getUpdateDataByRaceId(getContentResolver(), raceId);
        
        // リストの表示データ設定
        List<UpdateDataDisp> updateDataDispList = new ArrayList<UpdateDataDisp>();
        
        for(DataBaseUpdateData dbUpdateData : updateDataList){
        	UpdateDataDisp disp = new UpdateDataDisp();
        	String mainStr = dbUpdateData.getNumber() + " " + dbUpdateData.getSection() + " "+ dbUpdateData.getName() + " 選手 " + dbUpdateData.getPoint();
        	String subStr = dbUpdateData.getSplit();
        	disp.setMainStr(mainStr);
        	disp.setSubStr(subStr);
        	
        	updateDataDispList.add(disp);
        }
        
        Collections.reverse(updateDataDispList);
        
        // リストビュー設定
        m_UpdateDataAdapter = new UpdateDataAdapter(this, updateDataDispList);
        
        ListView updateListView = (ListView)findViewById(R.id.id_updatelist_listview_runner);
        updateListView.setAdapter(m_UpdateDataAdapter);
        
        // 大会詳細ボタン
        Button raceDetailButton = (Button)findViewById(R.id.id_updatelist_btn_detail);
        raceDetailButton.setTag(raceId);
        raceDetailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String raceId = (String)v.getTag();
				
				// 大会詳細画面遷移
				Intent intent = new Intent(UpdateListActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
				
			}
		});
        return;
	}
	
	private class UpdateDataAdapter extends ArrayAdapter<UpdateDataDisp>{
		
		LayoutInflater inflater;
		
		public UpdateDataAdapter(Context context, List<UpdateDataDisp> objects) {
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
			
			UpdateDataDisp disp = getItem(position);
			
			mainTextView.setText(disp.getMainStr());
			subTextView.setText(disp.getSubStr());
			
			return convertView;
		}
		
		
	}
	
	private class UpdateDataDisp{
		
		private String mainStr;
		
		private String subStr;

		public String getMainStr() {
			return mainStr;
		}

		public void setMainStr(String mainStr) {
			this.mainStr = mainStr;
		}

		public String getSubStr() {
			return subStr;
		}

		public void setSubStr(String subStr) {
			this.subStr = subStr;
		}
		
	}
}
