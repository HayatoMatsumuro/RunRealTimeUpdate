package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.PassRunnerInfo;
import com.hm.runrealtimeupdate.logic.RaceInfo;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PassListSectionActivity extends Activity {

	public static final String STR_ACTIVITY_ID = "passListSectionActivity";
	
	public static final String STR_INTENT_RACEID = "raceid";

	private static final long LONG_RESENT_TIME = 300000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passlistsection);
		
		Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        
        // 大会情報取得
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(PassListSectionActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
        // 地点通過情報取得
        List<PassRunnerInfo> passRunnerInfoList = Logic.getPassRunnerInfoList(getContentResolver(), raceId, LONG_RESENT_TIME);
        
        List<PassPointListElement> passPointList = new ArrayList<PassPointListElement>();
        
        // TODO: タブを切り替えた場合、更新されないのでonResume に移動する必要がある
        for( PassRunnerInfo passRunnerInfo : passRunnerInfoList ){
        	// 部門
        	PassPointListElement element = new PassPointListElement();
        	element.setSts(PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION);
        	element.setSection(passRunnerInfo.getSection());
        	passPointList.add(element);
        	
        	for( PassRunnerInfo.PassPointInfo passPointInfo:passRunnerInfo.getPassPointInfo()){
        		
        		// 地点
            	PassPointListElement pElement = new PassPointListElement();
            	pElement.setSts(PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT);
            	pElement.setPoint(passPointInfo.getPoint());
            	passPointList.add(pElement);
            	
            	// 選手
            	for( PassRunnerInfo.PassPointInfo.PassPointRunnerInfo passPointRunnerInfo : passPointInfo.getPassPointRunnerInfoList() ){
            		PassPointListElement rElement = new PassPointListElement();
            		rElement.setSts(PassPointListElement.STR_PASSPOINTLISTELEMENT_RUNNER);
            		rElement.setName(passPointRunnerInfo.getName());
            		rElement.setNumber(passPointRunnerInfo.getNumber());
            		rElement.setSplit(passPointRunnerInfo.getSplit());
            		rElement.setLap(passPointRunnerInfo.getLap());
            		rElement.setCurrentTime(passPointRunnerInfo.getCurrentTime());
            		rElement.setRecentFlg(passPointRunnerInfo.isRecentFlg());
            		passPointList.add(rElement);
            	}
        	}
        }
        
        // リストビューの設定
        ListView listView = (ListView)findViewById( R.id.id_activity_passlistsection_sectionlist_listview );
        PassPointAdapter adapter = new PassPointAdapter(this, passPointList);
        listView.setAdapter(adapter);
        
        // 戻るボタン
        Button backButton = (Button)findViewById( R.id.id_activity_passlistsection_back_button );
        backButton.setTag(raceId);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String raceId = (String)v.getTag();
				
				// 通過情報画面に遷移
				(( PassActivityGroup )getParent()).showPassListActivity( raceId );
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
			
			RelativeLayout sectionLayout = (RelativeLayout)convertView.findViewById(R.id.id_item_pass_point_runner_section_layout);
			RelativeLayout pointLayout = (RelativeLayout)convertView.findViewById(R.id.id_item_pass_point_runner_point_layout);
			RelativeLayout runnerLayout = (RelativeLayout)convertView.findViewById(R.id.id_item_pass_point_runner_runner_layout);
			
			TextView sectionTextView = (TextView)convertView.findViewById( R.id.id_item_pass_point_runner_section_textview );
			TextView pointTextView = (TextView)convertView.findViewById( R.id.id_item_pass_point_runner_point_textview );
			TextView nameTextView = (TextView)convertView.findViewById( R.id.id_item_pass_point_runner_runner_name_textview );
			TextView splitTextView = (TextView)convertView.findViewById( R.id.id_item_pass_point_runner_runner_split_textview );
			TextView updateNewTextView = (TextView)convertView.findViewById( R.id.id_item_pass_point_runner_runner_updatenew_textview );
			
			PassPointListElement element = getItem(position);
			
			if(element.getSts().equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_SECTION)){
				// 部門表示
				sectionTextView.setText(element.getSection());
				sectionLayout.setVisibility(View.VISIBLE);
				pointLayout.setVisibility(View.GONE);
				runnerLayout.setVisibility(View.GONE);
				
			}else if(element.getSts().equals( PassPointListElement.STR_PASSPOINTLISTELEMENT_POINT )){
				// 地点情報表示
				pointTextView.setText(element.getPoint());
				sectionLayout.setVisibility(View.GONE);
				pointLayout.setVisibility(View.VISIBLE);
				runnerLayout.setVisibility(View.GONE);
				
			}else{
				// ランナー情報表示
				nameTextView.setText(element.getName());
				splitTextView.setText(getString(R.string.str_txt_split) + " " + element.getSplit());
				sectionLayout.setVisibility(View.GONE);
				pointLayout.setVisibility(View.GONE);
				runnerLayout.setVisibility(View.VISIBLE);
				
				if(element.isRecentFlg()){
					updateNewTextView.setVisibility(View.VISIBLE);
				}else{
					updateNewTextView.setVisibility(View.INVISIBLE);
				}
			}
			
			return convertView;
		}
	}
	
	private class PassPointListElement{
		
		public static final String STR_PASSPOINTLISTELEMENT_SECTION = "section";
		
		public static final String STR_PASSPOINTLISTELEMENT_POINT = "point";
		
		public static final String STR_PASSPOINTLISTELEMENT_RUNNER = "runner";
		
		private String sts;

		private String section;
		
		private String point;
		
		private String number;
		
		private String name;
		
		private String split;
		
		private String lap;
		
		private String currentTime;
		
		private boolean recentFlg;

		public String getSts() {
			return sts;
		}

		public void setSts(String sts) {
			this.sts = sts;
		}

		public String getSection() {
			return section;
		}

		public void setSection(String section) {
			this.section = section;
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

		@SuppressWarnings("unused")
		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}
		
		public boolean isRecentFlg() {
			return recentFlg;
		}

		public void setRecentFlg(boolean recentFlg) {
			this.recentFlg = recentFlg;
		}
	}
}
