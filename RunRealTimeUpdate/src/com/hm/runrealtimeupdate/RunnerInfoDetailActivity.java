package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RunnerInfoDetailActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final String STR_INTENT_NUMBER = "number";

	public static final String STR_ACTIVITY_ID = "RunnerInfoDetailActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runnerinfodetail);
		
		Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        String number = intent.getStringExtra(STR_INTENT_NUMBER);
        
        RunnerInfo runnerInfo = Logic.getRunnerInfo(getContentResolver(), raceId, number);
        
        // ゼッケン番号
     	TextView numberTextView = (TextView)findViewById(R.id.id_runnerinfodetail_txt_number);
     	numberTextView.setText("No. " + runnerInfo.getNumber());
     	
     	// 選手名
     	TextView nameTextView = (TextView)findViewById(R.id.id_runnerinfodetail_txt_name);
     	nameTextView.setText(runnerInfo.getName());
     		
     	// 部門
     	TextView sectionTextView = (TextView)findViewById(R.id.id_runnerinfodetail_txt_section);
     	sectionTextView.setText(runnerInfo.getSection());
     	
		
		// タイムリスト
		TableLayout tableLayout = (TableLayout)findViewById(R.id.id_runnerinfodetail_table);
		
		for( RunnerInfo.TimeList timelist : runnerInfo.getTimeList() ){
			TableRow tableRow = new TableRow(this);
			
			// 地点
			TextView pointTextView = new TextView( this );
			pointTextView.setText(timelist.getPoint());
			
			// スプリット
			TextView splitTextView = new TextView( this );
			splitTextView.setText(timelist.getSplit());
			splitTextView.setGravity(Gravity.CENTER);
			
			// ラップ
			TextView lapTextView = new TextView( this );
    		lapTextView.setText(timelist.getLap());
    		lapTextView.setGravity(Gravity.CENTER);
    		
    		// カレントタイム
    		TextView currentTimeView = new TextView( this );
    		currentTimeView.setText(timelist.getCurrentTime());
    		currentTimeView.setGravity(Gravity.CENTER);
			
    		tableRow.addView(pointTextView);
    		tableRow.addView(splitTextView);
    		tableRow.addView(lapTextView);
    		tableRow.addView(currentTimeView);
    		
    		tableLayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
		}
		
	}

}
