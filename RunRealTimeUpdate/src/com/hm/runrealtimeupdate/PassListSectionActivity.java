package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseTimeList;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnerInfo;

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
        List<PassPointInfo> passPointInfoList = new ArrayList<PassPointInfo>();
        
        for( DataBaseRunnerInfo info:dbRunnerInfoList ){
        	List<DataBaseTimeList> dBTimeList = DataBaseAccess.getTimeListByRaceIdandNo(getContentResolver(), info.getRaceId(), info.getNumber());
        	
        	// 選手リスト作成
        	ParserRunnerInfo runnerInfo = new ParserRunnerInfo();
        	runnerInfo.setName(info.getName());
			runnerInfo.setNumber(info.getNumber());
			
			for( DataBaseTimeList timelist : dBTimeList){
				ParserRunnerInfo.TimeList timeList = new ParserRunnerInfo().new TimeList();
				
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
			//Collections.reverse(runnerInfo.getTimeList());
			//String point = runnerInfo.getTimeList().get(0).getPoint();
			
			// 現在の通過位置取得
			int passPointNo = runnerInfo.getTimeList().size()-1;
			
			int i = 0;
			for( i= 0; i < passPointInfoList.size(); i++){
				if( passPointInfoList.get(i).getPassPointNo() == passPointNo ){
					passPointInfoList.get(i).getRunnerInfoList().add(runnerInfo);
					break;
				}
			}
			
			if( i == passPointInfoList.size()){
				PassPointInfo passPointInfo = new PassPointInfo();
				passPointInfo.setPassPointNo(passPointNo);
				passPointInfo.getRunnerInfoList().add(runnerInfo);
				passPointInfoList.add(passPointInfo);
			}
        }
        
        Collections.sort(passPointInfoList, new PassPointInfoComparator());
        
        // 地点通過表示リスト作成
        List<PassPoint> passPointList = new ArrayList<PassPoint>();
        
        for( PassPointInfo passPointInfo : passPointInfoList){
        	
        	// 見出し
        	PassPoint passPoint = new PassPoint();
        	int timelistSize = passPointInfo.getRunnerInfoList().get(0).getTimeList().size();
        	passPoint.setPassPoint(passPointInfo.getRunnerInfoList().get(0).getTimeList().get(timelistSize-1).getPoint());
        	
        	passPointList.add(passPoint);
        	
        	// 選手情報
        	for(ParserRunnerInfo runnerInfo : passPointInfo.getRunnerInfoList()){
        		PassPoint passPointRunner = new PassPoint();
        		passPointRunner.setPassPointRunnerMain(runnerInfo.getNumber() + "  " + runnerInfo.getName());
        		passPointRunner.setPassPointRunnerSub(runnerInfo.getTimeList().get(timelistSize-1).getSplit());
        		
        		passPointList.add(passPointRunner);
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
	
	private static class PassPointInfoComparator implements Comparator<PassPointInfo>{

		@Override
		public int compare(PassPointInfo o1, PassPointInfo o2) {
			
			if( o1.getPassPointNo() < o2.getPassPointNo() ){
				return 1;
			}else{
				return -1;
			}
		}
		
	}
	
	private class PassPointInfo {
		
		int passPointNo;
		
		List<ParserRunnerInfo> runnerInfoList = new ArrayList<ParserRunnerInfo>();
		
		public int getPassPointNo() {
			return passPointNo;
		}

		public void setPassPointNo(int passPointNo) {
			this.passPointNo = passPointNo;
		}

		public List<ParserRunnerInfo> getRunnerInfoList() {
			return runnerInfoList;
		}

	}
	
	private class PassPointAdapter extends ArrayAdapter<PassPoint>{
		LayoutInflater inflater;
		
		public PassPointAdapter(Context context, List<PassPoint> objects) {
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
			
			PassPoint passPointRunner = getItem(position);
			
			if(passPointRunner.getPassPoint() == null){
				// ランナー情報表示
				mainTextView.setText(passPointRunner.getPassPointRunnerMain());
				subTextView.setText(passPointRunner.getPassPointRunnerSub());
				subTextView.setVisibility(View.VISIBLE);
				convertView.setBackgroundColor(Color.WHITE);
				
			}else{
				// 見出し 地点情報表示
				mainTextView.setText(passPointRunner.getPassPoint());
				subTextView.setVisibility(View.INVISIBLE);
				convertView.setBackgroundColor(Color.GRAY);
				
			}
			
			return convertView;
		}
	}
	
	private class PassPoint {
		
		/**
		 * 見出し 通過地点、見出しでないときはnullとなる
		 */
		private String passPoint;
		
		private String passPointRunnerMain;
		
		private String passPointRunnerSub;

		public PassPoint(){
			passPoint = null;
			passPointRunnerMain = null;
			passPointRunnerSub = null;
		}
		public String getPassPoint() {
			return passPoint;
		}

		public void setPassPoint(String passPoint) {
			this.passPoint = passPoint;
		}

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
