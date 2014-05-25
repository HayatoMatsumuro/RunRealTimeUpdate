package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.DataBaseTimeList;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PassListSectionActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final String STR_INTENT_SECTION = "section";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activite_passlistsection);
		
		Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        String section = intent.getStringExtra(STR_INTENT_SECTION);
        
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);
        
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlistsection_txt_racename);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 部門名表示
        TextView sectionTextView = (TextView)findViewById(R.id.id_passlistsection_txt_section);
        sectionTextView.setText(section);
        
        // 部門の選手リストを取得
        List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceIdandSection(getContentResolver(), raceId, section);
        
        // 地点情報リスト作成
        List<PassPoint> passPointList = new ArrayList<PassPoint>();
        
        for( DataBaseRunnerInfo info:dbRunnerInfoList ){
        	List<DataBaseTimeList> dBTimeList = DataBaseAccess.getTimeListByRaceIdandNo(getContentResolver(), info.getRaceId(), info.getNumber());
        	
        	// 選手リスト作成
        	RunnerInfo runnerInfo = new RunnerInfo();
        	runnerInfo.setName(info.getName());
			runnerInfo.setNumber(info.getNumber());
			
			for( DataBaseTimeList timelist : dBTimeList){
				RunnerInfo.TimeList timeList = new RunnerInfo().new TimeList();
				
				timeList.setPoint(timelist.getPoint());
				timeList.setSplit(timelist.getSplit());
				timeList.setLap(timelist.getLap());
				timeList.setCurrentTime(timelist.getCurrentTime());
				runnerInfo.addTimeList(timeList);
			}
			
			// タイムリストが空の場合は、処理を行わない
			if( runnerInfo.getTimeList().isEmpty()){
				continue;
			}
			
			// 最新の通過位置取得
			Collections.reverse(runnerInfo.getTimeList());
			String point = runnerInfo.getTimeList().get(0).getPoint();
						
			PassPointRunner passPointRunner = new PassPointRunner();
			passPointRunner.setPassPointRunnerMain(runnerInfo.getNumber() + "  " + runnerInfo.getName());
			passPointRunner.setPassPointRunnerSub(runnerInfo.getTimeList().get(0).getSplit());
			
			int i = 0;
			for(i= 0; i < passPointList.size(); i++){
				
				if( passPointList.get(i).getPassPoint().equals(point)){
					passPointList.get(i).getPassPointRunnerList().add(passPointRunner);
					break;
				}
			}
			
			if( i == passPointList.size()){
				PassPoint passPoint = new PassPoint();
				passPoint.setPassPoint(point);
				passPoint.getPassPointRunnerList().add(passPointRunner);
				passPointList.add(passPoint);
			}
        }
        
        // リストビューの設定
        ListView listView = (ListView)findViewById(R.id.id_passlistsection_listview_sectionlist);
        PassPointAdapter adapter = new PassPointAdapter(this, passPointList);
        listView.setAdapter(adapter);
        
        return;
	}
	
	
	private class PassPointAdapter extends ArrayAdapter<PassPoint>{

		LayoutInflater inflater;
		
		public PassPointAdapter(Context context, List<PassPoint> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_pass_point, parent, false);
			}
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.id_pass_point_txt_title);
			ListView listView = (ListView)convertView.findViewById(R.id.id_pass_point_listview_pointlist);
			
			PassPoint passPoint = getItem(position);
			
			titleTextView.setText(passPoint.getPassPoint());
			PassPointRunnerAdapter adapter = new PassPointRunnerAdapter(PassListSectionActivity.this, passPoint.getPassPointRunnerList());
			listView.setAdapter(adapter);
			
			// リストビューの高さ設定
			
			//listView.setLayoutParams(new  LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, lvWrapperHeight)););
			return convertView;
		}
	}
	private class PassPointRunnerAdapter extends ArrayAdapter<PassPointRunner>{
		LayoutInflater inflater;
		
		public PassPointRunnerAdapter(Context context, List<PassPointRunner> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_pass_point_runner, parent, false);
			}
			
			TextView mainTextView = (TextView)convertView.findViewById(R.id.id_pass_point_runner_main);
			TextView subTextView = (TextView)convertView.findViewById(R.id.id_pass_point_runner_sub);
			
			PassPointRunner passPointRunner = getItem(position);
			
			mainTextView.setText(passPointRunner.getPassPointRunnerMain());
			subTextView.setText(passPointRunner.getPassPointRunnerSub());
			
			return convertView;
		}
	}
	private class PassPoint {
		
		private String passPoint;

		private List<PassPointRunner> passPointRunnerList = new ArrayList<PassPointRunner>();
		
		public String getPassPoint() {
			return passPoint;
		}

		public void setPassPoint(String passPoint) {
			this.passPoint = passPoint;
		}

		public List<PassPointRunner> getPassPointRunnerList() {
			return passPointRunnerList;
		}
	}
	
	private class PassPointRunner {
		
		private String passPointRunnerMain;
		
		private String passPointRunnerSub;

		public String getPassPointRunnerMain() {
			return passPointRunnerMain;
		}

		public void setPassPointRunnerMain(String passPointRunnerMain) {
			this.passPointRunnerMain = passPointRunnerMain;
		}

		public String getPassPointRunnerSub() {
			return passPointRunnerSub;
		}

		public void setPassPointRunnerSub(String passPointRunnerSub) {
			this.passPointRunnerSub = passPointRunnerSub;
		}
	}
}
