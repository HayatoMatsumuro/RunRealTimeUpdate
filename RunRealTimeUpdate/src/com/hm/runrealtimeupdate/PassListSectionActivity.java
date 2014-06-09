package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.PassPointInfo;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PassListSectionActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final String STR_INTENT_SECTION = "section";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passlistsection);
		
		Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        String section = intent.getStringExtra(STR_INTENT_SECTION);
        
        // 大会情報取得
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlistsection_txt_racename);
        raceNameTextView.setText(raceInfo.getRaceName());
        
        // 部門名表示
        TextView sectionTextView = (TextView)findViewById(R.id.id_passlistsection_txt_section);
        sectionTextView.setText(section);
        
        // 地点通過情報取得
        List<PassPointInfo> passPointInfoList = Logic.getPassPointInfoList(getContentResolver(), raceId, section);
        
        List<PassPointListElement> passPointList = new ArrayList<PassPointListElement>();
        
        for( PassPointInfo passPointInfo:passPointInfoList){
        	// タイトル
        	PassPointListElement element = new PassPointListElement();
        	element.setSts(PassPointListElement.STR_PASSPOINTLISTELEMENT_TITLE);
        	element.setPoint(passPointInfo.getPoint());
        	passPointList.add(element);
        	
        	// 選手
        	for( PassPointInfo.PassPointRunnerInfo runnerInfo : passPointInfo.getPassPointRunnerInfoList() ){
        		PassPointListElement rElement = new PassPointListElement();
        		rElement.setSts(PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER);
        		rElement.setName(runnerInfo.getName());
        		rElement.setNumber(runnerInfo.getNumber());
        		rElement.setSplit(runnerInfo.getSplit());
        		rElement.setLap(runnerInfo.getLap());
        		rElement.setCurrentTime(runnerInfo.getCurrentTime());
        		passPointList.add(rElement);
        	}
        }
        
        // リストビューの設定
        ListView listView = (ListView)findViewById(R.id.id_passlistsection_listview_sectionlist);
        PassPointAdapter adapter = new PassPointAdapter(this, passPointList);
        listView.setAdapter(adapter);
        
        // 戻るボタン
        Button backButton = (Button)findViewById(R.id.id_passlistsection_btn_back);
        backButton.setTag(raceId);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String raceId = (String)v.getTag();
				
				// 速報リスト画面に遷移
				Intent intent = new Intent( PassListSectionActivity.this, PassListActivity.class);
				intent.putExtra(PassListActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
			}
		});
        return;
	}
	
	
	private class PassPointAdapter extends ArrayAdapter<PassPointListElement>{
		LayoutInflater inflater;
		
		public PassPointAdapter(Context context, List<PassPointListElement> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public boolean isEnabled(int position){
			return false;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_pass_point_runner, parent, false);
			}
			
			TextView mainTextView = (TextView)convertView.findViewById(R.id.id_pass_point_main);
			TextView subTextView = (TextView)convertView.findViewById(R.id.id_pass_point_sub);
			
			PassPointListElement element = getItem(position);
			
			if(element.getSts().equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_TITLE )){
				// 見出し 地点情報表示
				mainTextView.setText(element.getPoint());
				subTextView.setVisibility(View.INVISIBLE);
				convertView.setBackgroundColor(Color.GRAY);
				
			}else{
				// ランナー情報表示
				mainTextView.setText(element.getNumber() + " " + element.getName());
				subTextView.setText(element.getSplit());
				subTextView.setVisibility(View.VISIBLE);
				convertView.setBackgroundColor(Color.WHITE);
			}
			
			return convertView;
		}
	}
	
	private class PassPointListElement{
		
		public static final String STR_PASSPOINTLISTELEMENT_TITLE = "title";
		
		public static final String STR_PASSPOINTLISTELEMENT_RUNNER = "runner";
		
		private String sts;

		private String point;
		
		private String number;
		
		private String name;
		
		private String split;
		
		private String lap;
		
		private String currentTime;
		
		public String getSts() {
			return sts;
		}

		public void setSts(String sts) {
			this.sts = sts;
		}

		public String getPoint() {
			return point;
		}

		public void setPoint(String point) {
			this.point = point;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSplit() {
			return split;
		}

		public void setSplit(String split) {
			this.split = split;
		}

		@SuppressWarnings("unused")
		public String getLap() {
			return lap;
		}

		public void setLap(String lap) {
			this.lap = lap;
		}

		@SuppressWarnings("unused")
		public String getCurrentTime() {
			return currentTime;
		}

		public void setCurrentTime(String currentTime) {
			this.currentTime = currentTime;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}
	}
}
